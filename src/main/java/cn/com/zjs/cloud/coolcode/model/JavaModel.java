package cn.com.zjs.cloud.coolcode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */
public class JavaModel {
    private static final Logger logger = LoggerFactory.getLogger(JavaModel.class);

    private String pkg;

    private String parentPkg;

    private String pkgDir;

    private String parentPkgDir;

    private String className;

    private String simpleClassName;

    private String instanceName;

    private Field idField;

    private List<Field> commonFields = new ArrayList<>();

    public JavaModel(Class modelClass){
        pkg = modelClass.getPackage().getName();
        parentPkg = pkg.substring(0, pkg.lastIndexOf("."));
        String pkgPath = pkg.replaceAll("\\.", "/");
        pkgDir = pkgPath + "/";
        parentPkgDir = pkgPath.substring(0, pkgPath.lastIndexOf("/")+1);

        className = modelClass.getName();
        simpleClassName = modelClass.getSimpleName();
        instanceName = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
        logger.info(instanceName);
        Field[] fields = modelClass.getFields();
        for (Field field : fields){
            if(field.getName().equals("id")) {
                idField = field;
                continue;
            }
            commonFields.add(field);
        }
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getParentPkg() {
        return parentPkg;
    }

    public void setParentPkg(String parentPkg) {
        this.parentPkg = parentPkg;
    }

    public String getPkgDir() {
        return pkgDir;
    }

    public void setPkgDir(String pkgDir) {
        this.pkgDir = pkgDir;
    }

    public String getParentPkgDir() {
        return parentPkgDir;
    }

    public void setParentPkgDir(String parentPkgDir) {
        this.parentPkgDir = parentPkgDir;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Field getIdField() {
        return idField;
    }

    public void setIdField(Field idField) {
        this.idField = idField;
    }

    public List<Field> getCommonFields() {
        return commonFields;
    }

    public void setCommonFields(List<Field> commonFields) {
        this.commonFields = commonFields;
    }
}
