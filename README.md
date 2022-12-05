# 文档说明

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

### BUG 修复

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

  

### BUG 修复

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
