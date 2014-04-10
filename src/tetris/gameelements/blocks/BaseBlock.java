package tetris.gameelements.blocks;

import tetris.graphics.*;

public class BaseBlock extends Sprite{
	
	//基础块的颜色
	
	public static final int BULUE	 =0;
	public static final int RED		 =1;
	public static final int YELLOW	 =2;
	public static final int GREEN	 =3;
	public static final int	PURPLE	 =4;
	
	public static final int SIDELINE 	=1;
	//基础块的高和宽
	
	public static  int INWIDTH	 	=28;
	public static  int INHEIGHT		=28;
	
	public static  int WIDTH;
	public static  int HEIGHT;
	
	//空格线
	
	//当前颜色
	private int blockColor;

	//是否被固定
	private boolean isCuring =false;
	
	public BaseBlock(Animation aim,int blockColor){
		super(aim);
		this.blockColor=blockColor;
	}
	
    /**
       更新根据指定的速率游戏角色的动画及其位置
    */
    public void update(long elapsedTime) {
        if(!isCuring){
			y += dy * elapsedTime;
			x += dx * elapsedTime;
        }
        anim.update(elapsedTime);
    }

	public int getBlockColor(){
		return blockColor;
	}

	public boolean isCured(){
		return isCuring;
	}

	public void setCured(){
		isCuring=true;
	}

	public int getWidth(){
		return this.WIDTH;
	}

	public int getHeight(){
		return this.HEIGHT;
	}
}
