package cn.edu.tju.scs.hxt;
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCountAndSort {

	public static class TokenizerMapper 
	extends Mapper<Object, Text, Text, IntWritable>{

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				
				//To Do	job1: filter and uppercase the input data			
			}
		}
	}

	public static class IntSumReducer 
	extends Reducer<Text,IntWritable,Text,IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, 
				Context context
				) throws IOException, InterruptedException {
			int sum = 0;

			//To Do	job1: word count

			result.set(sum);
			context.write(key, result);
		}
	}
	
	
	private static class MyComparator extends IntWritable.Comparator {
		
		@Override
		public int compare(Object a, Object b) {
			//To Do	job2: sort by descending order
			return 1;
		}
		
		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			//To Do	job2: sort by descending order
			return 1;
		} 
		
	}
	
	public static void main(String[] args) throws Exception {
		//Don’t change!
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		for (String string : otherArgs) {
			System.out.println(string);
		}
		if (otherArgs.length < 2) {
			System.err.println("Usage: wordcount <in> [<in>...] <out>");
			System.exit(2);
		}
		//Don’t change!
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCountAndSort.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		for (int i = 0; i < otherArgs.length - 1; ++i) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		Path tempDir = new Path("wordcount-temp-" + Integer.toString(  
	            new Random().nextInt(Integer.MAX_VALUE)));
		System.out.println(tempDir.toString());
		FileOutputFormat.setOutputPath(job, tempDir);
		
		job.waitForCompletion(true);
		
        	Job sortJob = Job.getInstance(conf, "sort");
       		sortJob.setJarByClass(WordCountAndSort.class);  
	
        	FileInputFormat.addInputPath(sortJob, tempDir);  
        	sortJob.setInputFormatClass(SequenceFileInputFormat.class); 
 
        	sortJob.setMapperClass(InverseMapper.class);
        	sortJob.setNumReduceTasks(1);   
        	FileOutputFormat.setOutputPath(sortJob, new Path(otherArgs[1]));  

        	sortJob.setOutputKeyClass(IntWritable.class);  
        	sortJob.setOutputValueClass(Text.class);
        
		//To Do	job2: set the comparator as MyComparator(the class above)	
        	
        	System.out.println("The first job finished.");
        	System.exit(sortJob.waitForCompletion(true) ? 0 : 1); 
	

	}
}
