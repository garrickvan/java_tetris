package tetris.input;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
    该类负责管理游戏中用户的一切输入实现游戏中与用户交护动
	包括键盘输入和鼠标输入,还添加了用户自定义按键的功能,所
	有处理部分将跳过AWT线程,直接在main方法的线程中处理,提高
	游戏程序对玩家控制的反应,使其反应及时准确。
*/
public class InputManager implements KeyListener, MouseListener,
    MouseMotionListener, MouseWheelListener
{
    /**
        无形鼠标光标
    */
    public static final Cursor INVISIBLE_CURSOR =
        Toolkit.getDefaultToolkit().createCustomCursor(
            Toolkit.getDefaultToolkit().getImage(""),
            new Point(0,0),
            "invisible");

    // 自定义鼠标键码值
    public static final int MOUSE_MOVE_LEFT = 0;
    public static final int MOUSE_MOVE_RIGHT = 1;
    public static final int MOUSE_MOVE_UP = 2;
    public static final int MOUSE_MOVE_DOWN = 3;
    public static final int MOUSE_WHEEL_UP = 4;
    public static final int MOUSE_WHEEL_DOWN = 5;
    public static final int MOUSE_BUTTON_1 = 6;
    public static final int MOUSE_BUTTON_2 = 7;
    public static final int MOUSE_BUTTON_3 = 8;
	
	//鼠标值数组的最大长度
    private static final int NUM_MOUSE_CODES = 9;

    //键盘码值使用java.awt.event.keyEvent内默认的键值,即便是一些非常罕见的按键，如调音键其键值也同样是在600的范围内
	//所以键盘值数组的最大长度为600
    private static final int NUM_KEY_CODES = 600;
	
	//游戏活动中的键盘值数组
    private GameAction[] keyActions =
        new GameAction[NUM_KEY_CODES];
	//游戏活动中的鼠标值数组
    private GameAction[] mouseActions =
        new GameAction[NUM_MOUSE_CODES];
	
	//鼠标坐标
    private Point mouseLocation;
	//屏幕中点坐标
    private Point centerLocation;
	//组件对象
    private Component comp;
	//测试器
    private Robot robot;
	//鼠标的重定位控制
    private boolean isRecentering;

    /**
        构造器：对指定的组件创建一个输入管理
    */
    public InputManager(Component comp) {
        this.comp = comp;
        mouseLocation = new Point();
        centerLocation = new Point();

        //为组件组册所有监听
        comp.addKeyListener(this);
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.addMouseWheelListener(this);

        //去掉组件对聚焦键盘值的独占，使得系统的Tap+Alt等键能正常使用，这是全屏游戏暂停并退出所必须的
        comp.setFocusTraversalKeysEnabled(false);
    }


    /**
        为组件重新设置光标
    */
    public void setCursor(Cursor cursor) {
        comp.setCursor(cursor);
    }


    /**
        设置鼠标关联模式，将特定的键值关联到鼠标的活动中构成对应的游戏行为的映射
		该模式开启时，通过遥控类Robot将鼠标固定在屏幕的中心
    */
    public void setRelativeMouseMode(boolean mode) {
        if (mode == isRelativeMouseMode()) {
            return;
        }

        if (mode) {
            try {
                robot = new Robot();
                recenterMouse();
            }
            catch (AWTException ex) {
                robot = null;
            }
        }
        else {
            robot = null;
        }
    }


    /**
        返回鼠标关联模式是否开启成功
    */
    public boolean isRelativeMouseMode() {
        return (robot != null);
    }


    /**
        映射游戏活动中指定的键该键的键值使用java.awt.KeyEvent
		中默认的键值，如果键值已经被映射，那么将覆盖掉原来的
		游戏按键行为。
    */
    public void mapToKey(GameAction gameAction, int keyCode) {
        keyActions[keyCode] = gameAction;
    }


    /**
        映射游戏活动中指定的鼠标按键值，该键值为本类中已经设定的值
		注意每次被修改都会覆盖掉原来的游戏按键行为
    */
    public void mapToMouse(GameAction gameAction,
        int mouseCode)
    {
        mouseActions[mouseCode] = gameAction;
    }


    /**
        清除同一游戏按键行为的键值映射
    */
    public void clearMap(GameAction gameAction) {
        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] == gameAction) {
                keyActions[i] = null;
            }
        }

        for (int i=0; i<mouseActions.length; i++) {
            if (mouseActions[i] == gameAction) {
                mouseActions[i] = null;
            }
        }
		//重启游戏行为
        gameAction.reset();
    }


    /**
        获取对应的游戏行为的所有的键值，包括鼠标和键盘的映射
		以字符链的形式返回结果
    */
    public List getMaps(GameAction gameCode) {
        ArrayList list = new ArrayList();

        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] == gameCode) {
                list.add(getKeyName(i));
            }
        }

        for (int i=0; i<mouseActions.length; i++) {
            if (mouseActions[i] == gameCode) {
                list.add(getMouseName(i));
            }
        }
        return list;
    }


    /**
        重设所有的按键的游戏行为，
    */
    public void resetAllGameActions() {
        for (int i=0; i<keyActions.length; i++) {
            if (keyActions[i] != null) {
                keyActions[i].reset();
            }
        }

        for (int i=0; i<mouseActions.length; i++) {
            if (mouseActions[i] != null) {
                mouseActions[i].reset();
            }
        }
    }


    /**
        获取键值对应的按键名
    */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }


    /**
        获取鼠标值对应的鼠标名
    */
    public static String getMouseName(int mouseCode) {
        switch (mouseCode) {
            case MOUSE_MOVE_LEFT: return "Mouse Left";
            case MOUSE_MOVE_RIGHT: return "Mouse Right";
            case MOUSE_MOVE_UP: return "Mouse Up";
            case MOUSE_MOVE_DOWN: return "Mouse Down";
            case MOUSE_WHEEL_UP: return "Mouse Wheel Up";
            case MOUSE_WHEEL_DOWN: return "Mouse Wheel Down";
            case MOUSE_BUTTON_1: return "Mouse Button 1";
            case MOUSE_BUTTON_2: return "Mouse Button 2";
            case MOUSE_BUTTON_3: return "Mouse Button 3";
            default: return "Unknown mouse code " + mouseCode;
        }
    }


    /**
        获取鼠标的X坐标
    */
    public int getMouseX() {
        return mouseLocation.x;
    }


    /**
        获取鼠标的Y坐标
    */
    public int getMouseY() {
        return mouseLocation.y;
    }


    /**
		使用摇控类来定位鼠标到屏幕的中点，但是不是所有的系统平台都支持该操作
		(Windows,Linux支持)
    */
    private synchronized void recenterMouse() {
        if (robot != null && comp.isShowing()) {
            centerLocation.x = comp.getWidth() / 2;
            centerLocation.y = comp.getHeight() / 2;
            SwingUtilities.convertPointToScreen(centerLocation,
                comp);
            isRecentering = true;
            robot.mouseMove(centerLocation.x, centerLocation.y);
        }
    }

	//获取当前按键的对应的游戏按键行为
    private GameAction getKeyAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keyActions.length) {
            return keyActions[keyCode];
        }
        else {
            return null;
        }
    }

    /**
		获取当前的鼠标活动对应的键值
    */
    public static int getMouseButtonCode(MouseEvent e) {
         switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                return MOUSE_BUTTON_1;
            case MouseEvent.BUTTON2:
                return MOUSE_BUTTON_2;
            case MouseEvent.BUTTON3:
                return MOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

	/**
		获取当前的鼠标活动对应的游戏行为
	 **/
    private GameAction getMouseButtonAction(MouseEvent e) {
        int mouseCode = getMouseButtonCode(e);
        if (mouseCode != -1) {
             return mouseActions[mouseCode];
        }
        else {
             return null;
        }
    }


    //实现键盘监听接口
    public void keyPressed(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
        //不让按键在被其它程序接收
        e.consume();
    }


    //实现键盘监听接口
    public void keyReleased(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
        //不让按键在被其它程序接收
        e.consume();
    }


    //实现键盘监听接口
    public void keyTyped(KeyEvent e) {
        //不让按键在被其它程序接收
        e.consume();
    }


    //实现鼠标监听接口
    public void mousePressed(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
    }


    //实现鼠标监听接口
    public void mouseReleased(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
    }


    //实现鼠标监听接口
    public void mouseClicked(MouseEvent e) {}


    //实现鼠标监听接口
    public void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }


    //实现鼠标监听接口
    public void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }


    //实现鼠标活动监听接口
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }


    //实现鼠标活动监听接口
    public synchronized void mouseMoved(MouseEvent e) {
        //如果鼠标在屏幕中间就取消屏幕恢复到中间的状态
		if (isRecentering &&
            centerLocation.x == e.getX() &&
            centerLocation.y == e.getY())
        {
            isRecentering = false;
        }
		//如果不在中间就获取得鼠标的坐标并计算其移动的值
        else {
            int dx = e.getX() - mouseLocation.x;
            int dy = e.getY() - mouseLocation.y;
            mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
            mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

            if (isRelativeMouseMode()) {
                recenterMouse();
            }
        }

        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();

    }


    //实现鼠标滚轮事件的监听
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN,
            e.getWheelRotation());
    }
	
	//鼠标行为辅助判定
    private void mouseHelper(int codeNeg, int codePos,
        int amount)
    {
        GameAction gameAction;
        if (amount < 0) {
            gameAction = mouseActions[codeNeg];
        }
        else {
            gameAction = mouseActions[codePos];
        }
        if (gameAction != null) {
            gameAction.press(Math.abs(amount));
            gameAction.release();
        }
    }

}

