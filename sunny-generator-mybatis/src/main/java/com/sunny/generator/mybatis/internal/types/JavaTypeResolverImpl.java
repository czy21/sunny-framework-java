package com.sunny.generator.mybatis.internal.types;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

public class JavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    @Override
    protected JdbcTypeInformation overrideDefault(IntrospectedColumn column, JdbcTypeInformation defaultTypeInformation) {
        return switch (column.getJdbcType()) {
            case Types.LONGVARCHAR -> typeMap.get(Types.VARCHAR);
            case Types.TINYINT, Types.SMALLINT -> typeMap.get(Types.INTEGER);
            default -> super.overrideDefault(column, defaultTypeInformation);
        };
    }
}