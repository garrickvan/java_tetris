package tetris.input;

/**
    该类将游戏中的输入行为抽像化，修正了按键行为(包括键盘及
	鼠标)在实际游戏	中对应按键的意义，使输入管理程序通过此类
	来控制游戏中存在的像移动跳跃对应的应返回的按键数
*/
public class GameAction {

    /**
        普通行为,接受所有的amount按键计数
    */
    public static final int NORMAL = 0;
    /**
        只接受第按行为，该行为模式只接受由isPressed()返回的第一次接受的
		按键而忽略，玩家未松开该键所返回的acoumt键数
    */
    public static final int DETECT_INITAL_PRESS_ONLY = 1;
	//松开状态
    private static final int STATE_RELEASED = 0;
	//被按状态
    private static final int STATE_PRESSED = 1;
	//等待松开状态
    private static final int STATE_WAITING_FOR_RELEASE = 2;
	//行为名称
    private String name;
	//行为字段的变量
    private int behavior;
	//按键计数器
    private int amount;
	//状态字段变量
    private int state;

    /**
        创建普通行为的按键状态
    */
    public GameAction(String name) {
        this(name, NORMAL);
    }


    /**
        创建由程序指定的按键行为
    */
    public GameAction(String name, int behavior) {
        this.name = name;
        this.behavior = behavior;
        reset();
    }


    /**
        获取游戏按键行为的名称(名称是由程序或玩家按实际情况定义的)
    */
    public String getName() {
        return name;
    }


    /**
        重设游戏按键的行为状态，可以用作吸收掉玩家的按键响应
    */
    public void reset() {
        state = STATE_RELEASED;
        amount = 0;
    }


    /**
        击键行为，相当于按下+松开次按一个按键
    */
    public synchronized void tap() {
        press();
        release();
    }


    /**
        发送被按信号
    */
    public synchronized void press() {
        press(1);
    }


    /**
        根据当前的按键状态修正接受的按键次数或者移动指定的距离
    */
    public synchronized void press(int amount) {
		//当按键处于等待松开的状态时，忽略掉继续被接受的按键数直到状态改变
		//如果不是，就计算按键次数并改变状态
        if (state != STATE_WAITING_FOR_RELEASE) {
            this.amount+=amount;
            state = STATE_PRESSED;
        }

    }


    /**
        松开时将按键改为松开状态
    */
    public synchronized void release() {
        state = STATE_RELEASED;
    }


    /**
        返回通过检查最后一次按键计数器的记录是否为
		非零得知的按键是否按过
    */
    public synchronized boolean isPressed() {
        return (getAmount() != 0);
    }


    /**
        根据不同的按键状态返回最后一次被检查的按键次数
    */
    public synchronized int getAmount() {
        int retVal = amount;
        if (retVal != 0) {
            if (state == STATE_RELEASED) {
                amount = 0;
            }
            else if (behavior == DETECT_INITAL_PRESS_ONLY) {
                state = STATE_WAITING_FOR_RELEASE;
                amount = 0;
            }
        }
        return retVal;
    }
}
