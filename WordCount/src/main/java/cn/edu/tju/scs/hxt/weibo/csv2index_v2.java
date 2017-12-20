package cn.edu.tju.scs.hxt.weibo;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
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
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Takahashi on 2017/12/20.
 */
public class csv2index_v2 {
    public static class MapOne extends Mapper<LongWritable, Text,Text,Text>
    {
        @Override
        protected void map(LongWritable key, Text value,Context context)
                throws IOException, InterruptedException {
            // 需求： 将 hello world  --》 hello--a.txt 1  world-->a.txt  1
            //1.取出一行数据 转为字符串
            String line = value.toString();
//            System.out.println("string: "+line);
            //line byte size
            int lineLength = line.getBytes("utf-8").length;
            //2.将这一行数据按照指定的分割符进行第一次切分
            String[] fields = StringUtils.split(line,"\",\"");
            String subString1 = fields[2].toLowerCase();
            String subString2 = fields[9].toLowerCase();
//            String indexString = subString1 + subString2;
//            System.out.println(indexString);

//            String[] words = StringUtils.split(line," ");
            //3.获取读取的文件所属的文件切片
            Configuration conf = new Configuration();
            FileSystem fs  = FileSystem.get(conf);
            //4.使用context参数来获取子对象
            FileSplit inputSplit=(FileSplit)context.getInputSplit();
            //5,使用切片对象来获取文件名称
            String filename = inputSplit.getPath().getName();
            //6.讲数据进行传递

            String location = "[" + key.toString() + ":" + lineLength + "]";

            Analyzer ay1 = new ComplexAnalyzer();
            TokenStream ts1 = ay1.tokenStream("field", subString1);

            CharTermAttribute ch1 = ts1.addAttribute(CharTermAttribute.class);

            Map<String, Integer> words_offset1 = new HashMap<String, Integer>();
            ts1.reset();
            //7，封装数据输出格式为 k: hello-->[offset:length] v:1
            while (ts1.incrementToken()) {

//                System.out.println(ch1.toString());
                String word = ch1.toString();
                int offset = words_offset1.get(word) == null ? 0:words_offset1.get(word);
                int pos = subString1.indexOf(word, offset);
                words_offset1.put(word, pos+1);

                context.write(new Text(word+"-->"+location), new Text("0:" + pos));
            }

            if(subString2 != null && !subString2.equals(""))
            {
                Analyzer ay2 = new ComplexAnalyzer();
                TokenStream ts2 = ay2.tokenStream("field", subString2);

                CharTermAttribute ch2 = ts2.addAttribute(CharTermAttribute.class);

                Map<String, Integer> words_offset2 = new HashMap<String, Integer>();
                ts2.reset();
                //7，封装数据输出格式为 k: hello-->[offset:length] v:1
                while (ts2.incrementToken()) {

//                    System.out.println(ch2.toString());
                    String word = ch2.toString();
                    if(word.equals("tmt"))
                        System.out.println("line" + line);
                    int offset = words_offset2.get(word) == null ? 0:words_offset2.get(word);
                    int pos = subString2.indexOf(word, offset);
                    words_offset2.put(word, pos+1);

                    context.write(new Text(word+"-->"+location), new Text("1:" + pos));
                }
            }
        }
    }
    //======================reduce============================
    public static class ReducerOne extends Reducer<Text, Text,Text,Text>
    {
        @Override
        protected void reduce(Text key, Iterable<Text> values,
                              Context context) throws IOException, InterruptedException {

            //1.reduce 从map端接受来的数据格式为 hello-->a.txt 1,1,1
            //2,定义for循环将values集合中的数据进行累加
            int count = 0;
            String pos_str = "<";
            for(Text value:values)
            {
                count++;
                pos_str += value + ",";
            }
            pos_str += ">," + count;

//            System.out.println("key" + key.toString());
            String[] words = StringUtils.split(key.toString(),"-->");
            //4,获取字段的内容
            String word = words[0];
            String filename=words[1];

            //5.将字段的内容重新组合进行输出 输出格式为  google a.txt-->2;
            context.write(new Text(word), new Text(filename+"-->"+pos_str));
        }
    }

    public static class ReducerTwo extends Reducer<Text, Text,Text,Text>
    {
        @Override
        protected void reduce(Text key, Iterable<Text> values,Context context)
                throws IOException, InterruptedException {

            //1,从map端接受数据 定义链接符
            String link = "";
            for(Text value:values)
            {
                //2将集合中的数据取出 进行链接
                link+=value+" ";
            }
            //3输出数据 输出格式为 google a.txt-->2 b.txt-->1
            context.write(key,new Text(link));

        }
    }

    //===================主方法提交job==========================
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1.获取job对象
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        for (String string : otherArgs) {
            System.out.println(string);
        }

//        if (otherArgs.length < 2) {
//            System.err.println("Usage: indexMerge <in> [<in>...] <out>");
//            System.exit(2);
//        }

        Job job = Job.getInstance(conf, "index_mr");
        //2.指定jar包的类
        job.setJarByClass(csv2index_v2.class);
        //3.指定map（）的类
        job.setMapperClass(MapOne.class);
        job.setCombinerClass(ReducerOne.class);
        //4.指定reducer()的类
        job.setReducerClass(ReducerTwo.class);
        //5,指定输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

//        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        //6,指定文件输入路径
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
