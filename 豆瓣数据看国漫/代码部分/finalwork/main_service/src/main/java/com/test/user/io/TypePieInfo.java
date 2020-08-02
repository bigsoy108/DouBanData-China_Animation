package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Document(collection = "PieData")//按时间段划分，各类型占比情况
public class TypePieInfo implements Serializable{
    @Field("year")
    private String year;
    @Field("type")
    private List<String> type;
    @Field("num")
    private List num;

    public TypePieInfo() {
    }

    public TypePieInfo(String year, List<String> type, List num) {
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
