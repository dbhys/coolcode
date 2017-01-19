import cn.com.zjs.cloud.coolcode.CoolCode;
import cn.com.zjs.cloud.coolcode.model.Sample;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/13.
 */
public class CoolCodeTest {

    //@Test
    public void mybatisGenerate(){
        new CoolCode().modelGenerator(null);
    }

    //@Test
    public void generateStandardCURD(){
        try {
            new CoolCode().generateStandardCURD(Sample.class);
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:hbs/custom/**/*.*");
            new CoolCode().generateByResources(resources, Sample.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
