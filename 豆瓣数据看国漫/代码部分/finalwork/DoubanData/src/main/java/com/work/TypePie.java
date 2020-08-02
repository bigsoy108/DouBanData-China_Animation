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

//按时间段划分，统计
public class TypePie {
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


    //记录类型的列表
    static List<String> types = new ArrayList<>();

    //记录对应位置类型数量的列表
    static List nums = new ArrayList<>();


    //查看该类型是否已被记录
    public static Boolean isexit(String type){
        for(String i:types){
            if(i.equals(type))
                return true;
        }
        return false;
    }

    //找出类型对应的位置
    public static int loc(String tag){
        for(int i=0;i<types.size();i++){
            if(types.get(i).equals(tag))
                return i;
        }
        return -1;
    }

    public static class PieMapper
            extends Mapper<Object, BSONObject, Text, BSONWritable> {

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            int y = Integer.parseInt(value.get("year").toString());

            //按时间段划分数据
            if(y<=1978)//1978年及以前
            context.write(new Text("1978"), new BSONWritable(value));
            else if(y<=2003)//1978-2003
                context.write(new Text("2003"), new BSONWritable(value));
            else//2003至今
                context.write(new Text("2020"), new BSONWritable(value));
        }
    }

    public static class PieReducer
            extends Reducer<Text,BSONWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<BSONWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();

            //清除上一时间段的记录
            types.clear();
            nums.clear();

            while (value.hasNext()){
                BSONWritable cc = (BSONWritable)value.next();
                BSONObject res = cc.getDoc();
                List<String> list = (List<String>)res.get("type");
                for(String i:list){
                    if(i.equals("动画"))//不统计动画这一类型
                        continue;
                    if(!isexit(i)) {
                        types.add(i);
                        nums.add(0);
                    }
                    int loc = loc(i);
                    if(loc!=-1)
                        nums.set(loc,(int)nums.get(loc)+1);
                }
            }
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("year",key.toString());
            data.put("type",types);
            data.put("num",nums);
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PreData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PieData");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(TypePie.class);
        job.setMapperClass(PieMapper.class);
        job.setReducerClass(PieReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BSONWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}