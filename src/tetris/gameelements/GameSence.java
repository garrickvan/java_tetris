package tetris.gameelements;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import tetris.graphics.*;
import tetris.gameelements.blocks.*;
/**
	游戏场景
 **/
public class GameSence implements ActionListener{
	
	/**
		场景状态的字段
	 **/
	public static final int INMENU		=0;
	public static final int INSINGLE	=1;
	public static final int INDOUBLE	=2;

	//场景持有
	private  ArrayList<Sence> sences;
	
	//场景内的所有元素
	private ArrayList<Sprite> sprites;

	//场景内的所有元素
	private FallingBlock bufferBlcoksI;
	private FallingBlock bufferBlcoksII;

	//场景
	private Sence senceI;
	private Sence senceII;
	
	//当前场景状态
	public int senceState;

	//当前屏幕的高与宽
	private JFrame frame;
	private ScreenManager screen;
	private int width;
	private int height;
	//当前的提示面板
	private JOptionPane jop;
	private boolean isUsing;

	//游戏说明面板
	private JPanel playdetal;

	//游戏结束
	private boolean isGameOver;
	
	private boolean isExit;

	//获取屏幕的宽
	public  int getWidth(){
		return width;
	}
	//获取屏幕的高
	public  int getHeight(){
		return height;
	}
	
	/**
		构造
	 **/
	public GameSence(ScreenManager screen){
		this.screen=screen;
		this.frame=screen.getFullScreenWindow();
		this.width=frame.getWidth();
		this.height=frame.getHeight();
		jop=new JOptionPane();
		isGameOver=false;
		isExit=false;
		init();
		
	}
	
	//场景一
	public Sence getSenceI(){
		return senceI;
	}

	//场景二
	public Sence getSenceII(){
		return senceII;
	}

	/**
		初始化
	 **/
	public void init(){
        JFrame frame = screen.getFullScreenWindow();
        contentPane= frame.getContentPane();
		sences = new  ArrayList<Sence> ();
		sprites = new ArrayList<Sprite> ();
		senceState=INMENU;
		toMenu();
	}
	
	/**
		获取场景元素
	 **/
	public ArrayList<Sprite> getSprites(){
		return sprites;
	}
	
	/**
		获取屏幕管理
	**/
	public ScreenManager getScreen(){
		return screen;
	}
	
	//是否要退出
	public boolean isExit(){ 
		return isExit;
	}

	/**
		返回场景
	 **/
	public ArrayList<Sence> getSences(){
		
		return sences;
	}
	/**
		获取消息面板
	**/
	public JOptionPane getJOP(){
		return jop;
	}
	
	/**
		是否在用消息面板
	**/
	public boolean isUsingJOP(){
		return isUsing;
	}

	/**
		关闭组件的重画
	**/
	public void close(){
		this.isUsing=false;
	}

	/**
		返回缓冲块I
	 **/
	public FallingBlock getBufferBlcoksI(){
		
		return bufferBlcoksI;
	}
	/**
		返回缓冲块II
	 **/
	public FallingBlock getBufferBlcoksII(){
		
		return bufferBlcoksII;
	}
	
	/**
		清空场景
	 **/
	public void cleanSence(){
		if(sences!=null)
			sences.clear();
		if(sprites!=null)
			sprites.clear();
		senceI=null;
		senceII=null;
		if(contentPane!=null)
			contentPane.removeAll();	
	}
	
	/**
		设置缓冲落块的显示位置
	**/
	private void setBufferBlockLocation(int x,int y,FallingBlock bufferBlock){
		
		for(BaseBlock b:bufferBlock.getBlocks()){
			b.setX(b.getX()+x);
			b.setY(b.getY()+y);
		}
	}

	/**
		切换到单人场景
	 **/
	public void toSingleGame(){
		
		cleanSence();

		senceState=INSINGLE;
		
		BaseBlock.INWIDTH=38;
		BaseBlock.INHEIGHT=38;
		BaseBlock.WIDTH		=BaseBlock.INWIDTH+BaseBlock.SIDELINE*2;
		BaseBlock.HEIGHT	=BaseBlock.INHEIGHT+BaseBlock.SIDELINE*2;
		
		senceI= new Sence(10,height-10-BaseBlock.HEIGHT*Sence.ROW);
		senceI.setFallSpeed(.03f);
		sences.add(senceI);

		updateBufferBlock(1);
	}
	
