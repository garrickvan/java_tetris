package tetris.gameelements.blocks;

import java.util.*;
import java.awt.*;
import javax.swing.*;

import tetris.graphics.*;

/**
	此类只用于生成下落方块
 **/
public class BlockMaker{
	
	//随机函数
	private	Random rnd;

	//当前生成类型

	//不同下落块的相对坐标
	private int BlockTyped[][][] =	{
		{{-2,0},{-1,0},{0,0},{1,0}},	//长条
		{{-1,0},{0,0},{1,0},{0,-1}},	//T字块
		{{0,1},{1,1},{0,0},{0,-1}},		//正L字块
		{{-1,1},{0,1},{0,0},{0,-1}},	//反L字块
		{{0,0},{1,0},{1,-1},{0,-1}},	//田字块
		{{-1,1},{-1,0},{0,0},{0,-1}},	//正N字块
		{{0,1},{0,0},{-1,0},{-1,-1}},	//反N字块
		{{0,0},{0,-1}},					//短条
		{{0,0},{1,0},{0,-1}},			//正小L字块
		{{-1,0},{0,0},{0,-1}},			//反小L字块
		{{-1,0},{0,0},{1,0},{0,-1},{0,-2}}	//大T字块
	};
	

	//根据场景容器的位置，定制每个下落块的位置
	public BlockMaker(){
		init();
	}

	//初始化随机生成器
	public void init(){
		rnd= new Random();
	}

	//生成方块(不设置下落速度)
	public FallingBlock makeFallingBlocks(){
		
		//下落块中的基础块
		ArrayList<BaseBlock> blocks = new ArrayList<BaseBlock>();

		//随机生成下落块类型
		int btype = rnd.nextInt(BlockTyped.length);
		int coords[][] =BlockTyped[btype];
		
		//随机生成下落块颜色
		int ctype = rnd.nextInt(5);

		for(int i=0;i<coords.length;i++){
			Animation aim = getAnimation(ctype);
			BaseBlock b =new BaseBlock(aim,ctype);
			
			//设置当前块的相对位置
			int temp=coords[i][0];
			b.setX(temp*BaseBlock.WIDTH);
			temp=coords[i][1];
			b.setY(temp*BaseBlock.HEIGHT);

			blocks.add(b);
		}

		return new FallingBlock(blocks,coords,btype);
		
	}

	/**
		根据指定的类型生成下落块
	**/
	public FallingBlock makeOrder(int type,int color){
		
		//下落块中的基础块
		ArrayList<BaseBlock> blocks = new ArrayList<BaseBlock>();

		//生成下落块类型
		int coords[][] =BlockTyped[type];
		
		for(int i=0;i<coords.length;i++){
			Animation aim = getAnimation(color);
			BaseBlock b =new BaseBlock(aim,color);
			
			//设置当前块的相对位置
			int temp=coords[i][0];
			b.setX(temp*BaseBlock.WIDTH);
			temp=coords[i][1];
			b.setY(temp*BaseBlock.HEIGHT);

			blocks.add(b);
		}

		return new FallingBlock(blocks,coords,type);
	}
	
	//获取动画
	public Animation getAnimation(int color){
		
		Animation aim =new Animation();
		
		Image block1;
		Image block2;
		Image block3;
		
		switch(color){
		case 0:	block1=loadImage("blue1");
				block2=loadImage("blue2");
				block3=loadImage("blue3");
				aim.addFrame(block1,200);
				aim.addFrame(block2,200);
				aim.addFrame(block3,200);
				break;

		case 1:	block1=loadImage("red1");
				block2=loadImage("red2");
				block3=loadImage("red3");
				aim.addFrame(block1,200);
				aim.addFrame(block2,200);
				aim.addFrame(block3,200);
				break;

		case 2:	block1=loadImage("yellow1");
				block2=loadImage("yellow2");
				block3=loadImage("yellow3");
				aim.addFrame(block1,200);
				aim.addFrame(block2,200);
				aim.addFrame(block3,200);
				break;

		case 3:	block1=loadImage("green1");
				block2=loadImage("green2");
				block3=loadImage("green3");
				aim.addFrame(block1,200);
				aim.addFrame(block2,200);
				aim.addFrame(block3,200);
				break;

		case 4:	block1=loadImage("purple1");
				block2=loadImage("purple2");
				block3=loadImage("purple3");
				aim.addFrame(block1,200);
				aim.addFrame(block2,200);
				aim.addFrame(block3,200);

		}
		return aim;

	}
	
    public Image loadImage(String fileName) {
        return new ImageIcon(this.getClass().getClassLoader().getResource("images/"+fileName+".png")).getImage();
    }
}
