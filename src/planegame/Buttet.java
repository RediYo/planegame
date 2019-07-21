package planegame;

import java.awt.Graphics;
import java.awt.Image;

public class Buttet {
	//子弹的坐标
	int bx,by;
	//定义子弹图片
	Image bImg;
	//定义子弹的一个标识
	boolean exist=true;
	//子弹与英雄机碰撞标识
	boolean B = false;
	//定义一个子弹方向
	int direction;
	//定义一个子弹速度
	int bulletSpeed;
	public Buttet(int bx, int by, Image bImg, int direction, int bulletSpeed) {
		super();
		this.bx = bx;
		this.by = by;
		this.bImg = bImg;
		this.direction = direction;
		this.bulletSpeed = bulletSpeed;
	}
	//定义一个画炮弹的方法
	public void  drawBullet(Graphics g){
		g.drawImage(bImg, bx, by, null);
	}
	//子弹移动的方法
	public void moveBullet0(){
		by-=bulletSpeed;
		if(by<=20){//子弹移动到达边界，存在状态改变
			exist=false;
		}
	}
	public void moveBullet1(){
		by-=bulletSpeed;
		bx--;
		if(by<=20){//子弹移动到达边界，存在状态改变
			exist=false;
		}
	}
	public void moveBullet2(){
		by-=bulletSpeed;
		bx++;
		if(by<=20){//子弹移动到达边界，存在状态改变
			exist=false;
		}
	}
	public void moveBullet3(){
		by+=bulletSpeed;
		if(by>=600){//子弹移动到达边界，存在状态改变
			exist=false;
		}
	}
	public void moveBulletboss(){
		by+=bulletSpeed;
		if(by>=600){//子弹移动到达边界，存在状态改变
			exist=false;
		}
	}
	
}
