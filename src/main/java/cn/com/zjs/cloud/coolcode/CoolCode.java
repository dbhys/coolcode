package cn.com.zjs.cloud.coolcode;

import cn.com.zjs.cloud.coolcode.model.JavaTemplateModel;
import cn.com.zjs.cloud.coolcode.model.TemplateModel;
import cn.com.zjs.cloud.coolcode.model.JavaModel;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxudong on 2017/1/11.
 */
public class CoolCode {
    private static final Logger logger = LoggerFactory.getLogger(CoolCode.class);

    private static String TEMPLATE_DIR = "hbs/";

    private static String JAVA_SOURCE_DIR = "src/main/java/";
    private static String JAVA_RESOURCES_DIR = "src/main/resources/";

    private static String MYBATIS_CONFIG_FILE_LOCATION = "src/main/resources/generators/generatorConfig.xml";

    public CoolCode(){
        super();
    }

    public CoolCode(String javaSourceDir, String javaResourcesDir){
        this.JAVA_SOURCE_DIR = javaSourceDir;
        this.JAVA_RESOURCES_DIR = javaResourcesDir;
    }

    /**
     * 生成标准增删改查文件
     * @param modelClass
     * @throws IOException
     */
    public void generateStandardCURD(Class modelClass) throws IOException {
        TEMPLATE_DIR += "standard/";

        // Find all files in template dir
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:" + TEMPLATE_DIR + "**/*.*");
        //URL resource = CoolCode.class.getResource("/" + TEMPLATE_DIR + "dao/Dao.java.hbs");
        generateByResources(resources, modelClass);

    }

    /**
     * 根据ClassPathResources批量生成文件
     * @param resources
     * @param modelClass
     */
    public void generateByResources(Resource[] resources, Class modelClass) {
        if(resources == null || resources.length == 0) {
            logger.info("No templates found!");
            return;
        }
        for(Resource resource : resources){
            if (resource instanceof ClassPathResource){
                ClassPathResource classPathTemplate = (ClassPathResource) resource;
                logger.info("Template path: {}", classPathTemplate.getPath());
                parseClassPathResourceTemplateAndGenerateFile(classPathTemplate, modelClass);
            }
        }
    }

    /**
     *
     * @param ClassPathResourceTemplate
     * @param modelClass
     */
    public void parseClassPathResourceTemplateAndGenerateFile(ClassPathResource ClassPathResourceTemplate, Class modelClass) {
        String templateClassPath = ClassPathResourceTemplate.getPath();
        String templateFileName = ClassPathResourceTemplate.getFilename();
        String removePrefixTemplatePath = ClassPathResourceTemplate.getPath().replace(TEMPLATE_DIR, "");
        String removePrefixParentDirPath = removePrefixTemplatePath.substring(0, removePrefixTemplatePath.lastIndexOf("/"));
        JavaModel model = new JavaModel(modelClass);

        parseTemplateAndGenerateFile(model, templateClassPath, templateFileName, removePrefixParentDirPath);
    }

    /**
     * 递归根文件生成文件
     * @param file
     * @param modelClass
     */
    public void generateRecursivelyByClassPathFile(File file, Class modelClass) {
        if (!file.exists()) {
            logger.error("File[{}] is not exists!", file.getPath());
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File subFile : files) {
                generateRecursivelyByClassPathFile(subFile, modelClass);
            }
        } else {
            parseClassPathFileTemplateAndGenerateFile(file, modelClass);
        }
    }

