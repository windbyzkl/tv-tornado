package com.tv.util;
/**
 * @Description: 自定义响应数据结构
 * 				这个类是提供给门户，ios，安卓，微信商城用的
 * 				门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）
 * 				其他自行处理
 * 				200：表示成功
 * 				500：表示错误，错误信息在msg字段中
 * 				501：bean验证错误，不管多少个错误都以map形式返回
 * 				502：拦截器拦截到用户token出错
 * 				555：异常抛出信息
 */
public class JsonResult {
    private Object data;
    private Integer status;
    private String msg;
    public JsonResult(Object data){
        this.status = 200;
        this.msg = "ok";
        this.data = data;
    }
    public JsonResult(Integer status,String msg,Object data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public boolean isOk(){return this.status==200;}

    public static JsonResult ok(Object obj){return new JsonResult(obj);}

    public static JsonResult ok(){return new JsonResult(null);}

    public static JsonResult errorMsg(String msg){return new JsonResult(500,msg,null);}

    public static JsonResult errorMap(Object data){return new JsonResult(501,"error",data);}

    public static JsonResult errorTokenMsg(String msg){return new JsonResult(502,msg,null);}

    public static JsonResult errorException(String msg){return new JsonResult(503,msg,null);}

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
