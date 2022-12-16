package com.doggy.service;

import com.doggy.entity.Wx_Info;
import com.doggy.mapper.CustomerMapper;
import com.doggy.mapper.WxMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @ClassName:WxService
 * @Auther: yyj
 * @Description:
 * @Date: 16/12/2022 14:54
 * @Version: v1.0
 */
@Service
public class WxService {
    @Autowired
    WxMapper wxMapper;

    public Wx_Info queryWxInfo(HashMap<String, Object> map) {
        return wxMapper.queryWxInfo(map);
    }
}
