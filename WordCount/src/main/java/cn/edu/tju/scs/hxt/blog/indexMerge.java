package cn.edu.tju.scs.hxt.blog;

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
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Random;


public class indexMerge {

    public static class MapOne extends Mapper<LongWritable, Text,Text,LongWritable>
    {
        @Override
        protected void map(LongWritable key, Text value,Context context)
                throws IOException, InterruptedException {
            // 需求： 将 hello world  --》 hello--a.txt 1  world-->a.txt  1
            //1.取出一行数据 转为字符串
            String line = value.toString();
            //2.将这一行数据按照指定的分割符进行第一次切分
            String[] words = StringUtils.split(line," ");
            //3.获取读取的文件所属的文件切片
            Configuration conf = new Configuration();
            FileSystem fs  = FileSystem.get(conf);
            //4.使用context参数来获取子对象
            FileSplit inputSplit=(FileSplit)context.getInputSplit();
            //5,使用切片对象来获取文件名称
            String filename = inputSplit.getPath().getName();
            //6.讲数据进行传递
            for(String word:words)
            { //7，封装数据输出格式为 k: hello-->a.txt v:1
                context.write(new Text(word+"-->"+filename), new LongWritable(1));
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

    // --------------------map()-------------------------------
    // google-->c.txt1
    // hadoop-->a.txt2
    //以上形式为上一次mapreduce的输出形式 要作为这一次maprede的map的输入形式
    // 经过map()操作后 输出形式为 google c.txt-->1
    public static class MapTwo extends Mapper<LongWritable,Text,Text,Text>
    {
        @Override
        protected void map(LongWritable key, Text value,Context context)
                throws IOException, InterruptedException {
            //1.将读到的每一行数据转换为String类型进行操作
            String line = value.toString();
            //2.以指定的格式进行切分
            String[] filds = StringUtils.split(line,"\t");
            //3，上面哪个第一次切分 后的结果为 google-->c.txt    1
            // 下面第二次且分  后的结果为 google c.txt 1
            String[] words = StringUtils.split(filds[0],"-->");
            //4,获取字段的内容
            String word = words[0];
            String filename=words[1];
            long count = Long.parseLong(filds[1]);
            //5.将字段的内容重新组合进行输出 输出格式为  google a.txt-->2;
            context.write(new Text(word), new Text(filename+"-->"+count));
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

        Job job = Job.getInstance(conf, "mr_one");
        //2.指定jar包的类
        job.setJarByClass(indexMerge.class);
        //3.指定map（）的类
        job.setMapperClass(MapOne.class);
        //4.指定reducer()的类
        job.setReducerClass(ReduceOne.class);
        //5,指定输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

//        job.setOutputFormatClass(SequenceFileOutputFormat.class);

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
//        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        //9，提交任务

        Path tempDir = new Path("indexMerge-temp-" + Integer.toString(
                new Random().nextInt(Integer.MAX_VALUE)));
        System.out.println(tempDir.toString());
        FileOutputFormat.setOutputPath(job, tempDir);

        job.waitForCompletion(true);

        Job second_job = Job.getInstance(conf, "mr_two");
        second_job.setJarByClass(indexMerge.class);
        second_job.setMapperClass(MapTwo.class);
        second_job.setReducerClass(ReducerTwo.class);
//        second_job.setInputFormatClass(SequenceFileInputFormat.class);
        second_job.setOutputKeyClass(Text.class);
        second_job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(second_job, tempDir);
//        FileInputFormat.setInputPaths(second_job,new Path(args[0]));

//        Path output = new Path(args[1]);
//        FileSystem fs = FileSystem.get(conf);
//        if(fs.exists(output))
//        {
//            fs.delete(output,true);
//        }
        //8，指定文件的输出路径
        FileOutputFormat.setOutputPath(second_job,new Path(args[1]));

        System.exit(second_job.waitForCompletion(true)?0:1);
    }
}