	/**
		更新缓冲块
	**/
	private void updateBufferBlock(int i){
		
		if(i==1){
			bufferBlcoksI=senceI.getBufferBlock();
			senceI.getBuffer();
			setBufferBlockLocation( senceI.getRightPoint().x+BaseBlock.WIDTH*6,
									BaseBlock.HEIGHT*8,
									bufferBlcoksI);

		}
		if(i==2){
			bufferBlcoksII=senceII.getBufferBlock();
			senceII.getBuffer();
			setBufferBlockLocation( senceI.getRightPoint().x+BaseBlock.WIDTH*4,
									BaseBlock.HEIGHT*15,
									bufferBlcoksII);

		}
	}


	/**
		切换到双人场景
	 **/
	public void toDoubleGame(){
		
		cleanSence();

		senceState=INDOUBLE;
		
		BaseBlock.INWIDTH=33;
		BaseBlock.INHEIGHT=33;
		BaseBlock.WIDTH		=BaseBlock.INWIDTH+BaseBlock.SIDELINE*2;
		BaseBlock.HEIGHT	=BaseBlock.INHEIGHT+BaseBlock.SIDELINE*2;
		
		senceI= new Sence(10,height-10-BaseBlock.HEIGHT*Sence.ROW);
		int senceIWidth		=senceI.getRightPoint().x-senceI.getLeftPoint().x;
		senceII= new Sence(width-senceIWidth-10,height-10-BaseBlock.HEIGHT*Sence.ROW);
		senceI.setFallSpeed(.03f);
		senceII.setFallSpeed(.03f);
		sences.add(senceI);
		sences.add(senceII);
		
		updateBufferBlock(1);
		updateBufferBlock(2);
	}

	/**
		切换到主菜单
	 **/
	//按钮
	private JButton singleBtn;
	private JButton doubleBtn;
	private JButton detailBtn;
	private JButton exitBtn;
	private Container contentPane ;
	
	public void toMenu(){
		cleanSence();
		senceState=INMENU;

        JPanel playButtonSpace = new JPanel();
        
		singleBtn=createButton("singleBtn","单人游戏模式");
		doubleBtn=createButton("doubleBtn","双人游戏模式");
		detailBtn=createButton("detail","帮助");
		exitBtn=createButton("exit","退出");
		singleBtn.addActionListener(this);
		doubleBtn.addActionListener(this);
		detailBtn.addActionListener(this);
		exitBtn.addActionListener(this);
        
        playButtonSpace.setOpaque(false);
        playButtonSpace.add(singleBtn);

        if (contentPane instanceof JComponent) {
            ((JComponent)contentPane).setOpaque(false);
        }
        
        contentPane.setLayout(null);
        playButtonSpace.setLocation(250,320);
        playButtonSpace.setSize(500,500);
		contentPane.add(playButtonSpace);
		playButtonSpace.add(singleBtn);
		playButtonSpace.add(doubleBtn);
		playButtonSpace.add(detailBtn);
		playButtonSpace.add(exitBtn);
	}
	
	/**
	 *更新游戏
	 **/
	public void update(long elapsedTime){
		close();
		if(senceState==INMENU){
			
		}
		if(senceState==INSINGLE){
			
			if(senceI!=null&&!senceI.isGameOver()){
					senceI.update(elapsedTime);
				//更新下落块
				if(senceI.haveBufferBlock())
					updateBufferBlock(1);
				//添加动感
				if(bufferBlcoksI!=null){
					for(BaseBlock b:bufferBlcoksI.getBlocks())
						b.update(elapsedTime);
				}
					
			}

			if(senceI!=null&&senceI.isGameOver()){
				int choose=showYesOrNo("是否重新开始？","你输了！");
				if(choose==JOptionPane.YES_OPTION){
					toSingleGame();
				}else
					toMenu();
			}
		}
		if(senceState==INDOUBLE){	
		
			if(senceI!=null&&!senceI.isGameOver()){
					senceI.update(elapsedTime);
				if(senceI.haveBufferBlock())
					updateBufferBlock(1);
				//添加动感
				if(bufferBlcoksI!=null){
					for(BaseBlock b:bufferBlcoksI.getBlocks())
						b.update(elapsedTime);
				}
			}			
			if(senceI!=null&&senceI.isGameOver()){
				int choose=showYesOrNo("是否重新开始？","玩家1输了！");
				if(choose==JOptionPane.YES_OPTION){
					toDoubleGame();
				}else
					toMenu();
			}

			if(senceII!=null&&!senceII.isGameOver()){
					senceII.update(elapsedTime);
				if(senceII.haveBufferBlock())
					updateBufferBlock(2);
				//添加动感
				if(bufferBlcoksII!=null){
					for(BaseBlock b:bufferBlcoksII.getBlocks())
						b.update(elapsedTime);
				}				
			}
			if(senceII!=null&&senceII.isGameOver()){
				int choose=showYesOrNo("是否重新开始？","玩家2输了！");
				close();
				if(choose==JOptionPane.YES_OPTION){
					toDoubleGame();
				}else
					toMenu();				
			}
		} 
	}
	
