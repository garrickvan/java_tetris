package tetris.gameelements;

import java.awt.*;
import java.util.*;
import tetris.graphics.*;
import tetris.gameelements.blocks.*;

/**
	下落场景容器,对下落块及进行管理
 **/

 public class Sence{
	
	//碰撞状体
	public static final int NONE		=0;
	public static final int HITBOTTOM	=1;
	public static final int HITOTHER	=2;
	public static final int HITSIDE		=3;
	public static final int HITTOP		=4;

	//容器的左上角及右下角坐标
	private Point leftPoint;
	private Point rightPoint;
    //行数和列数
	public static final int ROW=18;
	public static final int RANK=10;
	//场景容器：基础块二维数组
	private BaseBlock blockStack[][] =new BaseBlock[ROW][RANK];

	//积分
	private	long score ;

	//游戏时间
	private StringBuffer gameTime=new StringBuffer();
	private long sumTime;

	//下落速度
	private float fallSpeed;
	private float baseSpeed;

	//容器号码
	public int ID;
	
	//总ID
	private static int SUMID;
	
	//下落块
	private FallingBlock fallingBlock;
	
	//缓冲块
	private FallingBlock bufferBlock;

	//下落块生成器
	private BlockMaker blockMaker;
	
	//碰撞状态
	private boolean isCured;
	private boolean isHitBottom;
	private boolean isHitTop;
	private boolean isGameOver;
	private boolean isUpdate;
	private boolean haveBuffer;

	/**
		构造方法
	 **/
	public Sence(int X,int Y){
		
		leftPoint 	= new Point(X,Y);
		rightPoint 	= new Point(X+BaseBlock.WIDTH*RANK,Y+BaseBlock.HEIGHT*ROW);
		init();
		initGame();
	}

	/**
		数据初始化
	 **/
	public void init(){
		
		SUMID++;
		ID=SUMID;
		blockMaker	= new BlockMaker();
	}
	
	/**
		get方法
	 **/
	public boolean haveBufferBlock(){
		return haveBuffer;
	}

	public void getBuffer(){
		this.haveBuffer=false;
	}
	
	public BaseBlock[][] getBlockStack(){
		return blockStack;
	}
	
	public StringBuffer getGemeTime(){
		return gameTime;
	}
	
	public long getScore(){
		return score;
	}
	
	public float getFallSpeed(){
		return fallSpeed;
	}
	
	public FallingBlock getFallingBlock(){
		return fallingBlock;
	}
	
	public boolean isHitBottom(){
		return isHitBottom;
	}
	
	public boolean isGameOver(){
		return isGameOver;
	}
	
	public Point getLeftPoint(){
		return leftPoint;
	}
	
	public Point getRightPoint(){
		return rightPoint;	
	}

	public FallingBlock getBufferBlock(){
		return bufferBlock;	
	}


	/**
		set下落块速度
	 **/
	public void setFallSpeed(float arg){
		this.fallSpeed=arg;
		this.baseSpeed=arg;
	}
	
	/**
		初始化游戏
	 **/
	public void initGame(){
		
		if(gameTime.length()==0){
			gameTime.append("00:00:00");
		}else
			gameTime.replace(0,gameTime.length(),"00:00:00");
		isHitBottom=false;
		isHitTop=false;
		isGameOver=false;
		isUpdate=false;
		leve=0;
		counTime=0;
		sumTime=0;
		score=0;
		bufferBlock=blockMaker.makeFallingBlocks();
		initFallBlock();
	}

	/**
		初始化下落块
	 **/
	public void initFallBlock(){
		
		fallingBlock=blockMaker.makeOrder(bufferBlock.getType(),bufferBlock.getColor());
		bufferBlock=null;
		bufferBlock=blockMaker.makeFallingBlocks();
		haveBuffer=true;
		for(BaseBlock b:fallingBlock.getBlocks()){
			b.setX(b.getX()+leftPoint.x+b.WIDTH*(RANK/2));
			b.setY(b.getY()+leftPoint.y-b.HEIGHT);
		}
		fallSpeed=baseSpeed;
		isCured=false;
	}

	/**
		下落块左移
	 **/
	public void moveLeft(){
		
		fallSpeed=baseSpeed;
		for(BaseBlock b:fallingBlock.getBlocks()){
			int X=(int)b.getX()-b.WIDTH;
			int Y=(int)b.getY();
			if(handleCollision(X,Y))
				return;
		}
		fallingBlock.moveLeft();
	}
	
	/**
		下落块右移
	 **/
	public void moveRight(){
		fallSpeed=baseSpeed;
		for(BaseBlock b:fallingBlock.getBlocks()){
			float X=(int)b.getX()+b.WIDTH;
			float Y=(int)b.getY();
			if(handleCollision(X,Y))
				return;
		}
		fallingBlock.moveRight();	
	}

	/**
		下落块加速
	 **/
	public void moveDown(){
		fallSpeed=0.5f;
	}

	/**
		下落块旋转
	 **/
	public void turn(){
		
		fallSpeed=baseSpeed;		
		ArrayList<BaseBlock> blocks=fallingBlock.getBlocks();
		
		int blocksNum=blocks.size();
		int i=0;
		int tempCoords[][]=new int[blocksNum][2];  //变换后的相对坐标系统
		int coords[][]=fallingBlock.getCoords();

		for(i=0;i<blocksNum;i++){
			tempCoords[i][0]=-coords[i][1];//根据公式：X1=Y0;变换坐标
			tempCoords[i][1]=coords[i][0];//根据公式：Y1=X0;变换坐标
		}
		//检查所有子块的坐标
		int tempX;
		int tempY;
		i=0;
		for(BaseBlock b:blocks){
			tempX=tempCoords[i][0]-coords[i][0];
			tempY=tempCoords[i][1]-coords[i][1];
			float X=b.getX()+tempX*b.WIDTH;
			float Y=b.getY()+tempY*b.HEIGHT;
			if(handleCollision(X,Y))
				return;
			i++;
		}
		fallingBlock.turn();
	}

	/**
		更新容器
	 **/
	private long counTime=0;
	private int leve=0;
	public void update(long elapsedTime){
		
		counTime+=elapsedTime;
		
		isHitBottom=false;
		isHitTop=false;
		isUpdate=true;
		
		sumTime+=elapsedTime;
		updateGameTime();
		updateBlocks(elapsedTime);
		checkGame();

		if(isHitTop&&isHitBottom)
			isGameOver=true;
			
		isUpdate=false;
		
		if(counTime>30000&&leve<=9){
			leve++;
			counTime=0;
			baseSpeed+=.01f;
			tip="等级提升到"+level+"了";
		}
		//更新下落块
		if(isCured)
			initFallBlock();
			
		if(sumTime%1000==0)
			tip="";
		currentLevel=String.valueOf(leve);
			
		
		/*//动感背景
		for(int i=0;i<blockStack.length;i++)
			for(int j=0;j<blockStack[i].length;j++)
				if(blockStack[i][j]!=null)
					blockStack[i][j].update(elapsedTime);*/
		
	}
	

	//自动等级
	private String currentLevel="";
	private int level;
	private String tip="";

	/**
		返回
	**/
	public String getCurrentLevel(){
		return currentLevel;
	}
	public String getTip(){
		return tip;
	}
	
	/**
		每个下落块的子块与容器及容器中的固定块做碰撞检查
	 **/
	public int collision(float X,float Y){
		
		int rank = (int)(X-leftPoint.x)/BaseBlock.WIDTH;
		int row = (int)(Y-leftPoint.y)/BaseBlock.HEIGHT;
		
		//是否碰到容器底
		if(Y+BaseBlock.WIDTH>=rightPoint.y)
			return HITBOTTOM;
		//是否碰到容器顶
		if(Y<=leftPoint.y-1)
			return HITTOP;
		//是否碰到容器边界
		if(X+BaseBlock.WIDTH>rightPoint.x||X<leftPoint.x)
			return HITSIDE;
		//是否碰到别的方块
		if(blockStack[row][rank]!=null)
			return HITOTHER;
		//是否碰到别的方块的顶部
		if(blockStack[row+1][rank]!=null
			&&Y+BaseBlock.HEIGHT>blockStack[row+1][rank].getY())
			return HITBOTTOM;
		return NONE;
		

	}



	/**
		碰撞处理
	 **/
	public boolean handleCollision(float X,float Y){
		
		int arg=collision(X,Y);
		
		switch(arg){
			
			case NONE:		return false;

			case HITSIDE:	return true;

			case HITTOP:	isHitTop=true;
							return false;

			case HITOTHER:	return true;

			case HITBOTTOM:	isHitBottom=true;
							if(isUpdate)
								addToSence();
							return true;
		}
		return false;
	}
	
	/**
		将碰撞块加到容器里面
	 **/
	public void addToSence(){
		
		ArrayList<BaseBlock> bs=fallingBlock.getBlocks();
		
		//添加到容器数组
		for(BaseBlock b:bs){
				
			int rank =Math.round((b.getX()-leftPoint.x)/BaseBlock.WIDTH);
			int row  =Math.round((b.getY()-leftPoint.y)/BaseBlock.HEIGHT);
			
			b.setX(rank*BaseBlock.WIDTH+leftPoint.x);
			b.setY(row*BaseBlock.HEIGHT+leftPoint.y);
			try{
				blockStack[row][rank]=b;
				b.setCured();	
			}catch(Exception e){
				isGameOver=true;
			}
		}

		isCured=true;
	}
	
	/**
		更新游戏时间
	 **/
	public void updateGameTime(){
		gameTime.delete(0,gameTime.length());
		long arg=sumTime/(1000*60*60);
		changHour(arg);
		gameTime.append(":");
		arg=(sumTime/(1000*60))%60;
		changMinute(arg);
		gameTime.append(":");
		arg=sumTime/1000%60;
		changSecond(arg);
	}
	/**
		修改秒
	 **/
	private void changSecond(long arg){		
		if(arg>=10)
			gameTime.append(String.valueOf(arg));
		else
			gameTime.append("0"+String.valueOf(arg));
	}
	/**
		修改分
	 **/
	private void changMinute(long arg){		
		if(arg>=10)
			gameTime.append(String.valueOf(arg));
		else
			gameTime.append("0"+String.valueOf(arg));
	}
	/**
		修改时
	 **/
	private void changHour(long arg){		
		if(arg>=10)
			gameTime.append(String.valueOf(arg));
		else
			gameTime.append("0"+String.valueOf(arg));
	}

	/**
		更新下落块状态
	 **/
	public void updateBlocks(long elapsedTime){
		for(BaseBlock b:fallingBlock.getBlocks()){
			b.setVelocityY(fallSpeed);
			b.update(elapsedTime);
		}
		for(BaseBlock b:fallingBlock.getBlocks())
			handleCollision(b.getX(),b.getY());
	}

	/**
		检查满行
	 **/
	public void checkGame(){
		int fullLineSum=0;

		for(int i=blockStack.length-1;i>=0;i--){
			int l=blockStack[i].length;
			int j=0;
			for(;j<l&&blockStack[i][j]!=null;j++ );
			if(j==l){
				for(j=0;j<l;j++)
					blockStack[i][j]=null;
				for(int r=i;r>0;r--){
					l=blockStack[r].length;
					for(int t=0;t<l;t++){
						if(blockStack[r-1][t]!=null){
							BaseBlock b=blockStack[r-1][t];
							b.setY(b.getY()+b.HEIGHT);
							blockStack[r-1][t]=null;
							blockStack[r][t]=b;
						}
					}
				}	
				fullLineSum++;
			}
		}

		if(fullLineSum>0){
			switch(fullLineSum){
				case 1:score+=100;break;
				case 2:score+=250;break;
				case 3:score+=450;break;
				case 4:score+=600;break;	
			}		
		}
	}
}

