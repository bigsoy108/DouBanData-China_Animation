package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;

@Getter
@Setter
@Document(collection = "TenData")//按时间段划分获得的总数据
public class DuanInfo implements Serializable {
    @Field("years")
    private String year;
    @Field("length1")
    private float len1;
    @Field("length0")
    private float len0;
    @Field("rate")
    private float rate;
    @Field("co_num")
    private float num;

    public DuanInfo() {
    }

    public DuanInfo(String year, float len1, float len0, float rate, float num) {
        this.year = year;
        this.len1 = len1;
        this.len0 = len0;
        this.rate = rate;
        this.num = num;
    }

    public String getYear() {
        return year;
    }

    public float getLen1() {
        return len1;
    }

    public float getLen0() {
        return len0;
    }

    public float getRate() {
        return rate;
    }

    public float getNum() {
        return num;
    }
}
