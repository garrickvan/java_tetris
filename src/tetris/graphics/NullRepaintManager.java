package tetris.graphics;

import javax.swing.RepaintManager;
import javax.swing.JComponent;

/**
    无重绘管理器，去掉JFrame的重绘管理器，使得屏幕重绘完全被主线程控制，防止闪屏
*/
public class NullRepaintManager extends RepaintManager {

    /**
        安装无重绘管理器
    */
    public static void install() {
        RepaintManager repaintManager = new NullRepaintManager();
        repaintManager.setDoubleBufferingEnabled(false);
        RepaintManager.setCurrentManager(repaintManager);
    }
	
	//清空所有的组件绘画
    public void addInvalidComponent(JComponent c) {}

    public void addDirtyRegion(JComponent c, int x, int y,
        int w, int h){}

    public void markCompletelyDirty(JComponent c) {}

    public void paintDirtyRegions() {}

}
