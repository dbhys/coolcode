package cn.com.zjs.cloud.coolcode.model;

import java.io.File;

/**
 * Created by Administrator on 2017/1/12.
 */
public class JavaTemplateModel implements TemplateModel {

    public JavaTemplateModel(String relativeParentDirPath, String templateFileName, JavaModel entityModel) {
        this.entityModel = entityModel;
        this.targetFileName = entityModel.getSimpleClassName() + templateFileName.substring(0, templateFileName.length() - 4);
        this.simpleClassName = targetFileName.substring(0, targetFileName.length() - 5);
        if (relativeParentDirPath == null || relativeParentDirPath.trim() == "") {
            this.pkg = entityModel.getPkg();
            this.targetFilePath = entityModel.getPkgDir() + this.targetFileName;
        } else {
            this.targetFilePath = entityModel.getParentPkgDir() + relativeParentDirPath.replaceAll("\\" + File.separator, "/") + "/" + targetFileName;
            this.pkg = entityModel.getParentPkg() + "." + relativeParentDirPath.replaceAll("\\/", ".").replaceAll("\\\\", ".");
        }
    }

    private String pkg;

    private String targetFileName;

    private String targetFilePath;

    private String simpleClassName;

    private JavaModel entityModel;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public JavaModel getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(JavaModel entityModel) {
        this.entityModel = entityModel;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    @Override
    public String getTargetFilePath() {
        return this.targetFilePath;
    }
}
