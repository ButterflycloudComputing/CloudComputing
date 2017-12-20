package cn.edu.tju.scs.hxt;

import cn.edu.tju.scs.hxt.pojo.dto.PositionInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * Created by haoxiaotian on 2017/12/20 14:15.
 */
public class Main {

    public static void main(String args[]){
        WordsTable wt = new WordsTable();
        wt.init();

        String values = wt.getValue("鼓掌");
        String [] valuesArray = values.split(" ");
        PositionInfo info;
        String [] infoArray;
        String pos,startPos,stopPos;
        List<PositionInfo> results = new ArrayList<>();
        if(valuesArray.length > 0){

            for(String item : valuesArray){
                infoArray = item.split("-->");
                assert(infoArray.length == 2);
                info = new PositionInfo();
                info.setScore(Integer.valueOf(infoArray[1]));
                pos = infoArray[0].substring(1,infoArray[0].length()-1);
                infoArray = pos.split(":");
                info.setStartPos(Long.valueOf(infoArray[0]));
                info.setLength(Integer.valueOf(infoArray[1]));
                results.add(info);
            }

            Collections.sort(results);

//            Comparator<PositionInfo> OrderDec =  new Comparator<PositionInfo>(){
//                public int compare(PositionInfo o1, PositionInfo o2) {
//                    // TODO Auto-generated method stub
//                    int numbera = o1.getScore();
//                    int numberb = o2.getScore();
//                    if(numberb > numbera)
//                    {
//                        return 1;
//                    }
//                    else if(numberb<numbera)
//                    {
//                        return -1;
//                    }
//                    else
//                    {
//                        return 0;
//                    }
//
//                }
//
//
//
//            };
//
//            PriorityQueue<PositionInfo> queue = new PriorityQueue<>(valuesArray.length,OrderDec);
//            for(String item : valuesArray){
//                infoArray = item.split("-->");
//                assert(infoArray.length == 2);
//                info = new PositionInfo();
//                info.setScore(Integer.valueOf(infoArray[1]));
//                pos = infoArray[0].substring(1,infoArray[0].length());
//                infoArray = pos.split(":");
//                info.setStartPos(Long.valueOf(infoArray[0]));
//                info.setLength(Integer.valueOf(infoArray[1]));
//                queue.add(info);
//            }

        }

        System.out.println(results);


        wt.randomRead("",results);


    }
}
