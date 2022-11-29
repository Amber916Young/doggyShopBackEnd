

// document.write("<script src='https://code.jquery.com/jquery-2.2.3.min.js' type='text/javascript'></script>");
function getBasic(type){
	//获取完整路径 为: http://localhost:6981/InspectionData/TEST.html

var fullPath=window.document.location.href;
//alert(fullPath);
//获取主机地址之后的路径 为: /InspectionData/TEST.html
var pathName=window.document.location.pathname;

//获取主机地址之后的路径长度 为: /InspectionData/TEST.html的长度为25
var pos=fullPath.indexOf(pathName);

//获取主机地址 为: http://localhost:6981
var localhostPath=fullPath.substring(0,pos);

//获取项目名 为: /TEST.html
var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
   if (type==1){//全路径
   	return fullPath;	
   }else if (type==2){//jsp名称
	   	return fullPath.split('/')[fullPath.split('/').length-1].split('?')[0];
   }else if (type==3){//服务器ip
	   return  localhostPath;
   }else if (type==4){ //项目路径和名称
	   return  fullPath.replace(localhostPath,"").substring(1);
   }else if (type==0){//微信中的token
   //return scriptSrc.replace(jsName,'');
   return fullPath.split('/')[fullPath.split('/').length-3];
   }

}

function getRootPath() 
{ 
	 var pathName = window.location.pathname.substring(1);
	 var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
	 if (webName=="WeiXin")
		 return window.location.protocol + '//' + window.location.host + '/'+ webName + '/'; 
	 else
		 return window.location.protocol + '//' + window.location.host + '/'; 
}


/**
 * 时间对象的格式化;
 */
function DateTime(d){  
	var s, t;  


	if(d.getYear()<1900 || (d.getYear()+"").length<4){
		s = (d.getYear()+1900)+"-";
	} else {
		s = d.getYear()+"-";
	}
	t =(d.getMonth() + 1)+""; 
	if (t.length<2){
		s +="0"+t+ "-" ;
	}else{
		s += t + "-";
	}  
	t =(d.getDate())+""; 
	if (t.length<2){
		s +="0"+t ;
	}else{
		s += t;
	}  

	t =(d.getHours())+""; 
	if (t.length<2){
		s +=" 0"+t+":" ;
	}else{
		s +=" "+ t+":";
	}  
	t =(d.getMinutes())+""; 
	if (t.length<2){
		s +="0"+t+":" ;
	}else{
		s +=t+":";
	}  
	t =(d.getSeconds())+""; 
	if (t.length<2){
		s +="0"+t ;
	}else{
		s +=t;
	}  
	return(s);  
}                         

function DateDemo(d){  
	var s, t;  


	if(d.getYear()<1900 || (d.getYear()+"").length<4){
		s = (d.getYear()+1900)+"-";
	} else {
		s = d.getYear()+"-";
	}
	t =(d.getMonth() + 1)+""; 
	if (t.length<2){
		s +="0"+t+ "-" ;
	}else{
		s += t + "-";
	}  
	t =(d.getDate())+""; 
	if (t.length<2){
		s +="0"+t ;
	}else{
		s += t;
	}  
	return(s);  
}                         

function addDate(dd,dadd){
	var a = new Date(dd)
	a = a.valueOf()
	a = a + dadd * 24 * 60 * 60 * 1000
	a = new Date(a)
	return a;
}

function addTime(dd,dadd){//增加秒
	var a = new Date(dd)
	a = a.valueOf()
	a = a + dadd * 1000
	a = new Date(a)
	return a;
}

function differDate(dd,ddd){//相差天数
	var a = new Date(dd)
	var b = new Date(ddd)
	a = Math.round(a.valueOf()/(24 * 60 * 60 * 1000))
	b = Math.round(b.valueOf()/(24 * 60 * 60 * 1000))
	var c=Math.abs(a-b);
	return c;
}

