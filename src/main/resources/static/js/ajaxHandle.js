/**
 * ajax 方法
 */
function ajax(url, params, method, callBack) {
    $.ajax({
        url : url,
        type : method,
        data: params,
        headers: {
            "Authorization": "Basic " + btoa("admin" + ":" + "698d51a19d8a121ce581499d7b701668")
        },
        dataType : "json",
        success : function(json) {
            let code = json.code
            if(code===0 || code=== 200 ){
                callBack(json);
            }else {
                console.log(json.msg);
                return false;
            }
        },
        error : function(json) {
            console.log("访问出错！");
            return false;
        }
    });
}

function adminReq(admin,url, params, method, callBack){
    admin.req({
        url: url //实际使用请改成服务端真实接口
        , data: params//这里把数据封装成json的了，在springmvc里面会自己解析。
        , datatype:'json'
        , method:method
        , done: function (json) {
            callBack(json);
        }
    });
}
/**
 * post 方法
 */
function ajaxPost(url, params, callBack) {
    ajax(url,params,"post",callBack);
}

/**
 * get 方法
 */
function ajaxGet(url, params, callBack) {
    ajax(url,params,"get",callBack);
}



/**
 * post 方法
 */
function ajaxPostAdmin(admin,url, params, callBack) {
    adminReq(admin,url,params,"post",callBack);
}

/**
 * get 方法
 */
function ajaxGetAdmin(admin,url, params, callBack) {
    adminReq(admin,url,params,"get",callBack);
}

/**
 * 获取指定的URL参数值
 * URL:http://www.quwan.com/index?name=tyler
 * 参数：paramName URL参数
 * 调用方法:getParam("name")
 * 返回值:tyler
 */
function getParam(paramName) {
    paramValue = "", isFound = !1;
    if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
        arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
        while (i < arrSource.length && !isFound) arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
    }
    return paramValue == "" && (paramValue = null), paramValue
}

function HashMap() {
    this.map = {};
}
HashMap.prototype = {
    put: function (key, value) {// 向Map中增加元素（key, value)
        this.map[key] = value;
    },
    get: function (key) { //获取指定Key的元素值Value，失败返回Null
        if (this.map.hasOwnProperty(key)) {
            return this.map[key];
        }
        return null;
    },
    remove: function (key) { // 删除指定Key的元素，成功返回True，失败返回False
        if (this.map.hasOwnProperty(key)) {
            return delete this.map[key];
        }
        return false;
    },
    removeAll: function () {  //清空HashMap所有元素
        this.map = {};
    },
    keySet: function () { //获取Map中所有KEY的数组（Array）
        var _keys = [];
        for (var i in this.map) {
            _keys.push(i);
        }
        return _keys;
    }
};



HashMap.prototype.constructor = HashMap;