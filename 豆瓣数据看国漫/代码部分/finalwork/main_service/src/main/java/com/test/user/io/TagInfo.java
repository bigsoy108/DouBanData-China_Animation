package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Document(collection = "TagPieData")//按年份划分，标签数量
public class TagInfo implements Serializable{
    @Field("year")
    private String year;
    @Field("tags")
    private List<String> type;
    @Field("num")
    private List num;

    public TagInfo() {
    }

    public TagInfo(String year, List<String> type, List num) {
        this.year = year;
        this.type = type;
        this.num = num;
    }

    public String getYear() {
        return year;
    }

    public List<String> getType() {
        return type;
    }

    public List getNum() {
        return num;
    }
}