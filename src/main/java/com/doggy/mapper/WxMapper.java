package com.doggy.mapper;

import com.doggy.entity.Wx_Info;

import java.util.HashMap;

public interface WxMapper {

    Wx_Info queryWxInfo(HashMap<String, Object> map);
}
