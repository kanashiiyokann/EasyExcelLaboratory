package com.example.template;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author zyw
 */
@Data
public class UserTemplate {

    @ExcelProperty({"账号", "用户"})
    private String name;
    @ExcelProperty("密码")
    private String pwd;

    @Override
    public String toString() {
        return "UserTemplate{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
