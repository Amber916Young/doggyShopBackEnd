package com.doggy.utils;
import lombok.Data;

import java.io.Serializable;

@Data
public class Page implements Serializable {
    //当前页
    private Integer page=1;
    private int id;
    //页大小
    private Integer rows=5;

    private Object data;
    // 总记录 数
    private Integer totalRecord;
    //总页数
    private Integer totalPage;
    //关键字类型
    private String keyType;
    //查询关键字
    private String keyWord;
    //开始记录位置
    private Integer start;
    //用户id
    private String userid;
    //其他用户id
    private String otherid;

    public Integer getTotalPage() {
        totalPage=(totalRecord-1)/rows+1;
        return totalPage;
    }
    public Integer getStart() {
        start=(page-1)*rows;
        return start;
    }
    public Page() {
    }
    public Page(Integer page, Integer rows, Integer totalRecord, Integer totalPage, String keyType, String keyWord, Integer start, String userid, String otherid) {
        this.page = page;
        this.rows = rows;
        this.totalRecord = totalRecord;
        this.totalPage = totalPage;
        this.keyType = keyType;
        this.keyWord = keyWord;
        this.start = start;
        this.userid = userid;
        this.otherid = otherid;
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", rows=" + rows +
                ", totalRecord=" + totalRecord +
                ", totalPage=" + totalPage +
                ", keyType='" + keyType + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", start=" + start +
                ", userid='" + userid + '\'' +
                ", otherid='" + otherid + '\'' +
                '}';
    }

}
