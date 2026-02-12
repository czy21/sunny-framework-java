package com.sunny.generator.mybatis.internal.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

public class JavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    public JavaTypeResolverImpl() {
        super();
        typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation("VARCHAR", new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("INTEGER", new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.SMALLINT, new JdbcTypeInformation("INTEGER", new FullyQualifiedJavaType(Integer.class.getName())));
    }
}