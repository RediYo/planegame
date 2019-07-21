package planegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Enemy {
    //计算总分
    public static int  sumscore=0;
    //敌机坐标
    int ex, ey;
    List<Buttet> Ebuttets = new ArrayList<Buttet>();//敌方子弹
    //敌机图片
    Image eImg;
    //敌机速度
    int eSpeed=-1;
    //boss血量
    int bossblood=-1;
    //通关标识符打败三个boss即可
    static boolean victory = false;
    //boss分数标识，避免因为线程不安全多次增加
    static int bosscore = 4;
    //敌机爆炸图片的显示时间
    int eTime = 8;
    //敌机的存活状态
    boolean exist = true;
    //画布
    PlaneJPanel mainView;
    //定义一个敌机和英雄机相撞的标识
    boolean Y = false;
    //定义英雄机和子弹碰撞的标识
    boolean D = false;
    boolean bossmove = false;//boss机移动边界标识，未移动到最左边为false，未移动到最右边为true
    int bossid=0;//boss的id标识
    static int bulletid=4;//boss子弹id标识


    public Enemy(PlaneJPanel mainView, int ex, int ey, int eSpeed, Image eImg) {
        super();
        this.ex = ex;
        this.ey = ey;
        this.eSpeed = eSpeed;
        this.eImg = eImg;
        this.mainView = mainView;
    }

    public void moveBoss() {
        if (!bossmove) {//boss机未移动到最左边为bossmove为false
            ex -= 5;//向左移动，速度为5
            if (ex <= 20) {//到了边界20，则标识置反
                bossmove = !bossmove;
            }
        }
        if (bossmove) {//boss机未移动到最右边为bossmove为true
            ex += 5;//向右移动，速度为5
            if (ex >= 280) {//到了最右边界280，标识置反
                bossmove = !bossmove;
            }
        }
    }

    //敌机移动的方法
    public void moveEnemy() {
        ey += eSpeed;//以创建敌机时设置的速度移动
        if (ey > mainView.getHeight()) {//敌机坐标超出界面范围，敌机状态为false
            exist = false;
        }
        if (!exist) {//如果敌机状态为false则敌机爆炸显示时间-1
            eTime--;
        }
        if (eTime <= 0) {//如果敌机爆炸时间<=0则移除敌机
            //敌机删掉
            mainView.enemies.remove(this);
        }
    }

    //画敌机的方法
    public void drawEnemy(Graphics g) {
        g.drawImage(eImg, ex, ey, null);
    }
    public void drawbossblood(Graphics g){
        g.setColor(Color.RED);//设置字体颜色为红色
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));//字体大小为20
        g.drawString("BOSS血量: "+bossblood, 260, 30);
    }

    //英雄机和子弹碰撞
    public void phint(Buttet bullet, PlaneJPanel plane) {
        //设置碰撞范围并判断
        int lx = Math.abs((bullet.bx + bullet.bImg.getWidth(null) / 3)
                - (plane.px + plane.p[0].getWidth() / 3));
        int ly = Math.abs((bullet.by + bullet.bImg.getHeight(null) / 4)
                - (plane.py + plane.p[0].getHeight() / 4));
        boolean h = lx <= (bullet.bImg.getWidth(null) / 3 + plane.p[0].getWidth() / 3);
        boolean r = ly <= (bullet.bImg.getHeight(null) / 4 + plane.p[0].getHeight() / 4);

        if (h && r) {//判断是否在碰撞范围之内
            bullet.exist = false;//该子弹状态置为不存在
            sumscore-=30;//英雄机和子弹碰撞-30分
            bullet.B=true;//英雄机和子弹碰撞标识置为true，此为子弹类标识
            plane.Pexist = false;//英雄机处于预死亡状态
            D = true;//英雄机和子弹碰撞标识置为true
        }
    }

    //敌机和子弹相撞
    public void hint(Buttet bullet, Enemy enemy) {
        //碰撞范围控制
        int lx = Math.abs((bullet.bx + bullet.bImg.getWidth(null) / 3)
                - (enemy.ex + enemy.eImg.getWidth(null) / 3));
        int ly = Math.abs((bullet.by + bullet.bImg.getHeight(null) / 4)
                - (enemy.ey + enemy.eImg.getHeight(null) / 4));
        boolean H = lx <= (bullet.bImg.getWidth(null) / 3
                + enemy.eImg.getWidth(null) / 3);
        boolean R = ly <= (bullet.bImg.getHeight(null) / 4
                + enemy.eImg.getHeight(null) / 4);
        if (H && R) {//判断是否处在碰撞范围之内
            if (this.eSpeed == 0) {//判断该敌机是否为boss，boss的eSpeed==0
                this.bossblood--;//血量减一
                if(this.bossblood <= 0) {//判断boss是否会处于死亡状态
                    if (this.bossid == 4 && bosscore == 4) {//判断是否为第一个boss
                        sumscore += 200;//打败第一个boss加200
                        bosscore++;//boss分数id+1，避免因线程问题分数多次加载
                        bulletid++;//子弹标识加一，一般只要boss死亡都会加一，为下一个boss装载不同子弹做准备
                    }else if (this.bossid == 5 && bosscore == 5) {//判断是否为第二个boss
                        sumscore += 500;//打败第二个boss加500
                        bosscore++;
                        bulletid++;
                    }else if (bossid == 6 && bosscore == 6) {//判断是否为第三个boss
                        sumscore += 1000;//打败终极boss加1000
                        bosscore++;
                        bulletid++;
                        victory = true;//游戏胜利标识置为true
                    }
                    enemy.exist = false;
                }
            } else {
                sumscore += 10;//击败普通敌机加10分
                enemy.exist = false;//敌机状态置为死亡
            }
            bullet.exist = false;//子弹状态置为不存在
        }
    }

    //英雄机和敌机碰撞
    public void hint2(Enemy enemy, PlaneJPanel plane) {
        //碰撞范围控制
        int lx = Math.abs((enemy.ex + enemy.eImg.getWidth(null) / 3)
                - (plane.px + plane.p[0].getWidth() / 3));
        int ly = Math.abs((enemy.ey + enemy.eImg.getHeight(null) / 4)
                - (plane.py + plane.p[0].getHeight() / 4));
        boolean h = lx <= (enemy.eImg.getWidth(null) / 3 + plane.p[0].getWidth() / 3);
        boolean r = ly <= (enemy.eImg.getHeight(null) / 4 + plane.p[0].getHeight() / 4);

        if (h && r) {//判断是否在碰撞范围之内
            if (this.eSpeed == 0) {//与boss机碰撞
                this.bossblood--;//boss机血量减一
                if(this.bossblood <= 0) {//判断boss机血量是否等于0
                    bulletid++;//boss子弹标识加一
                    enemy.exist = false;//该boss为死亡状态
                }
            } else {
                enemy.exist = false;//普通敌机直接死亡
            }
            plane.Pexist = false;//英雄机处于预死亡状态，英雄机血量为0才为真正死亡
            Y = true;//敌机和英雄机相撞标识置为真
        }
    }
}
