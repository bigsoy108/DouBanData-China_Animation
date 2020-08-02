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


//按年份划分数据
public class SortByYear {
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

    public static class SortByYearMapper
            extends Mapper<Object, BSONObject, Text, BSONWritable> {


        //根据动画的评分为其设定等级
        public String CheckScore(float score){
            if(score<=4)
                return("4分及以下");
            else if(score<6)
                return ("4-6分");
            else if(score<8)
                return ("6-8分");
            else return ("8分及以上");
        }

        @Override
        protected void map(Object key, BSONObject value, Context context) throws IOException, InterruptedException {
            String year = value.get("year").toString();

            //排除超过2020年的数据
            if(Integer.parseInt(year)>2020)
                return;
            String flag = value.get("flag").toString();
            float score = Float.parseFloat(value.get("score").toString());
            String rank = CheckScore(score);
            List<String> place = (ArrayList<String>)value.get("place");
            String coplace = "";

            //判断是否是合拍动画
            if(place.size()>1){
                for(int i=0;i<place.size();i++)
                    if(!place.get(i).equals("中国大陆"))
                        coplace = new String("y");
            }else{
                coplace = new String("n");
            }

            String view_num = value.get("view_num").toString();
            String com_num = value.get("com_num").toString();

            //计算总时长
            float length = Float.parseFloat(value.get("len1").toString())*Float.parseFloat(value.get("len2").toString());

            BasicBSONObject res = new BasicBSONObject() ;
            res.put("year",year);
            res.put("rank",rank);
            res.put("com_num",com_num) ;
            res.put("view_num",view_num) ;
            res.put("coplace",coplace) ;
            res.put("length",length) ;
            res.put("flag",flag) ;
            context.write(new Text(year), new BSONWritable(res));
        }
    }

    public static class SortByYearReducer
            extends Reducer<Text,BSONWritable, NullWritable,BSONWritable> {
        @Override
        protected void reduce(Text key, Iterable<BSONWritable> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            int all_num = 0;
            int t1_num = 0;
            int t0_num = 0;
            int rank1 = 0;
            int rank2 = 0;
            int rank3 = 0;
            int rank4 = 0;
            int com_num = 0;
            int view_num = 0;
            int co_num = 0;
            float length1 = 0;
            float length0 = 0;

            //对同一年份的动画数据进行统计
            while (value.hasNext()){
                BSONWritable cc = (BSONWritable)value.next();
                BSONObject res = cc.getDoc();
                all_num++;
                if(res.get("flag").toString().equals("1"))
                {
                    t1_num++;
                    length1 = length1 + Float.parseFloat(res.get("length").toString());
                }
                else                 {
                    t0_num++;
                    length0 = length0 + Float.parseFloat(res.get("length").toString());
                }
                if(res.get("coplace").toString().equals("y"))
                    co_num++;
                switch (res.get("rank").toString()){
                    case "4分及以下":
                        rank1++;
                        break;
                    case "4-6分" :
                        rank2++;
                        break;
                    case "6-8分":
                        rank3++;
                        break;
                    case "8分及以上":
                        rank4++;
                        break;
                }
                com_num = com_num+Integer.parseInt(res.get("com_num").toString());
                view_num = view_num+Integer.parseInt(res.get("view_num").toString());
            }
            BasicBSONObject data = new BasicBSONObject() ;
            data.put("year",key.toString());
            data.put("all_num",all_num);
            data.put("t1_num",t1_num);
            data.put("t0_num",t0_num);
            data.put("rank1",rank1);
            data.put("rank2",rank2);
            data.put("rank3",rank3);
            data.put("rank4",rank4);
            data.put("com_num",com_num);
            data.put("view_num",view_num);
            data.put("co_num",co_num);
            data.put("length1",length1);
            data.put("length0",length0);
            data.put("all_len",length0+length1);
            context.write(null,new BSONWritable(data));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        // 0. 初始化 MR Job
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.PreData");
        MongoConfigUtil.setOutputURI(conf,"mongodb://192.168.1.114:27017/GuomanDB.YearData");
        Job job = Job.getInstance(conf,"Mongo Connection");
        job.setJarByClass(SortByYear.class);
        job.setMapperClass(SortByYearMapper.class);
        job.setReducerClass(SortByYearReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BSONWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(BSONWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        job.waitForCompletion(true);

    }
}