    /**
     *
     * @param classPathFileTemplate
     * @param modelClass
     */
    public void parseClassPathFileTemplateAndGenerateFile(File classPathFileTemplate, Class modelClass) {
        String relativeTemplatePath = JAVA_RESOURCES_DIR + TEMPLATE_DIR;
        if(!classPathFileTemplate.getPath().contains(new File(relativeTemplatePath).getPath())){
            logger.error("Can't generate, File template[{}]. template file must under the folder [{}]", classPathFileTemplate.getName(), TEMPLATE_DIR);
            return;
        }
        if (!"/".equals(File.separator)) {
            relativeTemplatePath = relativeTemplatePath.replaceAll("/", "\\" + File.separator);
        }
        String removePrefixParentDirPath = classPathFileTemplate.getParent().length() < relativeTemplatePath.length() ? "" : classPathFileTemplate.getParent().replace(relativeTemplatePath, "");
        String templateFileName = classPathFileTemplate.getName();

        String removeResourcesPath = JAVA_RESOURCES_DIR;
        if (!"/".equals(File.separator)) {
            removeResourcesPath = removeResourcesPath.replaceAll("/", "\\" + File.separator);
        }
        String templateClassPath = classPathFileTemplate.getPath().replace(removeResourcesPath, "");
        JavaModel model = new JavaModel(modelClass);
        parseTemplateAndGenerateFile(model, templateClassPath, templateFileName, removePrefixParentDirPath);
    }

    public void parseTemplateAndGenerateFile(JavaModel model, String templateClassPath, String templateFileName, String removePrefixParentDirPath) {
        if (templateFileName.contains(".java")) {
            TemplateModel templateModel = new JavaTemplateModel(removePrefixParentDirPath, templateFileName, model);
            generateFile(templateClassPath, JAVA_SOURCE_DIR + templateModel.getTargetFilePath(), templateModel);
        } else {
            logger.debug("Static resources generate!");
            String targetFilePath = JAVA_RESOURCES_DIR;

            if (removePrefixParentDirPath == null || removePrefixParentDirPath.trim() == "") {
                targetFilePath += templateFileName.substring(0, templateFileName.lastIndexOf("."));
            } else {
                String tempPath;
                if(templateFileName.contains(".ftl")){
                    tempPath = "/" + model.getInstanceName() + "/" + templateFileName.substring(0, templateFileName.lastIndexOf("."));
                } else {
                    tempPath = "/" + templateFileName.substring(0, templateFileName.lastIndexOf("."));
                }
                if (!"/".equals(File.separator)) {
                    targetFilePath += removePrefixParentDirPath.replaceAll("\\" + File.separator, "/") + tempPath;
                } else {
                    targetFilePath += removePrefixParentDirPath + tempPath;
                }
            }
            generateFile(templateClassPath, targetFilePath, model);
        }
    }

    public void generateFile(String templateClassPath, String targetFilePath, Object templateModel) {
        FileWriter writer = null;
        try {
            ClassPathTemplateLoader classPathTemplateLoader = new ClassPathTemplateLoader("/", "");
            Handlebars handlebars = new Handlebars(classPathTemplateLoader);
            Template template = handlebars.compile(templateClassPath);
            logger.info(template.apply(templateModel));

            File file = new File(targetFilePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter(targetFilePath);
            template.apply(templateModel, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void modelGenerator(String mybatisConfigFileLocation) {
        try {
            if(mybatisConfigFileLocation == null || mybatisConfigFileLocation.trim() == "")
                mybatisConfigFileLocation = MYBATIS_CONFIG_FILE_LOCATION;
            List<String> warnings = new ArrayList<>();
            boolean overwrite = true;
            File configFile = new File(mybatisConfigFileLocation);

            /*Context context = new Context(ModelType.FLAT);
            context.setId("mysql");
            context.setTargetRuntime("MyBatis3Simple");

            JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
            jdbcConnectionConfiguration.setConnectionURL("jdbc:mysql://10.10.13.12:3306/test");
            jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
            jdbcConnectionConfiguration.setUserId("root");
            jdbcConnectionConfiguration.setPassword("root");
            context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
            javaModelGeneratorConfiguration.setTargetPackage("");
            javaModelGeneratorConfiguration.setTargetProject("");
            context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

            Configuration config1 = new Configuration();
            config1.addContext(context);*/

            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);

            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
