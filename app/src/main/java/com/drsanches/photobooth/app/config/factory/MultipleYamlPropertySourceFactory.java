package com.drsanches.photobooth.app.config.factory;

import com.drsanches.photobooth.app.common.exception.server.ServerError;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;

import java.util.Properties;

public class MultipleYamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public @NonNull PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();

        if (properties == null || resource.getResource().getFilename() == null) {
            throw ServerError.createWithMessage("Resource is null");
        }

        return new PropertiesPropertySource(resource.getResource().getFilename(), properties);
    }
}
