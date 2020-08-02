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

//各国合拍数量
public class PieCo {
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

    public static class CoMapper
            extends Mapper<Object, BSONObject, Text, IntWritable> {

        static IntWritable one = new IntWritable(1);
        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            List<String> place = (ArrayList<String>)value.get("place");
            if(place.size()>1){
                for(int i=0;i<place.size();i++) {
                    String loc = place.get(i).replace(" ","");
                    if (!loc.equals("中国大陆"))
                        context.write(new Text(loc), one);
                }
            }
        }
    }

    public static class CoReducer
            extends Reducer<Text,IntWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            int sum = 0;
            while (value.hasNext()){
                value.next();
                sum++;
            }
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("place",key.toString());
            data.put("num",sum);
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PreData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.CoData");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(PieCo.class);
        job.setMapperClass(CoMapper.class);
        job.setReducerClass(CoReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}