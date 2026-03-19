package com.cj.ssi.dropbox.loader.config;

import com.cj.ssi.dropbox.loader.repository.SsiPropertyKVRepository;
import com.cj.ssi.dropbox.loader.util.AESEncryptDecrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
https://stackoverflow.com/questions/40465360/spring-boot-use-database-and-application-properties-for-configuration
*/
@Component("additionalProperties")
public class ReadDbAdditionalAppProps implements EnvironmentAware {
   
    @Autowired
    private SsiPropertyKVRepository propertiesRepository;
    @Value("${ssi.app}")
    private String ssiApp;

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;

        Map<String, Object> propertySource = new HashMap<>();

        propertiesRepository.findAll()
            .stream()
            .filter(prop -> StringUtils.isNotBlank(prop.getPropKey()) && StringUtils.isNotBlank(prop.getPropValue()))
            .forEach(prop -> {
                String value;
                if (prop.getPropOnStart()) {
                    value = AESEncryptDecrypt.decrypt(prop.getPropValue(), ssiApp);
                } else {
                    value = prop.getPropValue();
                }
                propertySource.put(prop.getPropKey(), value);
            });

//        propertySource.entrySet().stream().forEach(System.out::println);

       var myFeatureEnabled =  environment.getProperty("myFeature", Boolean.class, Boolean.FALSE);
        configurableEnvironment
            .getPropertySources()
            .addAfter("systemEnvironment",
                    new MapPropertySource("additional-props", propertySource));
    }
   
}
