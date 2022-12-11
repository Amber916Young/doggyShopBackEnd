# 文档说明

## BUG 日记 08/12/2022

### 🧰BUG 修复

- 前端返回减少返回非必要信息

### 🔗链接 参考

https://ext.dcloud.net.cn/plugin?id=8827【价格信息组件】

## 【知识点】BigDecimal高精度计算

### **构造器描述** 

- BigDecimal(int)       创建一个具有参数所指定整数值的对象。 
- BigDecimal(double) 创建一个具有参数所指定双精度值的对象。 //不推荐使用
- BigDecimal(long)    创建一个具有参数所指定长整数值的对象。 
- BigDecimal(String) 创建一个具有参数所指定以字符串表示的数值的对象。//**推荐使用**

### **方法描述** 

- add(BigDecimal)        BigDecimal对象中的值相加，然后返回这个对象。 
- subtract(BigDecimal) BigDecimal对象中的值相减，然后返回这个对象。 
- multiply(BigDecimal)  BigDecimal对象中的值相乘，然后返回这个对象。 
- divide(BigDecimal)     BigDecimal对象中的值相除，然后返回这个对象。 
- toString()                将BigDecimal对象的数值转换成字符串。 
- doubleValue()          将BigDecimal对象中的值以双精度数返回。 
- floatValue()             将BigDecimal对象中的值以单精度数返回。 
- longValue()             将BigDecimal对象中的值以长整数返回。 
- intValue()               将BigDecimal对象中的值以整数返回。

