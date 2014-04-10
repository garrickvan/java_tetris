package tetris.graphics;

import java.awt.Image;
/**
	游戏中的元素角色
 **/
public class Sprite {

    protected Animation anim;
    // 位置 (单位：像素)
    protected float x;
    protected float y;
    // 速率 (单位：像素/秒)
    protected float dx;
    protected float dy;

    /**
        通过指定的动画创建一个新的游戏角色
    */
    public Sprite(Animation anim) {
        this.anim = anim;
    }

    /**
       更新根据指定的速率游戏角色的动画及其位置
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
        取得游戏角色的X坐标
    */
    public float getX() {
        return x;
    }

    /**
        取得游戏角色的Y坐标
    */
    public float getY() {
        return y;
    }

    /**
        设置游戏角色的X坐标
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        设置游戏角色的Y坐标
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        根据每个动画帧里面的图像，获取游戏角色的宽
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        根据每个动画帧里面的图像，获取游戏角色的宽
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        获取游戏角色的水平速率(像素/秒)
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        获取游戏角色的垂直速率(像素/秒)
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        设置游戏角色的水平速率(像素/秒)
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        设置游戏角色的垂直速率(像素/秒)
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        获取当前游戏角色帧的图像
    */
    public Image getImage() {
        return anim.getImage();
    }
}
