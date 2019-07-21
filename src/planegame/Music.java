package planegame;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Music {
	//音乐存放的相对路径
	File f=new File("planeMusic.mp3");
	Media media=new Media(f.toURI().toString());
	final JFXPanel fxPanel=new JFXPanel();
	MediaPlayer mediaPlayer=new MediaPlayer(media);

	//音乐播放方法
	public void play(){
		mediaPlayer.play();
	}
	//音乐暂停方法
	public void stopMusic(){
		mediaPlayer.stop();
	}

}
