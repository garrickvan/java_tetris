package tetris.gameelements;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import tetris.graphics.*;
import tetris.gameelements.blocks.*;

/**
	绘图器
 **/
public class GameRenderer{
	/**
		下落块与场景容器的持有
	 **/
	private ArrayList<Sence>		sences;
	private GameSence				gameSence;

	/**
		对场景进行绘图
	 **/
	public void draw(Graphics2D g,GameSence gs){
		
        JFrame frame = gs.getScreen().getFullScreenWindow();
        
		gameSence =gs;
		sences	  =gs.getSences();
		
		
        
		//根据不同的场景绘制
		//主菜单
		if(gs.senceState==gs.INMENU){
			
			Image background =loadImage("background1.jpg");
			g.drawImage(background,0,0,gs.getWidth(),gs.getHeight(),null);

			frame.getLayeredPane().paintComponents(g);
			
			Image title =loadImage("Title.png");
			int w=title.getWidth(null);
			int h=title.getHeight(null);
			g.drawImage(title,50,0,(int)(1.5f*w),(int)(1.5f*h),null);

		}
		
		
		
		//单人游戏
		if(gs.senceState==gs.INSINGLE&&gs.getSenceI()!=null){
			
			//游戏时间
			g.setColor(Color.RED);
			
			Image background =loadImage("background2.jpg");
			g.drawImage(background,0,0,gs.getWidth(),gs.getHeight(),null);

			Font myFont = new Font("SansSerif",Font.BOLD,48);
			g.setFont(myFont);
			g.drawString("游戏时间：",
						 4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+BaseBlock.HEIGHT);
			//时分秒			 
			g.setColor(Color.GREEN);
			String str=String.valueOf(gs.getSenceI().getGemeTime());
			g.drawString(str,
						 4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+2*BaseBlock.HEIGHT);
			
			//方块提示
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,36);
			g.setFont(myFont);		
			g.drawString("下一个方块:",4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
									  4*BaseBlock.HEIGHT+gs.getSenceI().getLeftPoint().y);
			//游戏分数
			g.setColor(Color.yellow);	
			g.drawString("总得分:",4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
									gs.getSenceI().getRightPoint().y-BaseBlock.HEIGHT);
			g.setColor(Color.RED);
			g.drawString(String.valueOf(gs.getSenceI().getScore()),
						 8*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getRightPoint().y-BaseBlock.HEIGHT);
						 
		    
			//游戏等级
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,30);
			g.setFont(myFont);
			g.drawString("游戏等级:",
						gs.getSenceI().getRightPoint().x+4*BaseBlock.WIDTH,
					   	gs.getSenceI().getLeftPoint().y+15*BaseBlock.HEIGHT);
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,30);
			g.setFont(myFont);
			g.drawString(gs.getSenceI().getCurrentLevel(),
						gs.getSenceI().getRightPoint().x+9*BaseBlock.WIDTH,
					   	gs.getSenceI().getLeftPoint().y+15*BaseBlock.HEIGHT);
					   	
						 
			//缓冲空间背景
            g.setColor(Color.gray);
			g.fillRect(	4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x-BaseBlock.SIDELINE,
						5*BaseBlock.HEIGHT+gs.getSenceI().getLeftPoint().y+BaseBlock.SIDELINE,
						5*BaseBlock.WIDTH,
						5*BaseBlock.HEIGHT);
			//缓冲空间
			g.setColor(Color.black);
			for(int i=0;i<5;i++){ 
				for(int j=0;j<5;j++){
						g.fillRect(	j*BaseBlock.WIDTH+4*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
									i*BaseBlock.HEIGHT+5*BaseBlock.HEIGHT+gs.getSenceI().getLeftPoint().y+3*BaseBlock.SIDELINE,
									BaseBlock.INWIDTH,
									BaseBlock.INHEIGHT);
				}
			}
			//缓冲块
			for(BaseBlock b:gs.getBufferBlcoksI().getBlocks()){
					if(b!=null)
						g.drawImage(b.getImage(),(int)b.getX(),(int)b.getY()+b.SIDELINE,b.INWIDTH,b.INHEIGHT,null);
			}
		

		}
		
		
		
		
		
		
		//双人游戏
		if(gs.senceState==gs.INDOUBLE&&gs.getSenceI()!=null&&gs.getSenceII()!=null){
			
			Image background =loadImage("background3.jpg");
			g.drawImage(background,0,0,gs.getWidth(),gs.getHeight(),null);
			//玩家I
			g.setColor(Color.white);
			Font myFont = new Font("SansSerif",Font.BOLD,48);
			g.setFont(myFont);
			g.drawString("玩家I",
						 2*BaseBlock.WIDTH+gs.getSenceI().getLeftPoint().x,
						 gs.getSenceI().getLeftPoint().y-BaseBlock.HEIGHT-5);
			
			//玩家II
			g.setColor(Color.yellow);
			g.drawString("玩家II",
						 2*BaseBlock.WIDTH+gs.getSenceII().getLeftPoint().x,
						 gs.getSenceI().getLeftPoint().y-BaseBlock.HEIGHT-5);
						 
			
			//玩家I方块
			g.setColor(Color.white);
			myFont = new Font("SansSerif",Font.BOLD,24);
			g.setFont(myFont);
			g.drawString("玩家I:",
						 3*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+3*BaseBlock.HEIGHT);
			
			//玩家II方块
			g.setColor(Color.yellow);
			g.drawString("玩家II:",
						 3*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+9*BaseBlock.HEIGHT);
						 
						 
			
			//游戏时间
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,48);
			g.setFont(myFont);
			g.drawString("游戏时间：",
						 2*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+BaseBlock.HEIGHT-5);
			//时分秒			 
			g.setColor(Color.GREEN);
			String str=String.valueOf(gs.getSenceI().getGemeTime());
			g.drawString(str,
						 2*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getLeftPoint().y+2*BaseBlock.HEIGHT);

			//游戏分数
			myFont = new Font("SansSerif",Font.BOLD,24);
			g.setFont(myFont);
			g.setColor(Color.white);	
			g.drawString("I玩家总得分:",2*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
									gs.getSenceI().getRightPoint().y-BaseBlock.HEIGHT);
			g.setColor(Color.RED);
			g.drawString(String.valueOf(gs.getSenceI().getScore()),
						 6*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceI().getRightPoint().y-BaseBlock.HEIGHT);
			//游戏分数B
			g.setFont(myFont);
			g.setColor(Color.yellow);	
			g.drawString("II玩家总得分:",2*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
									gs.getSenceII().getRightPoint().y);
			g.setColor(Color.RED);
			g.drawString(String.valueOf(gs.getSenceII().getScore()),
						 6*BaseBlock.WIDTH+gs.getSenceI().getRightPoint().x,
						 gs.getSenceII().getRightPoint().y);
						 
		    
			//游戏等级
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,30);
			g.setFont(myFont);
			g.drawString("游戏等级:",
						gs.getSenceI().getRightPoint().x+2*BaseBlock.WIDTH,
					   	gs.getSenceI().getLeftPoint().y+15*BaseBlock.HEIGHT);
					   	
			g.setColor(Color.RED);
			myFont = new Font("SansSerif",Font.BOLD,30);
			g.setFont(myFont);
			g.drawString(gs.getSenceI().getCurrentLevel(),
						gs.getSenceI().getRightPoint().x+7*BaseBlock.WIDTH,
					   	gs.getSenceI().getLeftPoint().y+15*BaseBlock.HEIGHT);
					   	
			
			for(BaseBlock b:gs.getBufferBlcoksI().getBlocks()){
					if(b!=null)
						g.drawImage(b.getImage(),
						(int)b.getX()+b.SIDELINE-2*BaseBlock.WIDTH,
						(int)b.getY()+b.SIDELINE+BaseBlock.HEIGHT,
						b.INWIDTH,
						b.INHEIGHT,null);
			}
			for(BaseBlock b:gs.getBufferBlcoksII().getBlocks()){
					if(b!=null)
						g.drawImage(b.getImage(),
						(int)b.getX()+b.SIDELINE,
						(int)b.getY()+b.SIDELINE,
						b.INWIDTH,
						b.INHEIGHT,null);
			}
			
		}




		//画方块容器
		for(Sence s:sences){

			g.setColor(Color.gray);
			g.fillRect(s.getLeftPoint().x,s.getLeftPoint().y,
						s.getRightPoint().x-s.getLeftPoint().x,s.getRightPoint().y-s.getLeftPoint().y);

			BaseBlock bb[][]=s.getBlockStack();
			FallingBlock fb =s.getFallingBlock();
			
			g.setColor(Color.black);
			//画容器中的块
			for(int i=0;i<bb.length;i++){
				for(int j=0;j<bb[i].length;j++){
					BaseBlock b=bb[i][j];
					if(b!=null)
						g.drawImage(b.getImage(),(int)b.getX()+b.SIDELINE,(int)b.getY()+b.SIDELINE,b.INWIDTH,b.INHEIGHT,null);
					else{

						g.fillRect(	j*b.WIDTH+b.SIDELINE+s.getLeftPoint().x,
									i*b.HEIGHT+b.SIDELINE+s.getLeftPoint().y,
									b.INWIDTH,
									b.INHEIGHT);
					}
				}
			}
			//画下落块
			for(BaseBlock b:fb.getBlocks()){
					if(b!=null)
						g.drawImage(b.getImage(),(int)b.getX()+b.SIDELINE,(int)b.getY()+b.SIDELINE,b.INWIDTH,b.INHEIGHT,null);
			}
		}
		
	}

	/**
		加载图片
	 **/
    public Image loadImage(String fileName) {
        return new ImageIcon("images/"+fileName).getImage();
    }
}
