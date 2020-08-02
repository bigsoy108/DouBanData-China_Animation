package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
@Document(collection = "TypeData")//按年份划分，类型数量
public class TypeInfo implements Serializable {
    @Field("year")
    private String year;
    @Field("type_num")
    private int num;

    public TypeInfo() {
    }

    public TypeInfo(String year, int num) {
        this.year = year;
        this.num = num;
    }

    public String getYear() {
        return year;
    }

    public int getNum() {
        return num;
    }
}