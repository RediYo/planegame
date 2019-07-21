package planegame;

import javax.swing.JFrame;

public class PlaneJFrame extends JFrame{


	public PlaneJFrame() {
		//设置标题
		this.setTitle("全民飞机大战");
		//设置窗体大小
		this.setSize(400, 600);
		//设置关闭按钮
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置不可以最大化
		this.setResizable(false);
		//设置窗体位置居中
		this.setLocationRelativeTo(null);
		PlaneJPanel panel=new PlaneJPanel();
		this.add(panel);
		//设置显示
		this.setVisible(true);
	}
	public static void main(String[] args) {
		new PlaneJFrame();
	}
}
