package com.work;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//找出个人参与制作国漫数量
public class ManList {
    static{
        // 在 static code 中设置操作 HDFS 的用户信息
        // 设置宿主机系统环境变量 HADOOP_USER_NAME == icss
        System.setProperty("HADOOP_USER_NAME", "icss");
        // 获取运行环境的 OS 信息，视需要设置 hadoop 主目录
        String osInfo = System.getProperty("os.name");
        if (osInfo.toLowerCase().indexOf("windows") != -1){
            System.setProperty("hadoop.home.dir", "d:/hadoop/hadoop-3.1.3");
            System.setProperty("hadoop.tmp.dir", "d:/mrtmp");
        }

    }

    public static class ManMapper
            extends Mapper<Object, BSONObject, Text, IntWritable> {

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            List<String> director = (ArrayList<String>)value.get("导演");
            List<String> writter = (ArrayList<String>)value.get("编剧");
            List<String> actor = (ArrayList<String>)value.get("主演");
            for(String name : director){
                context.write(new Text(name),new IntWritable(Integer.parseInt(value.get("年份").toString().substring(1,5))));
            }
            for(String name : writter){
                context.write(new Text(name),new IntWritable(Integer.parseInt(value.get("年份").toString().substring(1,5))));
            }
            for(String name : actor){
                context.write(new Text(name),new IntWritable(Integer.parseInt(value.get("年份").toString().substring(1,5))));
            }
        }
    }

    public static class ManReducer
            extends Reducer<Text,IntWritable, NullWritable, BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            int sum = 0;
            int year0 = 9999;
            while (value.hasNext()){
                IntWritable year = (IntWritable) value.next();
                sum++;
                if(year.get()<year0)
                    year0=year.get();
            }
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("name",key.toString());
            data.put("year",year0);
            data.put("num",sum);
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.OriginData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.ManData");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(ManList.class);
        job.setMapperClass(ManMapper.class);
        job.setReducerClass(ManReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}