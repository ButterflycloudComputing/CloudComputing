package cn.edu.tju.scs.hxt.pojo.dto;

/**
 * Created by haoxiaotian on 2017/12/20 17:34.
 */
public class PositionInfo implements Comparable<PositionInfo>{

    private long startPos;
    private int length;
    private int score;

    public PositionInfo() {
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "PositionInfo{" +
                "startPos=" + startPos +
                ", length=" + length +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(PositionInfo o2) {
        // TODO Auto-generated method stub
        int numbera = this.getScore();
        int numberb = o2.getScore();
        if(numberb > numbera)
        {
            return 1;
        }
        else if(numberb<numbera)
        {
            return -1;
        }
        else
        {
            return 0;
        }

    }
}
