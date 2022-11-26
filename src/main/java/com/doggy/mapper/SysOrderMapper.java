package com.doggy.mapper;


import com.doggy.entity.OrderCart;
import com.doggy.entity.OrderDetail;
import com.doggy.entity.OrderMaster;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Delete;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysGoodsMapper
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:37
 * @Version: v1.0
 */
public interface SysOrderMapper {

    OrderCart queryOrderCart(HashMap<String, Object> param);

    void insertOrderCart(HashMap<String, Object> param);

    void updateOrderCart(HashMap<String, Object> param);

    List<OrderCart> queryOrderCartList(HashMap<String, Object> param);

    @Delete("delete from Order_Cart where (customer_id = #{customer_id}  and good_id = #{good_id} ) " +
            "or cart_id = #{cart_id}")
    void deleteOrderCart(HashMap<String, Object> param);

    int pageQueryOrderCount(Page page);

    List<OrderMaster> pageQueryOrderData(Page page);

    OrderMaster queryOrderMaster(HashMap<String, Object> param);

    List<OrderDetail> queryOrderDetailAll(int order_id);

    int insertOrderMaster(OrderMaster master);

    void insertOrderDetail(OrderDetail detail);
}
