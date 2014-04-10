package tetris.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

/**
    该屏幕管理类用来处理屏幕的初始设置和全屏独占模式的切换
*/
public class ScreenManager {

	//图像驱动类
    private GraphicsDevice device;

    /**
        创建一个新的图像驱动
    */
    public ScreenManager() {
        GraphicsEnvironment environment =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
    }


    /**
        通过图像驱动返回本机可兼容的一系列显示模式
    */
    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }


    /**
        返回被本机兼容的显示模式中的的第一个模式
    */
    public DisplayMode findFirstCompatibleMode(DisplayMode modes[]){
        
		DisplayMode goodModes[] = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            for (int j = 0; j < goodModes.length; j++) {
                if (displayModesMatch(modes[i], goodModes[j])) {
                    return modes[i];
                }
            }

        }
		//万一没有被支持的显示模式就返回null值
        return null;
    }


    /**
        返回正在使用的显示模式
    */
    public DisplayMode getCurrentDisplayMode() {
        return device.getDisplayMode();
    }


    /**
        通过各个字段值的比较检查mode1是否被mode2兼容
    */
    public boolean displayModesMatch(DisplayMode mode1,DisplayMode mode2){
		//如果被选的mode1的宽和高与mode2不同则表明不能匹配，返回false
        if (mode1.getWidth() != mode2.getWidth() ||
            mode1.getHeight() != mode2.getHeight())
        {
            return false;
        }
		//先检查mode1和mode2是否与显示模式中的位深的合法值，再两者比较
        if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            mode1.getBitDepth() != mode2.getBitDepth())
        {
            return false;
        }
		//比较刷新率的方法类似上面
        if (mode1.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            mode2.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            mode1.getRefreshRate() != mode2.getRefreshRate())
         {
             return false;
         }

         return true;
    }


    /**
		进入全屏模式状态。
        如果被用户指定的显示模式不能被本机的显卡兼容
		或者本机的系统的显示模式不能被程序改变时，
		就使用当前的显示模式进入全屏状态
		
		此全屏模式的绘图策略采用双页面交换缓冲策略
    */
    public void setFullScreen(DisplayMode displayMode) {
        //使用全屏模式的处理Window被定死为JFrame
		final JFrame frame = new JFrame();
		//设置退出程序后窗口默认关闭
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//去掉JFrame的边框装饰
        frame.setUndecorated(true);
		//关掉JFrame的重绘监听线程，不再接受用户调用的重绘事件，以提高绘图性能
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);
		//通过device对frame开启全屏模式
        device.setFullScreenWindow(frame);

        if (displayMode != null &&
            device.isDisplayChangeSupported())
        {
            try {
                device.setDisplayMode(displayMode);
            }
            catch (IllegalArgumentException ex) { }
            //重新调整窗口大小以适应全屏大小
            frame.setSize(displayMode.getWidth(),
                displayMode.getHeight());
        }
        //在此操作是为了避免潜在的死锁出现
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    //创建双页面交换缓冲策略
					frame.createBufferStrategy(2);
                }
            });
        }
		//忽略两个有可能的异常
        catch (InterruptedException ex) {}
        catch (InvocationTargetException  ex) {}
    }


    /**
        返回全屏模式的绘图上下文(graphics2D)
		由于采用了双页面缓冲，务必要记得掉用
		update()方法去显示图片
		
		还要记住要销毁Graphics2D的引用，以便垃圾回收器及时回收资源
    */
    public Graphics2D getGraphics() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            return (Graphics2D)strategy.getDrawGraphics();
        }
        else {
            return null;
        }
    }


    /**
		更新显示
    */
    public void update() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        //同步显示避免出现程序运行时出现错误
        //这种错误一般在linux系统的eventqueue常见
		Toolkit.getDefaultToolkit().sync();
    }


    /**
		返回正在使用的全屏模式的JFrame
    */
    public JFrame getFullScreenWindow() {
        return (JFrame)device.getFullScreenWindow();
    }


    /**
        返回全屏模式的宽，如果模式启动失败，返回的将是0值
    */
    public int getWidth() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getWidth();
        }
        else {
            return 0;
        }
    }


    /**
        返回全屏模式的高，如果模式启动失败，返回的将是0值
    */
    public int getHeight() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getHeight();
        }
        else {
            return 0;
        }
    }


    /**
		恢复窗口模式
    */
    public void restoreScreen() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            window.dispose();
        }
        device.setFullScreenWindow(null);
    }


    /**
		创建当前窗口的缓冲图像
    */
    public BufferedImage createCompatibleImage(int w, int h,
        int transparancy)
    {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            GraphicsConfiguration gc =
                window.getGraphicsConfiguration();
            return gc.createCompatibleImage(w, h, transparancy);
        }
        return null;
    }
}
