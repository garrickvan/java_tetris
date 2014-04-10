package tetris.gameelements;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import tetris.graphics.*;
import tetris.gameelements.blocks.*;

/**
	游戏控制中心
 **/
public class GameCenter{
	
	private GameSence gameSence;
	
	/**
		构造方法
	**/
	public GameCenter(GameSence gs){
		this.gameSence=gs;
	}

	/**
		当前场景状态
	**/
	public int getSenceState(){
		return gameSence.senceState;
	}

	/**
		更新游戏
	**/
	public void update(long elapsedTime){
		gameSence.update(elapsedTime);
	}

	/**
		切换至单人游戏
	**/
	public void singleGame(){
		if(handChoose(gameSence.INSINGLE))
			gameSence.toSingleGame();
	}

	/**
		切换至双人游戏
	**/
	public void doubleGame(){
		if(handChoose(gameSence.INDOUBLE))
			gameSence.toDoubleGame();	
	}

	/**
		退回菜单
	**/
	public void toMenu(){
		if(handChoose(gameSence.INMENU))
			gameSence.toMenu();
	}
	
	/**
		A玩家方块旋转
	**/
	public void A_turn(){
		Sence s= gameSence.getSenceI();
		if(s!=null){
			s.turn();
		}
	}

	/**
		A玩家方块左移
	**/
	public void A_left(){
		Sence s= gameSence.getSenceI();
		if(s!=null){
			s.moveLeft();
		}
	}

	/**
		A玩家方块右移
	**/
	public void A_right(){
		Sence s= gameSence.getSenceI();
		if(s!=null){
			s.moveRight();
		}		
	}

	/**
		A玩家方块下移
	**/
	public void A_down(){
		Sence s= gameSence.getSenceI();
		if(s!=null){
			s.moveDown();
		}				
	}

	/**
		B玩家方块旋转
	**/
	public void B_turn(){
		Sence s= gameSence.getSenceII();
		if(s!=null){
			s.turn();
		}
		else {
			s= gameSence.getSenceI();
			if(s!=null)
				s.turn();
		}
	}

	/**
		B玩家方块左移
	**/
	public void B_left(){
		Sence s= gameSence.getSenceII();
		if(s!=null){
			s.moveLeft();
		}
		else {
			s= gameSence.getSenceI();
			if(s!=null)
				s.moveLeft();
		}		
	}

	/**
		B玩家方块右移
	**/
	public void B_right(){
		Sence s= gameSence.getSenceII();
		if(s!=null){
			s.moveRight();
		}
		else {
			s= gameSence.getSenceI();
			if(s!=null)
				s.moveRight();
		}		
	}

	/**
		B玩家方块下移
	**/
	public void B_down(){
		Sence s= gameSence.getSenceII();
		if(s!=null){
			s.moveDown();
		}
		else {
			s= gameSence.getSenceI();
			if(s!=null)
				s.moveDown();
		}		
	}

	/**
		根据提示退出当前状态
	**/
	private boolean handChoose(int state){
		switch(state){
		case 0:	if(gameSence.senceState!=0){
					if(gameSence.showYesOrNo("你要离开游戏回到主菜单吗？","温馨提示")==JOptionPane.YES_OPTION)
						return true;
					else
						return false;
				}
		case 1:if(gameSence.senceState!=1){
					if(gameSence.senceState!=gameSence.INMENU){
						if(gameSence.showYesOrNo("你要离开双人游戏吗？","温馨提示")==JOptionPane.YES_OPTION)
							return true;
						else
							return false;
					}else
						return true;
				}else{
					if(gameSence.showYesOrNo("你确定要复位游戏吗？","温馨提示")==JOptionPane.YES_OPTION)
							return true;
					else
							return false;
				}
		case 2:if(gameSence.senceState!=2){
					if(gameSence.senceState!=gameSence.INMENU){
						if(gameSence.showYesOrNo("你要离开单人游戏吗？","温馨提示")==JOptionPane.YES_OPTION)
							return true;
						else
							return false;
					}else
						return true;
				}else{
					if(gameSence.showYesOrNo("你确定要复位游戏吗？","温馨提示")==JOptionPane.YES_OPTION)
							return true;
					else
							return false;
				}		
		}
		return true;
	}
}
