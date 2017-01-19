package cn.com.zjs.cloud.coolcode.model;

import cn.com.zjs.cloud.coolcode.NameConvertUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
public class MyBatisXmlModel {

    private JavaModel entityModel;

    private String tableName;

    private Column idColumn;

    private List<Column> commonColumns;

    public MyBatisXmlModel(JavaModel entityModel){
        if(entityModel.getIdField() == null) throw new RuntimeException("Id field was not found, please create dao xml manually.");
        this.entityModel = entityModel;
        this.tableName = NameConvertUtil.camelToUnderline(entityModel.getSimpleClassName());
        idColumn = Column.fieldConvert(entityModel.getIdField());

     }


}

