package cn.edu.tju.scs.hxt;

import cn.edu.tju.scs.hxt.pojo.dto.PositionInfo;
import cn.edu.tju.scs.hxt.pojo.vo.WeiBo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haoxiaotian on 2017/12/20 4:06.
 */
public class WordsTable {

    private String indexFile;

    private HashMap<String,String> wordsTable;

    public void init(){
        System.out.println(indexFile);

        wordsTable = new HashMap<>();
        String key,value;
        String [] keyAndValue;
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(indexFile), conf);
            FSDataInputStream hdfsInStream = fs.open(new Path(indexFile));
            InputStreamReader isr = new InputStreamReader(hdfsInStream, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
// int k = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                keyAndValue = line.split("\t");
//                System.out.println("长度： " + keyAndValue.length);
//                System.out.println(keyAndValue[0]);
//                System.out.println(keyAndValue[1]);
                assert(keyAndValue.length == 2);
                key = keyAndValue[0];
                value = keyAndValue[1];
                wordsTable.put(key,value);
            }
//            HdfsOperate.readFile("hdfs://localhost:9000/weibo_output/part-r-00000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key){
        return wordsTable.get(key);
    }

    public List<WeiBo> randomRead(String hdfsPath, List<PositionInfo> positionInfos){
        FSDataInputStream hdfsInStream = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String [] info;
        int lastIdx;
        List<WeiBo> result = new ArrayList<>();
        WeiBo weiBo;
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
            hdfsInStream = fs.open(new Path(hdfsPath));
            for(PositionInfo pos: positionInfos){
                hdfsInStream.seek(pos.getStartPos());
                isr = new InputStreamReader(hdfsInStream, "utf-8");
                br = new BufferedReader(isr);
                String line;
                line = br.readLine();
                if(line != null) {
                    System.out.println(line);
                    info = line.split("\",\"");
                    info[0] = info[0].substring(1);
                    lastIdx = info.length-1;
                    info[lastIdx] = info[lastIdx].substring(0,info[lastIdx].length()-1);
                    assert info.length == 14;
                    weiBo = new WeiBo(info);
                    result.add(weiBo);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(hdfsInStream != null)
                    hdfsInStream.close();
                if(isr != null)
                    isr.close();
                if(br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}
