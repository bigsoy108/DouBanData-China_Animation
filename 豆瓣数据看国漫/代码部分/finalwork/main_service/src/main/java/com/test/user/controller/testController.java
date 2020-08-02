package com.test.user.controller;

import com.test.user.io.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.List;

@Controller     // 声明为一个 Controller
@RequestMapping("/main")        // 声明借助浏览器访问该 Controller 的 url Path
public class testController {
//    @Resource       // 注入 ThriftClient
//    private ServiceProvider serviceProvider;

    @Resource
    private MongoTemplate mongoTemplate;

    //主页面
    @RequestMapping(value = "/guoman", method = RequestMethod.GET)
    public String display2(){
        return "guoman";
    }

    //返回按年份划分的数据
    @RequestMapping(value = "/now", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<YearInfo> Sprider(){
        return mongoTemplate.findAll(YearInfo.class);
    }

    //返回按时间段划分的数据
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<DuanInfo> Sprider1(){
        return mongoTemplate.findAll(DuanInfo.class);
    }

    //返回每年类型数量的数据
    @RequestMapping(value = "/test2", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<TypeInfo> Sprider2(){
        return mongoTemplate.findAll(TypeInfo.class);
    }
     
	//返回各时间段类型占比数据
    @RequestMapping(value = "/test3", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<TypePieInfo> Sprider3(){
        return mongoTemplate.findAll(TypePieInfo.class);
    }

    //返回各国合拍数据
    @RequestMapping(value = "/test4", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<CoInfo> Sprider4(){
        return mongoTemplate.findAll(CoInfo.class);
    }

    //返回按年份划分标签数量
    @RequestMapping(value = "/test5", method = RequestMethod.POST)
    @ResponseBody       // 返回的数据是 Json 数据
    public List<TagInfo> Sprider5(){
        return mongoTemplate.findAll(TagInfo.class);
    }
}