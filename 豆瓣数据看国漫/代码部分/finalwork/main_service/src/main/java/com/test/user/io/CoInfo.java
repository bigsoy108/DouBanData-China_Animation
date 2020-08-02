package com.test.user.io;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;

@Getter
@Setter
@Document(collection = "CoData")//各国合拍数量
public class CoInfo implements Serializable{
    @Field("place")
    private String place;
    @Field("num")
    private int num;

    public CoInfo() {
    }

    public CoInfo(String place, int num) {
        this.place = place;
        this.num = num;
    }

    public String getPlace() {
        return place;
    }

    public int getNum() {
        return num;
    }
}
