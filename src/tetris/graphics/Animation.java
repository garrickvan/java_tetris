package tetris.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
	实现动画播放
*/
public class Animation {

    private ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;


    /**
        初始化字段
    */
    public Animation() {
        frames = new ArrayList();
        totalDuration = 0;
        start();
    }


    /**
        添加指定延时的帧
    */
    public synchronized void addFrame(Image image,
        long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }


    /**
		重置动画播放时间及播放帧索引，重新开始播放动画
    */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }


    /**
        根据需要，更新下一帧
    */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }


    /**
		获取当前帧的图像，如果没有图像返回null
    */
    public synchronized Image getImage() {
        if (frames.size() > 0) {
            return getFrame(currFrameIndex).image;
        }
        else {
            return null;
        }
    }


    private AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }

	/**
		内嵌的动画帧类
	 **/
    private class AnimFrame {

        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }
}
