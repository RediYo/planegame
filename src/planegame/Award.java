package planegame;

import java.awt.Graphics;
import java.awt.Image;

public class Award {
	//坐标
	int ax, ay;
	//图片对象
	Image aImg;
	//奖励的存在
	boolean exist=true;
	//将画布对象传进来准备做吃奖励的操作
	PlaneJPanel mainView;
	//有四种道具类型
	int kind;
	public Award(int ax, int ay, Image aImg, PlaneJPanel mainView, int kind) {
		super();
		this.ax = ax;
		this.ay = ay;
		this.aImg = aImg;
		this.mainView = mainView;
		this.kind = kind;
	}

	//画奖励的方法
	public void drawAward(Graphics g){
		g.drawImage(aImg, ax, ay, null);
	}

	//奖励移动的方法
	public void moveAward(){//每移动一次就判断是否已拾获道具
		ay+=4;//移动速度为4
		if(ay>=mainView.getHeight()){//越界删除奖励道具
			exist=false;
		}
		//各自判断
		hint0();
		hint1();
		hint2();
		hint3();
	}

	public void hint0(){
		if(kind==0){//说明飞机碰到的是桃心
			//奖励的最右边要大于飞机的最左边
			//飞机的最有边大于奖励的最左边
			//飞机的最下边大于奖励的最上边
			//奖励的最下边大于飞机的最上边
			//判断是否在拾捡范围之内
			if(ax+aImg.getWidth(null)>=mainView.px &&
					ax<mainView.px+mainView.p[0].getWidth()
					&&ay<=mainView.py+mainView.p[0].getHeight()
					&&ay+aImg.getHeight(null)>=mainView.py){
				exist=false;//道具失效
				mainView.awards.remove(this);//销毁道具
				mainView.blood++;//英雄机血量加一
			}
		}
	}
	public void hint1(){
		if(kind==2){//说明飞机碰到的是加速
			//判断是否在拾捡范围之内
			if(ax+aImg.getWidth(null)>=mainView.px &&
					ax<mainView.px+mainView.p[0].getWidth()
					&&ay<=mainView.py+mainView.p[0].getHeight()
					&&ay+aImg.getHeight(null)>=mainView.py){
				exist=false;//道具失效
				mainView.awards.remove(this);//销毁道具
				mainView.bulletSpeed=15;//英雄机子弹速度置为15
			}
		}
	}
	public void hint2(){
		if(kind==1){//说明飞机碰到的是三发子弹道具
			//判断是否在拾捡范围之内
			if(ax+aImg.getWidth(null)>=mainView.px &&
					ax<mainView.px+mainView.p[0].getWidth()
					&&ay<=mainView.py+mainView.p[0].getHeight()
					&&ay+aImg.getHeight(null)>=mainView.py){
				exist=false;//道具失效
				mainView.awards.remove(this);//销毁道具
				mainView.threeBullet=true;//三发子弹道具标识置为true
			}
		}
	}
	public void hint3() {
		if(kind==3){//说明飞机碰到的是减速道具
			//判断是否在拾捡范围之内
			if(ax+aImg.getWidth(null)>=mainView.px &&
					ax<mainView.px+mainView.p[0].getWidth()
					&&ay<=mainView.py+mainView.p[0].getHeight()
					&&ay+aImg.getHeight(null)>=mainView.py){
				exist=false;//道具失效
				mainView.awards.remove(this);//销毁道具
				mainView.bulletSpeed=1;//速度值置为1
			}
		}
	}


}