package com.work;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.hadoop.conf.Configuration;
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

//按年份统计标签数量
public class TagCount {
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

    static int year = 1920;

    static List<String> tags = new ArrayList<>();

    public static Boolean isexit(String type){
        for(String i:tags){
            if(i.equals(type))
                return true;
        }
        return false;
    }

    public static int loc(String tag){
        for(int i=0;i<tags.size();i++){
            if(tags.get(i).equals(tag))
                return i;
        }
        return -1;
    }

    public static class TagMapper
            extends Mapper<Object, BSONObject, Text, BSONWritable> {

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            if(Integer.parseInt(value.get("year").toString())>=(year+10))
                year+=10;
            List<String> list = (List<String>)value.get("tags");
            for(String i:list){
                if(!isexit(i))
                    tags.add(i);
            }
            context.write(new Text(String.valueOf(year)), new BSONWritable(value));
        }
    }

    public static class TagReducer
            extends Reducer<Text,BSONWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<BSONWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            int[] allnum = new int[tags.size()];
            while (value.hasNext()){
                BSONWritable cc = (BSONWritable)value.next();
                BSONObject res = cc.getDoc();
                List<String> list = (List<String>)res.get("tags");
                for(String i:list){
                    int loc = loc(i);
                    if(loc!=-1)
                        allnum[loc]++;
                }
            }
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("year",key.toString());
            for(int i=0;i<tags.size();i++){
                if(allnum[i]>5)
                    data.put(tags.get(i),allnum[i]);
            }
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PreData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.test");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(TagCount.class);
        job.setMapperClass(TagMapper.class);
        job.setReducerClass(TagReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BSONWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}