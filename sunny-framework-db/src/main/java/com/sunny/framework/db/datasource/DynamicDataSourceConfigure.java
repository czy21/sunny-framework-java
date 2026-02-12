package com.sunny.framework.db.datasource;



import com.sunny.framework.db.annotation.DS;
import com.sunny.framework.db.aspect.RoutingDataSourceAspect;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class DynamicDataSourceConfigure {

    DynamicDataSourceProperties dynamicDataSourceProperties;
    Map<Object, Object> dataSourceMap;

    public DynamicDataSourceConfigure(DynamicDataSourceProperties dynamicDataSourceProperties) {
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
        dataSourceMap = dynamicDataSourceProperties.getDatasource()
                .entrySet()
                .stream()
                .collect(HashMap::new,
                        (m, n) -> {
                            n.getValue().setPoolName(MessageFormat.format("datasource => {0}", n.getKey()));
                            HikariDataSource ds = new HikariDataSource(n.getValue());
                            m.put(n.getKey(), ds);
                        },
                        Map::putAll);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() throws NoSuchMethodException {
        RoutingDataSource rds = new RoutingDataSource();
        rds.setTargetDataSources(dataSourceMap);
        String master = (String) DS.class.getDeclaredMethod("value").getDefaultValue();
        rds.setDefaultTargetDataSource(dataSourceMap.get(master));
        return rds;
    }

    @Bean
    public RoutingDataSourceAspect routingDataSourceAspect() {
        return new RoutingDataSourceAspect();
    }

}
