package cn.edu.tju.scs.hxt.weibo;

/**
 * Created by Takahashi on 2017/12/20.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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


public class IndexOne_csv {


    public static class MapOne extends Mapper<LongWritable, Text,Text,LongWritable>
    {
        @Override
        protected void map(LongWritable key, Text value,Context context)
                throws IOException, InterruptedException {
            // 需求： 将 hello world  --》 hello--a.txt 1  world-->a.txt  1
            //1.取出一行数据 转为字符串
            String line = value.toString();
            System.out.println("string: "+line);
            //line byte size
            int lineLength = line.getBytes("utf-8").length;
            //2.将这一行数据按照指定的分割符进行第一次切分
            String[] fields = StringUtils.split(line,"\",\"");
            String indexString = fields[2] + fields[9];
            System.out.println(indexString);

//            String[] words = StringUtils.split(line," ");
            //3.获取读取的文件所属的文件切片
            Configuration conf = new Configuration();
            FileSystem fs  = FileSystem.get(conf);
            //4.使用context参数来获取子对象
            FileSplit inputSplit=(FileSplit)context.getInputSplit();
            //5,使用切片对象来获取文件名称
            String filename = inputSplit.getPath().getName();
            //6.讲数据进行传递

            String[] self_stop_words = {  "了",  "，",  "：", "," };
            CharArraySet cas = new CharArraySet(0, true);
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
            TokenStream ts = sca.tokenStream("field", indexString);
            // TODO: CharTermAttribute 又是啥意思？
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);

            ts.reset();
            //7，封装数据输出格式为 k: hello-->[offset:length] v:1
            while (ts.incrementToken()) {
                System.out.println(ch.toString());
                String location = "[" + key.toString() + ":" + lineLength + "]";
                context.write(new Text(ch.toString()+"-->"+location), new LongWritable(1));
            }


        }
    }
    //======================reduce============================
    public static class ReduceOne extends Reducer<Text, LongWritable,Text,LongWritable>
    {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values,
                              Context context) throws IOException, InterruptedException {

            //1.reduce 从map端接受来的数据格式为 hello-->a.txt 1,1,1
            //2,定义for循环将values集合中的数据进行累加
            long count = 0;
            for(LongWritable value:values)
            {
                count += value.get();
            }
            context.write(key,new LongWritable(count));
        }
    }

    //===================主方法提交job==========================
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1.获取job对象
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        //2.指定jar包的类
        job.setJarByClass(IndexOne_csv.class);
        //3.指定map（）的类
        job.setMapperClass(MapOne.class);
        //4.指定reducer()的类
        job.setReducerClass(ReduceOne.class);
        //5,指定输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        //6,指定文件输入路径
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        //7,进行健壮型判断如果存储文件已经存在则删除

//        Path output = new Path(args[1]);
//        FileSystem fs = FileSystem.get(conf);
//        if(fs.exists(output))
//        {
//            fs.delete(output,true);
//        }
        //8，指定文件的输出路径
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        //9，提交任务
        System.out.print("111");
        System.exit(job.waitForCompletion(true)?0:1);
    }

}
