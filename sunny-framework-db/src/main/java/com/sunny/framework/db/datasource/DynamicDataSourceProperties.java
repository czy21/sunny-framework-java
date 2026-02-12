package com.sunny.framework.db.datasource;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.dynamic")
public class DynamicDataSourceProperties {
    private Map<String, HikariConfig> datasource;

    public Map<String, HikariConfig> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, HikariConfig> datasource) {
        this.datasource = datasource;
    }
}