//读取cookies
function getCookie(name)
{
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

	if(arr=document.cookie.match(reg))

		return (unescape(arr[2]));
	else
		return "";
}
//删除cookies
function delCookie(name)
{
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval=getCookie(name);
	if(cval!=null){
		document.cookie= name + "="+cval+";expires="+exp.toGMTString();
	}
	window.location.reload();
}
//写cookies
function setCookie(name,value,Days)
{
	//alert(name+":"+value);
	var exp = new Date();
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

//按秒写cookies
function setCookieSecond(name,value,second)
{
	//alert(name+":"+value);
	var exp = new Date();
	exp.setTime(exp.getTime() + second*1000);
	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

function open_authority(type,tinyType,authority){//获取权限
	var authority= eval(authority);
	var bb=false
	if (type==17){
		bb=false;
	}else{
		if (authority!=null){
			for (var i=0;i<authority.length;i++){
				if (authority[i].type==type && (authority[i].tinyType==tinyType || tinyType==0)){
					bb=true;
					break;
				}
			}
		}
	}
	return bb;
}


function findDimensions() //函数：获取尺寸   
{   //获取窗口宽度   
	var result=new Array(2);
	if (window.innerWidth)   
		result[0] = window.innerWidth;   
	else if ((document.body) && (document.body.clientWidth))   
		result[0] = document.body.clientWidth;   //获取窗口高度   
		if (window.innerHeight)   
			result[1] = window.innerHeight;   
		else if ((document.body) && (document.body.clientHeight))   
			result[1] = document.body.clientHeight;   //通过深入Document内部对body进行检测，获取窗口大小   
		if (document.documentElement  && document.documentElement.clientHeight && document.documentElement.clientWidth)   
		{   
			result[1] = document.documentElement.clientHeight;   
			result[0] = document.documentElement.clientWidth;   
		}   //结果输出至两个文本框   
		return result;

}  

function backstage(name,parameter){ //&source=123456&sec=60
	loadExtentFile("/jQueryLigerUI-V1.2.3/lib/jquery/jquery-1.3.2.min.js", "js");
	// document.write("<script language=javascript src='jquery-1.5.1.min.js'></script>"); 
	var result="";
	var url= "<%=basePath%>PublicServlet?orderType="+name+"&className=FileUtil"+parameter;	
	//alert(url);
	$.ajax({
		url:url,  
		cache: false,
		async: false,
		dataType: 'text',
		contentType: "application/json; charset=utf-8",
		success:function(data,responseText) {  
			result=data;

		},  

		error : function(msg) {  
			$.ligerDialog.error(msg+"异常！");  
		}  
	});
	return result;

}

function loadExtentFile(filePath, fileType){
	if(fileType == "js"){
		var oJs = document.createElement('script');        
		//document.body.appendChild(new_element); 
		oJs.setAttribute("type","text/javascript");
		oJs.setAttribute("src", filename);//文件的地址 ,可为绝对及相对路径
		document.getElementsByTagName_r("head")[0].appendChild(oJs);//绑定
	}else if(fileType == "css"){
		var oCss = document.create_rElement("link"); 
		oCss.setAttribute("rel", "stylesheet"); 
		oCss.setAttribute("type", "text/css");  
		oCss.setAttribute("href", filename);
		document.getElementsByTagName_r("head")[0].appendChild(oCss);//绑定
	}
}

function adjustDisplay(filePath, Display){
	var result=new Array();
	var fieldNameArray=filePath.split(",");
	for (var k=0; k<fieldNameArray.length;k++){
		for (var i=0; i<Display.length;i++){
			if (Display[i].column_name.trim()==fieldNameArray[k].trim())
				result.push(Display[i])

		}
	}
	return result;
}

//判断是否全是汉字
function isAllChinese(str){ 

	var badChar ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	badChar += "abcdefghijklmnopqrstuvwxyz"; 
	badChar += "0123456789"; 
	badChar += " "+"　";//半角与全角空格 
	badChar += "`~!@#$%^&()-_=+]\\\\|:;\"\\\'<,>?/";//不包含*或.的英文符号 

	if(""==str){ 
		return false; 
	} 
	for(var i=0;i<str.length;i++){ 
		var c = str.charAt(i);//字符串str中的字符 
		if(badChar.indexOf(c) > -1){ 
			return false; 
		} 
	} 
	return true; 
} 
//判断是否是包含汉字
function isChinese(str){ 

	var badChar ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	badChar += "abcdefghijklmnopqrstuvwxyz"; 
	badChar += "0123456789"; 
	badChar += " "+"　";//半角与全角空格 
	badChar += "`~!@#$%^&()-_=+]\\\\|:;\"\\\'<,>?/";//不包含*或.的英文符号 

	if(""==str){ 
		return false; 
	} 
	for(var i=0;i<str.length;i++){ 
		var c = str.charAt(i);//字符串str中的字符 
		if(badChar.indexOf(c) <= -1){ 
			return true; 
		} 
	} 
	return false; 
} 


function removeArrayItem(arr, item) {
	 var result=new Array();
	    for(var i=0; i<arr.length; i++){
	    if(arr[i].trim()!=item.trim()){
	        result.push(arr[i]);
	    }
	}
	 return result;
}


function Public(name,parameter,type,serverFlag){ 
	var result;
	    
	var url= "/PublicServlet?orderType="+name+"&"+parameter+"&serverFlag="+serverFlag;	
	//console.log(url);
	$.ajax({
		url:url,  
		cache: false,
		async: false,
		dataType: type,
		//data : parameter,
		contentType: "application/json; charset=utf-8",
		success:function(data,responseText) { 

			result=data.trim();
			//console.log("1"+result);

		},  

		error : function (XMLHttpRequest, textStatus, errorThrown) {
			result="error: "+textStatus+"   "+errorThrown;
			// $.ligerDialog.error(JSON.stringify(msg)+"异常！");
		}  
	});
	return result;

}
function verifyPhone(phone)
{
	if (phone=="") return true;
//var phone = document.getElementById('phone').value;
if(!(/^1[345789]\d{9}$/.test(phone))){ 
    alert("手机号码有误，请重填");  
    return false; 
}else{
	return true;
} 
}
function PublicNew(name,parameter,type,post,serverFlag){ 
	var result;
	if (post==1){
	var url= "/PublicServlet?orderType="+name+"&serverFlag="+serverFlag;	
	//alert(url);
	$.ajax({
			 url:url,  
			     cache: false,
			     async: false,
		         dataType: type,
		         data : parameter,
		         contentType: "application/json; charset=utf-8",
				 success:function(data,responseText) {  
					 result=data;
					 
		    		 
		 		},  
		 		
			 error : function (XMLHttpRequest, textStatus, errorThrown) {
				 result="error: "+textStatus+"   "+errorThrown;
			 	 // $.ligerDialog.error(JSON.stringify(msg)+"异常！");  
				 }  
		  });
	}else{
		var url= "/PublicServlet?orderType="+name+"&parameter="+parameter+"&serverFlag="+serverFlag;	
		//alert(url);
		$.ajax({
				 url:url,  
				     cache: false,
				     async: false,
			         dataType: type,
			         data : parameter,
			         contentType: "application/json; charset=utf-8",
					 success:function(data,responseText) {  
						 result=data;
						 
			    		 
			 		},  
			 		
				 error : function (XMLHttpRequest, textStatus, errorThrown) {
					 result="error: "+textStatus+"   "+errorThrown;
				 	 // $.ligerDialog.error(JSON.stringify(msg)+"异常！");  
					 }  
			  });
		
	}
	//alert(result);
		    return result;

}

function modifySql(sql,serverFlag,prompt){
	var result=0;
	var url= "/PublicServlet?orderType=modifySql&sql="+encodeURIComponent(sql)+"&serverFlag="+serverFlag;
	//alert(url);
	    $.ajax({
		 url:url,  
		     cache: false,
		     async: false,
	         dataType: 'json',
	         contentType: "application/json; charset=utf-8",
			 success:function(data,responseText) {  
				 //alert(data[0].sign);
	    		 if(data[0].sign =="1" ){ 
	    			 
	    			 alert(prompt);
	    			 result=1;
		     	 }else{
		     	    	
		     		alert(data[0].result);
		     		result=0;
	     	    }
	    		 
	 		},  
	 		
		 error : function(msg) {  
			 //alert(msg+"异常！"); 
			// console.log(msg+"异常！")
			 result= 2;
			 }  
	  });
	   // alert(result)
	    return  result;
}
 
//<!--将select的值赋给input框-->
function qlcTrainS(idName) {
	//alert(idName
   // var arrValue=document.getElementById(idName).options[document.getElementById(idName).selectedIndex].value;
	//alert(idName.substring(0,idName.indexOf("_"))) ;  
	$("#"+idName.substring(0,idName.indexOf("_"))).val( $("#"+idName).val())
}




