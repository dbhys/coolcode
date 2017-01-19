package cn.com.zjs.cloud.coolcode.model;

import cn.com.zjs.cloud.coolcode.NameConvertUtil;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/1/13.
 */
public class Column {

    public static Column fieldConvert(Field field){
        Column column = new Column();
        column.property = field.getName();
        column.name = NameConvertUtil.camelToUnderline(field.getName());
        column.jdbcType = "";
        return column;
    }

    private String name;

    private String property;

    private String jdbcType;

}