	/*
	 *提示面版
	 */
	public int showYesOrNo(String str1,String str2){
		
		jop.getRootFrame().setAlwaysOnTop(false); 
		int w=200;
		int h=50;
		jop.setSize(w,h); 
		jop.setLocation((width - w) / 2,(height - h) / 2);
		jop.paint(screen.getGraphics());
		isUsing=true;
		jop.getRootFrame().setAlwaysOnTop(true); 
		return jop.showConfirmDialog(frame,str1,str2, JOptionPane.YES_NO_OPTION);
	}
	
	/**
		事件监听	
	**/
	public void actionPerformed(ActionEvent e){
				
		if(e.getSource()==singleBtn)
			toSingleGame();
		if(e.getSource()==doubleBtn)
			toDoubleGame();
		if(e.getSource()==detailBtn){
			
			if(playdetal!=null){
				contentPane.remove(playdetal);
				playdetal=null;
			}
			
			if(playdetal==null){
				playdetal=new JPanel();
				String str="俄罗斯方块游戏按键说明\n"+"A:玩家一方块向左\n"+"D:玩家一方块向右\n"+
						   "W:玩家一方块向上\n"+"S:玩家一方块下落\n"+"SPEACE: 单人模式：玩家一方块下落\n"+
							"UP:单人模式：玩家一方块向上；双人模式：玩家二方块向上\n"+
							"DOWN: 单人模式：玩家一方块向下；双人模式：玩家二方块向下\n"+
							"LEFT: 单人模式：玩家一方块向左；双人模式：玩家二方块向左\n"+
							"RIGHT: 单人模式：玩家一方块向右；双人模式：玩家二方块向右\n"+
							"ENTER: 单人模式：玩家一方块下落；双人模式：玩家二方块下落\n"+
							"P:暂停\n"+"ESC:返回菜单或者退出游戏.\n";
				JTextArea strArea= new JTextArea(str);
				Font myFont = new Font("SansSerif",Font.BOLD,13);
				strArea.setFont(myFont);
				strArea.setEditable(false);
				playdetal.setLocation(600,420);
		        playdetal.setSize(450,260);
				playdetal.add(strArea);
				contentPane.add(playdetal);
			}

		}
			
		if(e.getSource()==exitBtn)
			isExit=true;
		return ;
	}
	
	/**
		创建按钮
	**/
    public JButton createButton(String name, String toolTip) {
        ImageIcon iconRollover = new ImageIcon(this.getClass().getClassLoader().getResource("images/" + name + ".png"));
        int w = iconRollover.getIconWidth();
        int h = iconRollover.getIconHeight();

        // 为按钮设置手型鼠标
        Cursor cursor =
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

        // 创建半透明的默认图片
        Image image = screen.createCompatibleImage(w, h,
            Transparency.TRANSLUCENT);
        Graphics2D g = (Graphics2D)image.getGraphics();
        Composite alpha = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, .5f);
        g.setComposite(alpha);
        g.drawImage(iconRollover.getImage(), 0, 0, null);
        g.dispose();
        ImageIcon iconDefault = new ImageIcon(image);

        // 创建默认图标
        image = screen.createCompatibleImage(w, h,
            Transparency.TRANSLUCENT);
        g = (Graphics2D)image.getGraphics();
        g.drawImage(iconRollover.getImage(), 2, 2, null);
        g.dispose();
        ImageIcon iconPressed = new ImageIcon(image);

        // 创建按钮
        JButton button = new JButton();
        button.addActionListener(this);
        button.setIgnoreRepaint(true);
        button.setFocusable(false);
        button.setToolTipText(toolTip);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setCursor(cursor);
        button.setIcon(iconDefault);
        button.setRolloverIcon(iconRollover);
        button.setPressedIcon(iconPressed);

        return button;
    }

}
