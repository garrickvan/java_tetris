package tetris.gameelements.blocks;

import java.util.*;

public class FallingBlock{
	
	//该下落块包含的所有子块
	private ArrayList<BaseBlock> blocks;

	//下落块的颜色
	private int color;	

	//所有子块的横竖相对坐标系
	private int coords[][];

	//下落速度
	private float fallSpeed;
	
	private boolean isCursing;


	//下落块的类型
	private int type;
	
	//构造方法
	public FallingBlock(ArrayList<BaseBlock> blocks,int coords[][],int type){
		
		this.blocks=blocks;
		this.color=this.blocks.get(0).getBlockColor();
		this.coords=coords;
		this.isCursing=false;
		this.type=type;
	}	
	
	//获取该下落块包含的所有子块
	public ArrayList<BaseBlock> getBlocks(){
		return blocks;
	}

	/**
		获取相对坐标
	**/
	public int[][] getCoords(){
		return coords;
	}

	//获取下落速度
	public float getFallSpeed(){
		return fallSpeed;
	}
	
	//设置下落块速度
	public void setFallSpeed(float fallSpeed){
		this.fallSpeed=fallSpeed;
		for(BaseBlock b:blocks)
			b.setVelocityY(fallSpeed);
	}
	
	/*
	 *设置冻结
	 */
	public void setCuring(){ 
		
		this.isCursing=true;
		for(BaseBlock b:blocks)
			b.setCured();
	}
	
	//下落块左移
	public void moveLeft(){
		for(BaseBlock b:blocks)
			b.setX(b.getX()-b.WIDTH);
	}

	//下落块左移
	public void moveRight(){
		for(BaseBlock b:blocks)
			b.setX(b.getX()+b.WIDTH);
	}

	/**
		下落块旋转(只做顺时针旋转)
	 **/
	public void turn(){
		
		int blocksNum=blocks.size();
		int i=0;
		int tempCoords[][]=new int[blocksNum][2];  //变换后的相对坐标系统

		for(i=0;i<blocksNum;i++){
			tempCoords[i][0]=-coords[i][1];//根据公式：X1=-Y0;变换坐标
			tempCoords[i][1]=coords[i][0];//根据公式：Y1=X0;变换坐标
		}
		//刷新所有子块的坐标
		int tempX;
		int tempY;
		i=0;
		for(BaseBlock b:blocks){
			tempX=tempCoords[i][0]-coords[i][0];
			tempY=tempCoords[i][1]-coords[i][1];

			b.setX(b.getX()+tempX*b.WIDTH);
			b.setY(b.getY()+tempY*b.HEIGHT);
			i++;
		}

		//更新当前的相对坐标系
		coords=tempCoords;
	}
		
	/**
		克隆下落块
	**/
	public Object clone(){	
		
		ArrayList<BaseBlock> b=blocks;

		return new FallingBlock(b,coords,type);

	}

	/**
		获取当前块的颜色
	**/
	public int getColor(){
		return color;	
	}
	/**
		获取当前块的类型
	**/
	public int getType(){
		return type;
	}
}
