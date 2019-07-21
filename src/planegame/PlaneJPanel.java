package planegame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlaneJPanel extends JPanel implements MouseListener, MouseMotionListener, Runnable {
    boolean musickg = true;
    //音乐播放图片
    static ImageIcon mg;
    //健康公告标识
    boolean gonggao=true;
    Music music;
    //定义背景图片对象
    static Image startImg;
    static Image restartImg;
    //定义背景图片的坐标
    int x = 0, y = 0;
    //定义游戏开关
    boolean ck = true;
    Thread t;//线程
    //定义一个暂停的开关
    boolean suspend = false;
    //创建放飞机的数组存放英雄机的图片
    static BufferedImage p[] = new BufferedImage[4];
    //定义一个数组下标用来切换图片
    int pc = 0;
    //定义飞机坐标
    int px = 100, py = 100;
    //定义一个存放子弹的图片变量
    static Image bImg, bImg1, bImg2, bImg3, bImg6, bImg8;
    static Image bimage;
    //定义有个几个存放子弹
    List<Buttet> buttets = new ArrayList<Buttet>();
    //定义一个计数的变量
    int count = 0;
    //定义一个集合来存放奖励
    List<Award> awards = new ArrayList<Award>();
    static BufferedImage awa[] = new BufferedImage[4];//奖励道具图片
    //定义一个飞机的血量
    int blood = 2;
    //定义飞机发射子弹是三发,控制子弹时间标识
    boolean threeBullet;
    int bulletTime = 0;//三发子弹道具使用时间标识
    //子弹速度
    int bulletSpeed = 8;
    int bulletTime2 = 0;//子弹加速道具使用时间标识
    int bulletTime3=0;//子弹减速使用时间标识
    //定义一个敌机的集合
    List<Enemy> enemies = new ArrayList<Enemy>();
    //英雄机活着的标识
    boolean Pexist = true;
    //游戏结束标志
    boolean over = false;
    //程序开始时间
    long startTime;
    //bossid标识创建boss时使用
    int bossid = 4;
    //当前boss
    Enemy currentEnemy;
    //求模的模数
    int m = 1;

    static {//静态代码块
        try {
            //加载开始图片
            startImg = ImageIO.read(new File("image/GameInterface/interface_1.png"));
            restartImg = ImageIO.read(new File("image/GameInterface/jeimian_2.png"));
            //加载音乐
            mg=new ImageIcon("image/open.png");
            //加载飞机图片
            p[0] = ImageIO.read(new File("image/1.png"));
            p[1] = ImageIO.read(new File("image/2.png"));
            p[2] = ImageIO.read(new File("image/HeroPlane/plane_2.png"));
            p[3] = ImageIO.read(new File("image/HeroPlane/plane_3.png"));
            bImg = ImageIO.read(new File("image/bullet/bullet_1.png"));
            bImg1 = ImageIO.read(new File("image/bullet/bullet_8.png"));
            bImg2 = ImageIO.read(new File("image/bullet/bullet_2.png"));
            bImg3 = ImageIO.read(new File("image/bullet/bullet_3.png"));
            bImg6 = ImageIO.read(new File("image/bullet/bullet_6.png"));
            bImg8 = ImageIO.read(new File("image/bullet/bullet_5.png"));
            awa[0] = ImageIO.read(new File("image/award/award_1.png"));
            awa[1] = ImageIO.read(new File("image/award/award_2.png"));
            awa[2] = ImageIO.read(new File("image/award/award_3.png"));
            awa[3] = ImageIO.read(new File("image/award/award_4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlaneJPanel() {
        //监听鼠标和移动
        addMouseListener(this);
        addMouseMotionListener(this);
        music = new Music();
        music.play();
        t = new Thread(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //画开始图片
        g.drawImage(startImg, x, y, null);
        //画音乐开关
        g.drawImage(mg.getImage(), 360, 0, null);
        if(gonggao) {//健康提示
            g.setColor(Color.white);
            g.setFont(new Font("宋体", Font.BOLD, 15));
            g.drawString(" ————游戏公告————", 90, 460);
            g.drawString("抵制不良游戏  拒绝盗版游戏", 90, 490);
            g.drawString("注意自我保护  谨防受骗上当", 90, 510);
            g.drawString("适度游戏益脑  沉迷游戏伤身 ", 90, 530);
            g.drawString("合理安排时间  享受健康生活 ", 90, 550);
        }
        //画飞机，根据bossid及boss血量<0判断换装
        if (ck == false) {
            if (bossid == 4) {
                pc = pc == 0 ? 1 : 0;
            } else if (currentEnemy.bossid == 4 && currentEnemy.bossblood <= 0) {
                pc = 2;
            } else if (currentEnemy.bossid == 5 && currentEnemy.bossblood <= 0) {
                pc = 3;
            }
            g.drawImage(p[pc], px, py, null);
        }
        //英雄机画炮弹
        for (int i = 0; i < buttets.size(); i++) {
            //取出每一个炮弹对象
            Buttet buttet = buttets.get(i);
            if (buttet.exist == false) {//如果子弹存在状态为false则销毁
                buttets.remove(buttet);//销毁子弹
            } else {
                buttet.drawBullet(g);//画子弹
            }
        }
        //画敌机炮弹
        for (int i = 0; i < enemies.size(); i++) {
            //取出每一个炮弹对象
            Enemy enemy = enemies.get(i);
            for (int j = 0; j < enemy.Ebuttets.size(); j++) {
                enemy.Ebuttets.get(j).drawBullet(g);
            }
        }
        //画奖励
        for (int i = 0; i < awards.size(); i++) {
            //取出每一个奖励对象
            Award award = awards.get(i);
            award.drawAward(g);
        }
        //画敌机
        if (ck == false) {
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (enemy.eSpeed == 0) {//判断是否是boss机
                    if (enemy.bossblood > 0) {//boss机未死亡时画血量
                        enemy.drawEnemy(g);
                        enemy.drawbossblood(g);//画boss机血量
                    }
                } else {
                    enemy.drawEnemy(g);
                }
            }
        }

        //画飞机血量
        if (ck == false) {//ck==false保证进入游戏
            if (blood > 0 && blood <= 5) {//如果血量在规定范围之内，则画出血量
                for (int i = 1; i <= blood; i++) {
                    g.drawImage(awa[0], i * 30, 10, null);
                }
            } else if (blood <= 0) {
                over = true;
                //加载英雄机死亡图片
                try {
                    p[0] = ImageIO.read(new File("image/blast/blast_3.png"));
                    p[1] = ImageIO.read(new File("image/blast/blast_3.png"));
                    t.stop();//线程停止
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                for (int i = 1; i < 6; i++) {
                    g.drawImage(awa[0], i * 30, 10, null);
                }
            }
        }
        //如果游戏胜利，画出胜利模式界面
        if (Enemy.victory == true) {
            //全屏清除敌机和奖励只剩英雄机
            enemies.clear();
            awards.clear();
            ck=true;//游戏完成
        }
        //画分数
        if (ck == false) {
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("分数: " + String.valueOf(Enemy.sumscore), 280, 60);
        }
        //胜利
        if (ck == true && Enemy.victory == true) {
            g.drawImage(p[pc], px, py, null);//画英雄机
            //画得分
            g.setFont(new Font("微软雅黑", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("YOU WIN！", 110, 180);
            g.drawString("最终得分：" + Enemy.sumscore, 90, 240);
        }
        //画游戏结束
        if (ck == false) {
            if (over == true) {
                //画游戏结束界面
                g.drawImage(restartImg, 0, 0, null);
                //画分数
                g.setFont(new Font("微软雅黑", Font.BOLD, 30));
                g.setColor(Color.WHITE);
                g.drawString("最终得分：" + Enemy.sumscore, 110, 150);
            }
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                if (suspend) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (count % 20 == 0) {
                    //创建炮弹,并添加到集合中，三种变装炮弹
                    if (pc == 2) {
                        bimage = bImg6;
                    } else if (pc == 3) {
                        bimage = bImg8;
                    } else {
                        bimage = bImg;
                    }
                    Buttet buttet = new Buttet(px + p[pc].getWidth() / 2 - bimage.getWidth(null) / 2, py, bimage, 0, bulletSpeed);
                    buttets.add(buttet);
                    if (threeBullet) {
                        Buttet buttet1 = new Buttet(px + p[pc].getWidth() / 2 - bimage.getWidth(null) / 2, py, bimage, 1, bulletSpeed);
                        Buttet buttet2 = new Buttet(px + p[pc].getWidth() / 2 - bimage.getWidth(null) / 2, py, bimage, 2, bulletSpeed);
                        buttets.add(buttet1);
                        buttets.add(buttet2);
                    }
                }
                if (threeBullet) {//三发子弹显示中
                    bulletTime++;
                }
                if (bulletTime == 300) {//控制三发子弹道具使用时间
                    threeBullet = false;
                    bulletTime = 0;
                }
                if (bulletSpeed == 15) {//加速子弹中
                    bulletTime2++;
                }
                if (bulletTime2 == 300) {//控制子弹加速道具的使用时间
                    bulletSpeed = 8;
                    bulletTime2 = 0;
                }
                if (bulletSpeed == 1) {//减速子弹中
                    bulletTime3++;
                }
                if (bulletTime3 == 300) {//控制子弹减速道具使用时间
                    bulletSpeed = 8;
                    bulletTime3 = 0;
                }
                if (count % 100 == 0) {
                    //生成的奖励
                    int h = (int) (Math.random() * 4);
                    Award award = new Award((int) (Math.random() * 300 + 30), -50, awa[h], this, h);
                    awards.add(award);
                }
                //奖励的移动
                for (int i = 0; i < awards.size(); i++) {
                    Award award2 = awards.get(i);
                    award2.moveAward();
                }
                //两波敌兵
                if (count % 80 == 0) {
                    int ex = (int) (Math.random() * 350);
                    int eh = (int) (Math.random() * 5 + 2);
                    Image eImg = new ImageIcon("image/LittlePlane/plane" + eh + ".png").getImage();
                    Enemy enemy = new Enemy(this, ex, -50, eh--, eImg);
                    enemies.add(enemy);
                }
                //第二波敌兵
                if (count % 120 == 0) {
                    int ex = (int) (Math.random() * 350);
                    int eh = (int) (Math.random() * 5 + 2);
                    Image eImg = new ImageIcon("image/LittlePlane/plane" + eh + ".png").getImage();
                    Enemy enemy = new Enemy(this, ex, -50, eh, eImg);
                    enemies.add(enemy);
                }
                //创建第一个boss，将在15s后上场
                if (bossid == 4 && (System.currentTimeMillis() - startTime) > 15000) {
                    Image eImg = new ImageIcon("image/BossPlane/plane_" + bossid + ".png").getImage();
                    Enemy enemy = new Enemy(this, 180, 0, 0, eImg);
                    currentEnemy = enemy;
                    enemy.bossblood = 15;
                    enemy.bossid = 4;
                    enemy.eTime = 15;
                    enemies.add(enemy);
                    bossid++;
                }
                //创建第二个boss，将在60s后上场
                if (bossid == 5 && (System.currentTimeMillis() - startTime) > 60000) {
                    Image eImg = new ImageIcon("image/BossPlane/plane_" + bossid + ".png").getImage();
                    Enemy enemy = new Enemy(this, 180, 0, 0, eImg);
                    currentEnemy = enemy;
                    enemy.bossblood = 30;
                    enemy.bossid = 5;
                    enemy.eTime = 15;
                    enemies.add(enemy);
                    bossid++;
                }
                //创建第三个boss，将在120s后上场
                if (bossid == 6 && (System.currentTimeMillis() - startTime) > 120000) {
                    Image eImg = new ImageIcon("image/BossPlane/plane_" + bossid + ".png").getImage();
                    Enemy enemy = new Enemy(this, 180, 0, 0, eImg);
                    currentEnemy = enemy;
                    enemy.bossblood = 60;
                    enemy.bossid = 6;
                    enemy.eTime = 15;
                    enemies.add(enemy);
                    bossid++;
                }
                //敌机各添加子弹
                if (count % 80 == 0) {
                    for (int i = 0; i < enemies.size(); i++) {
                        if (enemies.get(i).eSpeed != 0) {//当前只为普通敌机添加子弹
                            Buttet ebuttet = new Buttet(enemies.get(i).ex + 20, enemies.get(i).ey + 25, bImg,
                                    3, enemies.get(i).eSpeed + 1);//子弹速度设置为敌机速度加一
                            enemies.get(i).Ebuttets.add(ebuttet);
                        }
                    }
                }
                //boss机添加子弹
                if (currentEnemy != null && count % 20 == 0) {
                    Random random = new Random();
                    //不同boss机添加子弹的样式和频率不同
                    if (currentEnemy.bossid == 4 && Enemy.bulletid == 4) {
                        bimage = bImg1;
                        m = 2;
                    } else if (currentEnemy.bossid == 5 && Enemy.bulletid == 5) {
                        bimage = bImg2;
                        m = 3;
                    } else if (currentEnemy.bossid == 6 && Enemy.bulletid == 6) {
                        bimage = bImg3;
                        m = 3;
                    }
                    if ((m == 2 || m == 3) && random.nextInt() % m == 0) {
                        Buttet ebuttet = new Buttet(currentEnemy.ex + 50, currentEnemy.ey + 25, bimage, 3, random.nextInt(2) + 2);
                        currentEnemy.Ebuttets.add(ebuttet);
                        m = 1;
                    }
                }

                for (int i = 0; i < buttets.size(); i++) {//炮弹移动
                    Buttet buttet2 = buttets.get(i);
                    //判断子弹发射方向，不同操作执行不同移动方法
                    if (buttet2.direction == 0) {
                        buttet2.moveBullet0();
                    } else if (buttet2.direction == 1) {
                        buttet2.moveBullet1();
                    } else {
                        buttet2.moveBullet2();
                    }
                }
                //敌方子弹移动
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    if (enemy.eSpeed == 0) {//敌机为boss
                        for (int j = 0; j < enemy.Ebuttets.size(); j++) {
                            Buttet ebuttet2 = enemy.Ebuttets.get(j);
                            ebuttet2.moveBulletboss();//boss发射子弹移动
                        }
                    } else {
                        //普通敌机子弹移动
                        for (int j = 0; j < enemy.Ebuttets.size(); j++) {
                            Buttet ebuttet2 = enemy.Ebuttets.get(j);
                            ebuttet2.moveBullet3();
                        }
                    }
                }

                //敌机移动
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy2 = enemies.get(i);
                    if (count % 4 == 0 && enemy2.eSpeed == 0) {
                        enemy2.moveBoss();//boss机横向移动过程
                    } else {
                        enemy2.moveEnemy();//普通敌机移动过程
                    }
                }
                //子弹和敌机相撞
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    for (int j = 0; j < buttets.size(); j++) {
                        Buttet buttet = buttets.get(j);
                        enemy.hint(buttet, enemy);
                    }
                }

                //子弹和英雄机相撞
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    for (int j = 0; j < enemy.Ebuttets.size(); j++) {
                        Buttet buttet = enemy.Ebuttets.get(j);
                        enemy.phint(buttet, this);
                        if (enemy.D) {//子弹和英雄机相撞标识为true
                            if (buttet.B == true) {//该子弹标识为true
                                enemy.Ebuttets.remove(j);
                                //break;
                            }
                        }
                    }
                }
                //清理死亡敌机
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    if (enemy.exist == false && enemy.eTime <= 0) {//敌机死亡且爆炸时间为0
                        enemies.remove(i);//移除敌机
                        i--;//避免因移除敌机而少遍历
                    } else {
                        if (enemy.exist == false) {//爆炸时间大于零
                            enemy.eTime--;
                            if (enemy.eSpeed == 0) {
                                //显示boss爆炸图
                                enemy.eImg = new ImageIcon("image/blast/blast_3.png").getImage();
                            } else {
                                //普通敌机爆炸图
                                enemy.eImg = new ImageIcon("image/blast/blast_1.png").getImage();
                            }
                            //enemy.eImg = new ImageIcon("image/blast/blast_1.png").getImage();
                        }
                    }
                }
                //英雄机撞到敌机的效果，撞boss机直接结束
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    enemy.hint2(enemy, this);
                    if (enemy.exist == false && enemy.Y) {//英雄机撞到敌机，标识为true，敌机死亡
                        enemies.remove(i);
                        break;
                    }
                }
                //英雄机死亡照片
                if (Pexist == false) {
                    blood--;//英雄机扣血
                    Pexist = true;
                }
                y++;
                if (y == 0) {
                    y = -5400;
                    try {
                        startImg = ImageIO.read(new File("image/background/background_2.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;//计数
                repaint();//重绘
            }
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //鼠标移动到开始游戏的框内鼠标做改变
        if (ck && e.getX() >= 131 && e.getX() <= 260 && e.getY() >= 390 && e.getY() <= 430) {
            //设置鼠标状态
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else if (ck == false) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        px = e.getX() - p[pc].getWidth() / 2;
        py = e.getY() - p[pc].getHeight() / 2;
        if (ck == false && py <= 0) {//如果到了上边
            py = 0;
        }
        if (ck == false && px <= 0) {//如果到了左边
            px = 0;
        }
        //右边
        if (ck == false && px + p[pc].getWidth() >= this.getWidth()) {//如果到了右边
            px = this.getWidth() - p[pc].getWidth();
        }
        //下边
        if (ck == false && py + p[pc].getHeight() >= this.getHeight()) {//如果到了右边
            py = this.getHeight() - p[pc].getHeight();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {


        //只能在游戏外面打开和关闭
        //音乐打开和关闭
        if (ck && e.getModifiers() == e.BUTTON1_MASK && musickg == true && e.getX() <= 380 && e.getX() >= 360 && e.getY() >= 0 && e.getY() <= 80) {
            music.stopMusic();
            mg = new ImageIcon("image/close.PNG");
            musickg = false;
            System.out.println("关闭声音");
            repaint();
        } else if (ck && e.getModifiers() == e.BUTTON1_MASK && musickg == false && e.getX() >= 360 && e.getX() <= 400 && e.getY() >= 0 && e.getY() <= 80) {
            music.stopMusic();
            mg = new ImageIcon("image/open.PNG");
            musickg = true;
            music.play();
            System.out.println("打开声音");
            repaint();
        }
        //进入游戏
        if (ck && e.getModifiers() == e.BUTTON1_MASK && e.getX() >= 131 && e.getX() <= 260 && e.getY() >= 390 && e.getY() <= 430) {
            ck = false;
            over = false;
            gonggao=false;
            startTime = System.currentTimeMillis();//游戏开始时间
            try {
                //加载背景图片
                startImg = ImageIO.read(new File("image/background/background_2.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            y = -5400;

            /*if(t.getState()== Thread.State.NEW){
                t.start();
            }else{
                this.run();
            }*/

            t.start();

        }
        //右键暂停
        if (ck == false && e.getModifiers() == e.BUTTON3_MASK) {
            suspend = suspend ? resume() : suspend();//三目运算
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    //定义两个方法,一个暂停的方法,一个是唤醒的方法
    public boolean suspend() {
        suspend = true;
        return suspend;
    }

    public synchronized boolean resume() {
        suspend = false;
        notify();//是将线程唤醒的方法
        return suspend;
    }
}
