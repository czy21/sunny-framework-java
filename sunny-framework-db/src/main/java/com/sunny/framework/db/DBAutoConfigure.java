package com.sunny.framework.db;


import com.sunny.framework.db.datasource.DynamicDataSourceConfigure;
import com.sunny.framework.db.datasource.DynamicDataSourceProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sunny-common-db-default.properties")
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(value = {DynamicDataSourceProperties.class})
@Import({DynamicDataSourceConfigure.class})
public class DBAutoConfigure {
}