[![zgR0K0.png](https://s1.ax1x.com/2022/12/07/zgR0K0.png)](https://imgse.com/i/zgR0K0)



### 【code】Java 高精度计算总金额

```java
    synchronized public HashMap<String,Object> ReturnCartNumAndPrice( List<OrderCart> cartList, Rule rule) {
        int rule_type = rule.getType(); //优惠卷类型, 0-满减, 1-折扣 2 直减
        int use_range = rule.getUse_range(); //使用范围，0—全场，1—商品
        Set<Integer> set = new HashSet<>();
        Set<Integer> setAll = new HashSet<>();
        Set<Integer> goodsIdSet = new HashSet<>();
        Map<Integer, Goods> goodsMap = new HashMap<>();
        if (use_range == 1) {
            String goods_list = rule.getGoods_list().replaceAll("\\[", "").replaceAll("\\]", "");
            String[] goods_ids = goods_list.split(",");
            for (String id : goods_ids) {
                id = id.trim();
                if (id != null || id.equals("") || id.length() > 0) {
                    set.add(Integer.parseInt(id));
                }
            }
        }
        BigDecimal match_amount_price = new BigDecimal("0");
        BigDecimal match_amount_price_all = new BigDecimal("0");

        Map<Integer, BigDecimal> countMap = new HashMap<>();

        for (OrderCart orderCart : cartList) {
            int id = orderCart.getGood_id();
            Goods goods = goodsService.queryAllGoodsById(id);
            setAll.add(id);
            goodsMap.put(id, goods);
            BigDecimal tmp = new BigDecimal(Double.toString(goods.getOriginal_price()));
            BigDecimal count = new BigDecimal(orderCart.getGood_amount());
            tmp = tmp.multiply(count);
            countMap.put(id, count);
            // 符合条件的
            if (!set.isEmpty()) {
                if (set.contains(id)) {
                    goodsIdSet.add(id);
                    match_amount_price = match_amount_price.add(tmp);
                }
            }
            match_amount_price_all = match_amount_price_all.add(tmp);
            orderCart.setGoods(goods);

        }

        BigDecimal amount = new BigDecimal(Double.toString(rule.getAmount()));
        BigDecimal discount = new BigDecimal(Double.toString(rule.getDiscount() / 10.0));
        BigDecimal threshold = new BigDecimal(Double.toString(rule.getThreshold()));
        boolean flag = false;
        if (rule_type == 1) { //优惠卷类型, 0-满减, 1-折扣 2 直减
            amount = match_amount_price.multiply(discount);
        }
        if (use_range == 0) {
            for (int gid : goodsIdSet) {
                Goods goods = goodsMap.get(gid);
                BigDecimal count = countMap.get(gid);
                BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
                BigDecimal total_original_price = org.multiply(count);
                BigDecimal ratio = total_original_price.divide(match_amount_price, 8, BigDecimal.ROUND_HALF_UP);
                BigDecimal disMoney = ratio.multiply(amount);
                BigDecimal div = total_original_price.subtract(disMoney);
                BigDecimal newPrice = div.divide(count, 2, BigDecimal.ROUND_HALF_UP);
                //优惠卷类型, 0-满减, 1-折扣 2 直减
                if (rule_type == 0) {
                    if (match_amount_price.compareTo(threshold) < 0) {
                        flag =true;
                        break;
                    } else {
                        goods.setPrice(newPrice.doubleValue());
                    }
                } else if (rule_type == 1) {
                    goods.setPrice(newPrice.doubleValue());
                } else if (rule_type == 2) {
                    goods.setPrice(newPrice.doubleValue());
                }
            }
        } else {
            for (int gid : setAll) {
                Goods goods = goodsMap.get(gid);
                BigDecimal count = countMap.get(gid);
                BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
                BigDecimal total_original_price = org.multiply(count);

                BigDecimal ratio = total_original_price
                        .divide(match_amount_price_all, 8, BigDecimal.ROUND_HALF_UP);
                BigDecimal disMoney = ratio.multiply(amount);
                BigDecimal div = total_original_price.subtract(disMoney);
                BigDecimal newPrice = div.divide(count, 2, BigDecimal.ROUND_HALF_UP);

                //优惠卷类型, 0-满减, 1-折扣 2 直减
                if (rule_type == 0) {
                    if (match_amount_price_all.compareTo(threshold) < 0) {
                        flag =true;
                        break;
                    } else {
                        goods.setPrice(newPrice.doubleValue());
                    }
                } else if (rule_type == 1) {
                    goods.setPrice(newPrice.doubleValue());

                    goods.setPrice(disMoney.divide(count, 2, RoundingMode.HALF_UP).doubleValue());

                } else if (rule_type == 2) {
                    goods.setPrice(newPrice.doubleValue());

                }
            }
        }


        BigDecimal Discount = new BigDecimal("0");
        BigDecimal amount_price = new BigDecimal("0");
        BigDecimal amount_price_discount = new BigDecimal("0");

        List<HashMap<String, Object>> res = new ArrayList<>();
        for (OrderCart orderCart : cartList) {
            Goods goods = orderCart.getGoods();
            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("good_id", orderCart.getGood_id());
            objectHashMap.put("good_amount", orderCart.getGood_amount());
            objectHashMap.put("price", goods.getPrice());
            objectHashMap.put("original_price", goods.getOriginal_price());
            BigDecimal count = new BigDecimal(orderCart.getGood_amount());
            BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
            BigDecimal price = new BigDecimal(Double.toString(goods.getPrice()));
            BigDecimal total_original_price = org.multiply(count).setScale(2, BigDecimal.ROUND_HALF_UP);
            objectHashMap.put("total_original_price", total_original_price);
            BigDecimal total_price = price.multiply(count).setScale(2, BigDecimal.ROUND_HALF_UP);
            objectHashMap.put("total_price", total_price);
            amount_price = amount_price.add(total_original_price);
            amount_price_discount = amount_price_discount.add(total_price);
            Discount = Discount.add(total_original_price.subtract(total_price));
            res.add(objectHashMap);
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("priceList", res);
        if(flag){
            param.put("flag", "未满足优惠最低金额");
        }
        param.put("amount_price", amount_price.setScale(2, RoundingMode.HALF_UP));
        param.put("amount_price_discount", amount_price_discount.setScale(2, RoundingMode.HALF_UP));
        param.put("discount", Discount.setScale(2, RoundingMode.HALF_UP));
        return param;
    }

```









## BUG 日记 06/12/2022

### 🧰BUG 修复

- 获取总金额，需要保留小数点两位
- 获取产品详情页面有bug，找不到图片
- 产品侧边栏没有图片也会存在一个占位view
- 清空购物车后，stepper应该归零，最终需要加上v-model来双向选择
- 如果没有地址记录，支付页面会报错，需要自动跳转到新增地址页面
- 【**复杂**】优惠券选择页面是基于当前购买商品，两个基本商品和全场优惠券，比较复杂的判断，首先要获取购物车的商品信息，再获取用户的优惠券信息，如果匹配则返回可以使用的优惠券
- 提醒用户是否有可用优惠券
- 后台优惠券修改，优惠规则修改界面有bug



### 🍀样式优化

- button修改为右下角固定样式

### 🔗链接 参考

https://blog.csdn.net/playis/article/details/126374487 优惠券样式

[轻量级工具emoji-java处理emoji表情字符](https://blog.csdn.net/qq_44799924/article/details/117114788  )



## 【知识点】uniapp 返回上一页携带参数

```javascript
// 返回的页面添加以下方法 （onBackPress 是返回的生命周期）

onBackPress(options) {
	if (options.from === 'navigateBack') {
		return false;
	}
	let pages = getCurrentPages(); //获取所有页面栈实例列表
	let nowPage = pages[pages.length - 1]; //当前页页面实例
	let prevPage = pages[pages.length - 2]; //上一页页面实例
	prevPage.$vm.searchVal = 1211; //修改上一页data里面 searchVal 的值
	
	//uni.navigateTo跳转的返回，默认1为返回上一级
	uni.navigateBack({
		delta: 1
	});
	return true; // 此处必须 return 
}

```

**上一页获取**

```js
data() {
	return {
		searchVal :'' // 返回后等于 1211
	}
}
```



## BUG 日记 03/12/2022

### 优惠券系统设计

[![zD31SO.png](https://s1.ax1x.com/2022/12/03/zD31SO.png)](https://imgse.com/i/zD31SO)

[![zD33lD.png](https://s1.ax1x.com/2022/12/03/zD33lD.png)](https://imgse.com/i/zD33lD)





## BUG 日记 02/12/2022

### 新增功能 

- 后台评论管理

- 接口统一管理

[<img src="https://s1.ax1x.com/2022/12/02/zDVmX6.png" alt="zDVmX6.png" style="zoom:50%;" />](https://imgse.com/i/zDVmX6)

### 🧰BUG 修复

- 默认地址只允许存在一个
- 限制最大添加地址数量5个
- 删除商品的时候，客户的订单也会被删除，因为商品和订单里的字段关联，所以实际上删除指的是下架商品！



## BUG 日记 01/12/2022

### 新增功能 

- 用户完成交易后评论
- 产品页面显示评论

逻辑非常复杂。。。。我已被绕晕

### BUG 修复

- 新增order detail的is_comment字段, 方便查询已评价和未评价订单

### 下一阶段

- 后台评论管理搭建搭建
- 物流信息接入京东或者中通（暂时不确定个人开发者是否可以接入



## BUG 日记 27/11/2022

### 新增功能 

- 新增用户信息修改功能，主要是为了获取手机号，因为微信版本更新不再开放`getUserinfo`接口

- 清除缓存功能

- 更新支付页面布局

  

### 🧰BUG 修复

- 订单积分未加入到用户表的points字段里
- 订单Master表修改，因为地址会在主表删除，所以用addressid很不可靠
- 订单提交后要update用户表，更新points字段
- 冒泡事件修复
- 地址VUE页面更新



### 下一阶段

微信支付用不了，所以只能使用支付宝接口

支付宝也要验证，现在准备接入第三方个人开发者支付接口

https://h5zhifu.com/

```apl
   table.render({
      elem: '#tableview'
      ,url: '/product/queryAll' //模拟接口
      ,toolbar: '#toolbarDemo' //开启头部工具栏，并为其绑定左侧模板
      ,defaultToolbar: ['filter', 'exports', 'print']
      ,title: 'MANETs List'
      ,cols: [
        [
          {type: 'checkbox', fixed: 'left'}
          ,{field: 'id', width: 150, title: 'id', sort: true}
          ,{field: 'title', title: 'title', minWidth: 100}
          ,{field: 'createTime', title: 'createTime', minWidth: 100, templet: function (d) {
            if(d.createTime==null) return "" ;
            var createTime = layui.util.toDateString(d.createTime, "yyyy-MM-dd HH:mm:ss");
            return "<label id='" + d.LAY_INDEX + "'  >" + createTime + "</label>";
          }}
     
        ]
      ]
      ,page: true
      ,limit: 30
      ,limits: [10, 20, 30, 50, 100,150,200]
      ,text: 'error'
    });
```
