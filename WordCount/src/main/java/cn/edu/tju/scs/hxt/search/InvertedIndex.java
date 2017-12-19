package cn.edu.tju.scs.hxt.search;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/12/18 0018.
 */

public class InvertedIndex {

    /**
     * map
     */
    public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
        private Text keyInfo = new Text();  // 存储单词和URI的组合
        private Text valueInfo = new Text(); //存储词频
        private FileSplit split;  // 存储split对象。
        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            // 获得<key,value>对所属的FileSplit对象。
            split = (FileSplit) context.getInputSplit();
            // 自定义停用词
            String st=value.toString();
            String[] self_stop_words = {  "了",  "，",  "：", "," };
            CharArraySet cas = new CharArraySet( 0, true);
            for (int i = 0; i < self_stop_words.length; i++) {
                cas.add(self_stop_words[i]);
            }
            // 加入系统默认停用词
            Iterator<Object> itor = SmartChineseAnalyzer.getDefaultStopSet().iterator();
            while (itor.hasNext()) {
                cas.add(itor.next());
            }

            // TODO: 可以更换更 NB 的分词器
            // 中英文混合分词器(其他几个分词器对中文的分析都不行)
            SmartChineseAnalyzer sca = new SmartChineseAnalyzer(cas);

            // TODO: TokenStream 啥意思？
            TokenStream ts = sca.tokenStream("field", st);
            // TODO: CharTermAttribute 又是啥意思？
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);

            ts.reset();
            while (ts.incrementToken()) {
                String path=split.getPath().toString();
                System.out.println("path====="+path);
                int indexone=path.lastIndexOf("/");
                int indextow=path.lastIndexOf(".");
                String path1=path.substring(indexone+1,indextow);
                //System.out.println("path1====="+path1);
                String path2=path1.replaceAll("-","/");
                String path3=path2.replaceAll("#", "？");
                keyInfo.set(ch.toString()+":"+path3);
                valueInfo.set("1");
                context.write(keyInfo, valueInfo);
            }
            ts.end();
            ts.close();
        }
    }

    public static class InvertedIndexCombiner extends Reducer<Text, Text, Text, Text> {
        private Text info = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //System.out.println("***Combiner***values===="+values.toString());
            //统计词频
            int sum = 0;
            for (Text value : values) {
                sum += Integer.parseInt(value.toString() );
            }
            //System.out.println("--Combiner----sum====="+sum);
            int splitIndex = key.toString().indexOf(":");

            //重新设置value值由URI和词频组成
            info.set(key.toString().substring(splitIndex + 1) +":"+sum );

            //重新设置key值为单词
            key.set(key.toString().substring(0,splitIndex));
            context.write(key, info);
        }
    }


    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text>{
        private Text result = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //生成文档列表
            String fileList = new String();
            for (Text value : values) {
                fileList += value.toString()+";";
            }
            result.set(fileList);
            context.write(key, result);
        }

    }

    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf,"InvertedIndex");
            job.setJarByClass(InvertedIndex.class);
            //实现map函数，根据输入的<key,value>对生成中间结果。
            job.setMapperClass(InvertedIndexMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setCombinerClass(InvertedIndexCombiner.class);
            job.setReducerClass(InvertedIndexReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path("hdfs://192.168.61.128:9000/sohu/"));
            FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.61.128:9000/sohuout/"+System.currentTimeMillis()+"/"));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
