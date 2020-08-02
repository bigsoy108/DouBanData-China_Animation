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

//对原始数据预处理
public class IntChange {

    //调用本地Hadoop
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


    public static class IntChangeMapper
            extends Mapper<Object, BSONObject, Text, BSONWritable> {

        public String LengthChange(String origin,String year){//规范时长格式
            //为缺少时长的数据进行补充
            if(origin.equals("无")){
                if(Integer.parseInt(year)>=2020)
                    return("0");
                else return ("5");
            }
            //以下均为规范时长格式，统一为以分钟为单位的浮点数
            int end = origin.indexOf("’");
            String len = new String(origin);
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            end = len.indexOf("'");
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            end = len.indexOf("′");
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            end = len.indexOf("‘");
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            end = len.indexOf("m");
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            end = len.indexOf("分");
            if(end>-1){
                len = new String(len.substring(0,end));
            }
            return len;
        }

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            String view = value.get("看过").toString().replace("人看过","");
            String year = value.get("年份").toString().substring(1,5);
            String flag = value.get("是电影").toString();
            BasicBSONObject res = new BasicBSONObject() ;
            res.put("id",value.get("ID").toString());
            res.put("title",value.get("标题").toString());

            //分别对两种类型的动画统计时长
            if(flag.equals("1")) {
                res.put("len1",LengthChange(value.get("片长").toString(),year));
                res.put("len2","1");
            }else {
                List<String> length0 =(ArrayList<String>)value.get("片长");
                String len1 = new String(LengthChange(length0.get(1),year));
                String len2  = new String(length0.get(0));
                res.put("len1",len1);
                res.put("len2",len2);
            }
            res.put("year",year);
            if(value.get("评分").toString().equals("暂无评分"))
                res.put("score","0");
            else res.put("score",value.get("评分").toString());
            res.put("com_num",value.get("评分人数").toString()) ;
            res.put("place",value.get("制片国家/地区")) ;
            res.put("flag",flag) ;
            res.put("tags",value.get("豆瓣成员标签")) ;
            res.put("view_num",view) ;
            res.put("type",value.get("类型")) ;
            context.write(new Text(value.get("ID").toString()), new BSONWritable(res));
        }
    }

    public static class IntChangeReducer
            extends Reducer<Text,BSONWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<BSONWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            BSONWritable cc = (BSONWritable) value.next();
            context.write(null,cc);
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();

        //设置输入输出地址
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.OriginData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PreData");
        Job job = Job.getInstance(conf,"Mongo Connection");

        //设置要调用的类
        job.setJarByClass(IntChange.class);
        job.setMapperClass(IntChangeMapper.class);
        job.setReducerClass(IntChangeReducer.class);

        //设置输入输出格式
        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BSONWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);

        //运行
        job.waitForCompletion(true);

    }
}