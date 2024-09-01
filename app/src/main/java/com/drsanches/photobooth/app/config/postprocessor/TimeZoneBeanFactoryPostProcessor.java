package com.drsanches.photobooth.app.config.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
class TimeZoneBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory ignored) throws BeansException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
