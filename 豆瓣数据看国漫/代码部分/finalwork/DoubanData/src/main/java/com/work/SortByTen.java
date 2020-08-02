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

//按时间段划分数据
public class SortByTen {
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

    public static class SortByTenMapper
            extends Mapper<Object, BSONObject, Text, BSONWritable> {


        static int year = 1940;

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            if(year<2010) {
                if (Integer.parseInt(value.get("year").toString()) >= (year + 10))
                    year += 10;
            }else if (Integer.parseInt(value.get("year").toString()) >= (year + 5))
                year += 5;
            context.write(new Text(String.valueOf(year)), new BSONWritable(value));
        }
    }

    public static class SortByTenReducer
            extends Reducer<Text,BSONWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<BSONWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            int com_num = 0;
            int view_num = 0;
            int co_num = 0;
            float length1 = 0;
            float length0 = 0;
            while (value.hasNext()){
                BSONWritable cc = (BSONWritable)value.next();
                BSONObject res = cc.getDoc();
                length0 = length0 + Float.parseFloat(res.get("length0").toString());
                length1 = length1 + Float.parseFloat(res.get("length1").toString());
                co_num = co_num+Integer.parseInt(res.get("co_num").toString());
                com_num = com_num+Integer.parseInt(res.get("com_num").toString());
                view_num = view_num+Integer.parseInt(res.get("view_num").toString());
            }
            float rate = ((float)com_num)/view_num;
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("years",key.toString());
            data.put("rate",rate);
            data.put("co_num",co_num);
            data.put("length1",length1);
            data.put("length0",length0);
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.YearData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.TenData");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(SortByTen.class);
        job.setMapperClass(SortByTenMapper.class);
        job.setReducerClass(SortByTenReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BSONWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}