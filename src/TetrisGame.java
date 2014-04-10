import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import tetris.graphics.*;
import tetris.input.*;
import tetris.sound.*;
import tetris.sound.util.*;
import tetris.gameelements.*;
import javax.sound.midi.*;

import javax.sound.sampled.*;

/**
    启动游戏的主类
*/
public class TetrisGame {
	
	//屏幕字体
    protected static final int FONT_SIZE = 24;
	
	//供屏幕管理选择的屏幕分辨率
    private static final DisplayMode POSSIBLE_MODES[] = {
        new DisplayMode(1024, 768, 24, 0),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(800, 600, 16, 0),
        new DisplayMode(800, 600, 32, 0),
        new DisplayMode(800, 600, 24, 0),
    };
    
    //声音播放采样率44100MHZ,16-bit,单声道,带符号,小端存储
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private boolean isRunning;
    private ScreenManager screen;
    private InputManager inputManager;
    private SoundManager soundManager;
	
	//游戏按键
	private GameAction	singleGame_btn;
	private GameAction	doubleGame_btn;
	private GameAction	A_turn;
	private GameAction	A_left;
	private GameAction	A_right;
	private GameAction	A_down;
	private GameAction	B_turn;
	private GameAction	B_left;
	private GameAction	B_right;
	private GameAction	B_down;
	private GameAction	pause;
	private GameAction	goback;
	private GameAction	changeWindow;

	private JFrame normalFrame;

	//游戏中心
	GameCenter		gameCenter;
	GameRenderer	gameRenderer;
	GameSence		gameSence;
	
	//游戏暂停
    private boolean paused;

	/**
		构造方法
	 **/

	 public TetrisGame(){
		run();
	 }

    /**
        停止游戏
    */
    public void stop() {
        isRunning = false;
    }


    /**
        运行游戏
    */
    public void run() {
        try {
            init();
            gameLoop();
        }
        finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }

