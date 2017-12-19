package cn.edu.tju.scs.hxt.weibo;

/**
 * Created by Takahashi on 2017/12/20.
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Iterator;
public class SearchDemo {
    public static class FindMapper extends Mapper<Text, Text, Text, Text> {
        @Override
        protected void map(Text key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                // 要处理的文本
                String searchString = "国际";


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
                TokenStream ts = sca.tokenStream("field", searchString);
                // TODO: CharTermAttribute 又是啥意思？
                CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
                // 自定义停用词

                ts.reset();
                while (ts.incrementToken()) {
                    if (key.toString().equals(ch.toString())) {
                        context.write(key, value);
                    }
                }
                ts.end();
                ts.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class FindReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String val=null;
            // 生成文档列表
            for (Text text : values) {
                System.out.println("********"+text.toString());

                String[] indexs = text.toString().split(" ");
//                for(int i=0;i<sts.length;i++){
//                    String stt=sts[i].toString().substring(0,sts[i].toString().indexOf(":"));
//                    val+=stt+";";
//                }
                Text value=new Text();
                value.set(val);
                context.write(key, value);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
            for (String string : otherArgs) {
                System.out.println(string);
            }

            Job job = Job.getInstance(conf, "search_demo");
            job.setJarByClass(SearchDemo.class);
            // 实现map函数，根据输入的<key,value>对生成中间结果。
            job.setMapperClass(FindMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setInputFormatClass(KeyValueTextInputFormat.class);
            job.setReducerClass(FindReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

//            FileInputFormat.addInputPath(job,
//                    new Path("hdfs://192.168.61.128:9000/sohuout/1462889264074/part-r-00000"));
//            FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.61.128:9000/sohufind/" + System.currentTimeMillis() + "/"));
            FileInputFormat.setInputPaths(job,new Path(args[0]));
            FileOutputFormat.setOutputPath(job,new Path(args[1]));


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
