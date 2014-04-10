package tetris.sound;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import tetris.sound.util.ThreadPool;
import tetris.sound.util.LoopingByteInputStream;


/**
	音频管理器，负责游戏中的声音线程分配，及游戏声音的播放，暂停及游戏暂停
	时全局声音的暂停。
*/
public class SoundManager extends ThreadPool {

    private AudioFormat playbackFormat;
    private ThreadLocal localLine;
    private ThreadLocal localBuffer;
    private Object pausedLock;
    private boolean paused;

    /**
        新建一个音频管理器，使用当前格式下本机支持的最大混频源数作为总线程数
    */
    public SoundManager(AudioFormat playbackFormat) {
        this(playbackFormat,
            getMaxSimultaneousSounds(playbackFormat));
    }


    /**
        创建一个指定线程数量的音频管理器
    */
    public SoundManager(AudioFormat playbackFormat,
        int maxSimultaneousSounds)
    {
        super(Math.min(maxSimultaneousSounds,
            getMaxSimultaneousSounds(playbackFormat)));
        this.playbackFormat = playbackFormat;
        localLine = new ThreadLocal();
        localBuffer = new ThreadLocal();
        pausedLock = new Object();
		//唤醒所有的线程，使其就绪
        synchronized (this) {
            notifyAll();
        }
    }


    /**
        获取指定格式下，本机支持的最大混频源数
    */
    public static int getMaxSimultaneousSounds(
        AudioFormat playbackFormat)
    {
        DataLine.Info lineInfo = new DataLine.Info(
            SourceDataLine.class, playbackFormat);
        Mixer mixer = AudioSystem.getMixer(null);
        return mixer.getMaxLines(lineInfo);
    }


    /**
        清理所有的混频器
    */
    protected void cleanUp() {
        // 设置暂停状态
        setPaused(false);

        // 清理混频器，停止一切播放的声音
        Mixer mixer = AudioSystem.getMixer(null);
        if (mixer.isOpen()) {
            mixer.close();
        }
    }


    public void close() {
        cleanUp();
        super.close();
    }


    public void join() {
        cleanUp();
        super.join();
    }


    /**
        设置声音暂停状态，但有时声音不会马上就暂停
    */
    public void setPaused(boolean paused) {
        if (this.paused != paused) {
            synchronized (pausedLock) {
                this.paused = paused;
                if (!paused) {
                    // 重新启动声音
                    pausedLock.notifyAll();
                }
            }
        }
    }


    /**
        测试当前是否处于暂停状态
    */
    public boolean isPaused() {
        return paused;
    }


    /**
        获取指定路径的音频采样，如果路径不存在，返回null.
    */
    public Sound getSound(String filename) {
        return getSound(getAudioInputStream(filename));
    }


    /**
        获取指定输入流的音频采样，如果流不存在，返回null.
    */
    public Sound getSound(InputStream is) {
        return getSound(getAudioInputStream(is));
    }


    /**
        Loads a Sound from an AudioInputStream.
    */
    public Sound getSound(AudioInputStream audioStream) {
        if (audioStream == null) {
            return null;
        }

        // get the number of bytes to read
        int length = (int)(audioStream.getFrameLength() *
            audioStream.getFormat().getFrameSize());

        // read the entire stream
        byte[] samples = new byte[length];
        DataInputStream is = new DataInputStream(audioStream);
        try {
            is.readFully(samples);
            is.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        // return the samples
        return new Sound(samples);
    }


    /**
        Creates an AudioInputStream from a sound from the file
        system.
    */
    public AudioInputStream getAudioInputStream(String filename) {
        try {
            return getAudioInputStream(
                new FileInputStream(filename));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
        Creates an AudioInputStream from a sound from an input
        stream
    */
    public AudioInputStream getAudioInputStream(InputStream is) {

        try {
            if (!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            // open the source stream
            AudioInputStream source =
                AudioSystem.getAudioInputStream(is);

            // convert to playback format
            return AudioSystem.getAudioInputStream(
                playbackFormat, source);
        }
        catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
        Plays a sound. This method returns immediately.
    */
    public InputStream play(Sound sound) {
        return play(sound, null, false);
    }


    /**
        Plays a sound with an optional SoundFilter, and optionally
        looping. This method returns immediately.
    */
    public InputStream play(Sound sound, SoundFilter filter,
        boolean loop)
    {
        InputStream is;
        if (sound != null) {
            if (loop) {
                is = new LoopingByteInputStream(
                    sound.getSamples());
            }
            else {
                is = new ByteArrayInputStream(sound.getSamples());
            }

            return play(is, filter);
        }
        return null;
    }


    /**
        Plays a sound from an InputStream. This method
        returns immediately.
    */
    public InputStream play(InputStream is) {
        return play(is, null);
    }


    /**
        Plays a sound from an InputStream with an optional
        sound filter. This method returns immediately.
    */
    public InputStream play(InputStream is, SoundFilter filter) {
        if (is != null) {
            if (filter != null) {
                is = new FilteredSoundStream(is, filter);
            }
            runTask(new SoundPlayer(is));
        }
        return is;
    }


    /**
        Signals that a PooledThread has started. Creates the
        Thread's line and buffer.
    */
    protected void threadStarted() {
        // wait for the SoundManager constructor to finish
        synchronized (this) {
            try {
                wait();
            }
            catch (InterruptedException ex) { }
        }

        // use a short, 100ms (1/10th sec) buffer for filters that
        // change in real-time
        int bufferSize = playbackFormat.getFrameSize() *
            Math.round(playbackFormat.getSampleRate() / 10);

        // create, open, and start the line
        SourceDataLine line;
        DataLine.Info lineInfo = new DataLine.Info(
            SourceDataLine.class, playbackFormat);
        try {
            line = (SourceDataLine)AudioSystem.getLine(lineInfo);
            line.open(playbackFormat, bufferSize);
        }
        catch (LineUnavailableException ex) {
            // the line is unavailable - signal to end this thread
            Thread.currentThread().interrupt();
            return;
        }

        line.start();

        // create the buffer
        byte[] buffer = new byte[bufferSize];

        // set this thread's locals
        localLine.set(line);
        localBuffer.set(buffer);
    }


    /**
        Signals that a PooledThread has stopped. Drains and
        closes the Thread's Line.
    */
    protected void threadStopped() {
        SourceDataLine line = (SourceDataLine)localLine.get();
        if (line != null) {
            line.drain();
            line.close();
        }
    }


    /**
        The SoundPlayer class is a task for the PooledThreads to
        run. It receives the threads's Line and byte buffer from
        the ThreadLocal variables and plays a sound from an
        InputStream.
        <p>This class only works when called from a PooledThread.
    */
    protected class SoundPlayer implements Runnable {

        private InputStream source;

        public SoundPlayer(InputStream source) {
            this.source = source;
        }

        public void run() {
            // get line and buffer from ThreadLocals
            SourceDataLine line = (SourceDataLine)localLine.get();
            byte[] buffer = (byte[])localBuffer.get();
            if (line == null || buffer == null) {
                // the line is unavailable
                return;
            }

            // copy data to the line
            try {
                int numBytesRead = 0;
                while (numBytesRead != -1) {
                    // if paused, wait until unpaused
                    synchronized (pausedLock) {
                        if (paused) {
                            try {
                                pausedLock.wait();
                            }
                            catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    // copy data
                    numBytesRead =
                        source.read(buffer, 0, buffer.length);
                    if (numBytesRead != -1) {
                        line.write(buffer, 0, numBytesRead);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
