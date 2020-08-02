package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
@Document(collection = "YearData")//按年份划分获得的总数就
public class YearInfo implements Serializable {
    @Field("year")
    private String year;
    @Field("all_num")
    private int allNum;
    @Field("t1_num")
    private int t1Num;
    @Field("t0_num")
    private int t0Num;
    @Field("rank1")
    private int rank1;
    @Field("rank2")
    private int rank2;
    @Field("rank3")
    private int rank3;
    @Field("rank4")
    private int rank4;
    @Field("com_num")
    private int com_num;
    @Field("view_num")
    private int view_num;
    @Field("length1")
    private float len1;
    @Field("length0")
    private float len0;
    @Field("all_len")
    private float length;

    public YearInfo() {
    }

    public YearInfo(String year, int allNum, int t1Num, int t0Num, int rank1, int rank2, int rank3, int rank4, int com_num, int view_num, float len1, float len0, float length) {
        this.year = year;
        this.allNum = allNum;
        this.t1Num = t1Num;
        this.t0Num = t0Num;
        this.rank1 = rank1;
        this.rank2 = rank2;
        this.rank3 = rank3;
        this.rank4 = rank4;
        this.com_num = com_num;
        this.view_num = view_num;
        this.len1 = len1;
        this.len0 = len0;
        this.length = length;
    }

    public String getYear() {
        return year;
    }

    public int getAllNum() {
        return allNum;
    }

    public int getT1Num() {
        return t1Num;
    }

    public int getT0Num() {
        return t0Num;
    }

    public int getRank1() {
        return rank1;
    }

    public int getRank2() {
        return rank2;
    }

    public int getRank3() {
        return rank3;
    }

    public int getRank4() {
        return rank4;
    }

    public int getCom_num() {
        return com_num;
    }

    public int getView_num() {
        return view_num;
    }

    public float getLen1() {
        return len1;
    }

    public float getLen0() {
        return len0;
    }

    public float getLength() {
        return length;
    }
}