    /**
        游戏退出的守护线程，为确保完全退出游戏
    */
    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // 现等待虚拟机退出
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException ex) { }
                // 如果线程还在运行就强制退出
                System.exit(0);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    /**
        设置全屏及程序初始化
    */
    public void init() {
		
		screenInit();
		
		inputManagerInit();
		
		soundManagerInit();
		
        NullRepaintManager.install();
		
        isRunning = true;

		initGame();
        
    }
	/**
		游戏初始化
	**/
	public void initGame(){
		
		gameSence		= new GameSence(screen);
		gameCenter		= new GameCenter(gameSence);
		gameRenderer	= new GameRenderer();
		
	}
	//屏幕管理初时化
	public void screenInit(){
        //全屏画面
        screen = new ScreenManager();
        DisplayMode displayMode =
            screen.findFirstCompatibleMode(POSSIBLE_MODES);
        screen.setFullScreen(displayMode);

        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.LIGHT_GRAY);
        window.setForeground(Color.white);
        window.setAlwaysOnTop(true);
	}
	
	//输入管理初始化
	public void inputManagerInit(){
		
		Component com= screen.getFullScreenWindow();
		inputManager = new InputManager(com);

		
		//游戏动作声明
		singleGame_btn	 = new GameAction("singleGame_btn",GameAction.DETECT_INITAL_PRESS_ONLY);
		doubleGame_btn	 = new GameAction("doubleGame_btn",GameAction.DETECT_INITAL_PRESS_ONLY);
		A_turn			 = new GameAction("A_turn",GameAction.DETECT_INITAL_PRESS_ONLY);
		A_left			 = new GameAction("A_left",GameAction.DETECT_INITAL_PRESS_ONLY);
		A_right			 = new GameAction("A_right",GameAction.DETECT_INITAL_PRESS_ONLY);
		A_down			 = new GameAction("A_down",GameAction.DETECT_INITAL_PRESS_ONLY);

		B_turn			 = new GameAction("B_turn",GameAction.DETECT_INITAL_PRESS_ONLY);
		B_left			 = new GameAction("B_left",GameAction.DETECT_INITAL_PRESS_ONLY);
		B_right			 = new GameAction("B_right",GameAction.DETECT_INITAL_PRESS_ONLY);
		B_down			 = new GameAction("B_down",GameAction.DETECT_INITAL_PRESS_ONLY);

		pause			 = new GameAction("pause",GameAction.DETECT_INITAL_PRESS_ONLY);
		goback			 = new GameAction("goback",GameAction.DETECT_INITAL_PRESS_ONLY);
		changeWindow		 = new GameAction("changeWindow",GameAction.DETECT_INITAL_PRESS_ONLY);
		
		//映射按键到指定的键盘键
		inputManager.mapToKey(singleGame_btn, KeyEvent.VK_1);
		inputManager.mapToKey(doubleGame_btn, KeyEvent.VK_2);

		inputManager.mapToKey(A_turn, KeyEvent.VK_W);
		inputManager.mapToKey(A_left, KeyEvent.VK_A);
		inputManager.mapToKey(A_right, KeyEvent.VK_D);
		inputManager.mapToKey(A_down, KeyEvent.VK_S);
		inputManager.mapToKey(A_down, KeyEvent.VK_SPACE);

		
		inputManager.mapToKey(B_turn, KeyEvent.VK_UP);
		inputManager.mapToKey(B_left, KeyEvent.VK_LEFT);
		inputManager.mapToKey(B_right, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(B_down, KeyEvent.VK_DOWN);
		inputManager.mapToKey(B_down, KeyEvent.VK_ENTER);

		
		inputManager.mapToKey(pause, KeyEvent.VK_P);
		inputManager.mapToKey(goback, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(changeWindow, KeyEvent.VK_F1);
		
	}
	
	//声音管理初始化
	
    private MidiPlayer midiPlayer;
    private Sequence music1;
    private Sequence music2;
    private Sequence music3;
    
	public void soundManagerInit(){
		
		soundManager = new SoundManager(PLAYBACK_FORMAT);
        midiPlayer = new MidiPlayer();
        music1 = midiPlayer.getSequence("sounds/backgroundMusic1.mid");
        music2 = midiPlayer.getSequence("sounds/backgroundMusic2.mid");
        music3 = midiPlayer.getSequence("sounds/backgroundMusic3.mid");
		midiPlayer.play(music1, true);
	}

    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }


    /**
        游戏死循环，直到被按暂停
    */
    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (isRunning) {
            long elapsedTime =
                System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            // 更新游戏
            update(elapsedTime);

            // 重绘画面
            
            Graphics2D g = screen.getGraphics();
			if(g==null&&normalFrame==null){
				normalFrame=creatNormalFrame();
	            java.awt.image.BufferStrategy strategy = normalFrame.getBufferStrategy();
                g=(Graphics2D)strategy.getDrawGraphics();
				draw(g);
				g.dispose();
			}else{
				if(normalFrame!=null)
					normalFrame=null;
				draw(g);
				g.dispose();
				screen.update();				
			}
            
            try{ 
            	Thread.sleep(20);
            }catch(Exception e){
            
            }
        }
    }


    /**
        更新游戏状态
    */
    public void update(long elapsedTime) {
        
		checkSystemInput();

        if (!paused) {
			checkInput();
			gameCenter.update(elapsedTime);
			if(gameSence.isExit())
				stop();
        }

    }


    /**
        重绘画面
    */
    public void draw(Graphics2D g){
		gameRenderer.draw(g,gameSence);
    }
    
	/**
		检查系统输入
	**/
    public void checkSystemInput() {
        if (pause.isPressed())
            setPaused(!paused);
	}

	/**
		检查输入
	**/
	public void checkInput(){
		
        	
        	
		if(singleGame_btn.isPressed()){
			gameCenter.singleGame();
			midiPlayer.stop();
			midiPlayer.play(music2, true);
		}
		
		if(doubleGame_btn.isPressed()){
			gameCenter.doubleGame();
			midiPlayer.stop();
			midiPlayer.play(music3, true);
		}
			

		if(A_turn.isPressed())
			gameCenter.A_turn();
		
		if(A_left.isPressed())
			gameCenter.A_left();
		
		if(A_right.isPressed())
			gameCenter.A_right();
		
		if(A_down.isPressed())
			gameCenter.A_down();
		
		if(B_turn.isPressed())
			gameCenter.B_turn();
		
		if(B_left.isPressed())
			gameCenter.B_left();
		
		if(B_right.isPressed())
			gameCenter.B_right();		
		
		if(B_down.isPressed())
			gameCenter.B_down();

        if (goback.isPressed()){
			if(gameCenter.getSenceState()==GameSence.INMENU)
				stop();
			else{
				gameCenter.toMenu();
				midiPlayer.stop();
				midiPlayer.play(music1, true);
			}
					
        }	
        if (changeWindow.isPressed())
            screen.restoreScreen();

	}

	/**
		游戏暂停
	**/
    public void setPaused(boolean p) {
        if (paused != p) {
            this.paused = p;
            inputManager.resetAllGameActions();
        }
    }

    //主线程
    public static void main(String [] args){
    	new TetrisGame();
    }

	/**
		创建正常窗体
	**/
	public JFrame creatNormalFrame(){
		
		JFrame frame = new JFrame();
		//设置退出程序后窗口默认关闭
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//去掉JFrame的边框装饰
        frame.setUndecorated(true);
		//关掉JFrame的重绘监听线程，不再接受用户调用的重绘事件，以提高绘图性能
        frame.setIgnoreRepaint(true);
		frame.setSize(1024,768);
		frame.createBufferStrategy(2);
        frame.setResizable(false);

		return frame;
	}
}
