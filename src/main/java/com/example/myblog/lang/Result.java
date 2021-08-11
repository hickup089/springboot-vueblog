package com.example.myblog.lang;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    private int code;
    private String msg;
    private Object data;

    public static Result succ(Object data){
        Result res=new Result();
        res.setCode(0);
        res.setMsg("succ");
        res.setData(data);
        return res;
    }

    public static Result succ(String mess, Object data) {
        Result m = new Result();
        m.setCode(0);
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess) {
        Result m = new Result();
        m.setCode(-1);
        m.setData(null);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode(-1);
        m.setData(data);
        m.setMsg(mess);
        return m;
    }

    public static Result fail(int code,String mess, Object data) {
        Result m = new Result();
        m.setCode(code);
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
}
