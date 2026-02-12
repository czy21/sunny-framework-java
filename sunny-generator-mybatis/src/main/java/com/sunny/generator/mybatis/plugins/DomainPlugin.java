package com.sunny.generator.mybatis.plugins;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class DomainPlugin extends PluginAdapter {

    private boolean enabledMybatisPlus;
    private boolean enabledOpenApi3;
    private boolean enabledOpenapi3InModel;
    private boolean enabledSwagger;
    private boolean enabledSwaggerInModel;
    private Set<String> openapiExcludeColumns;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String baseRecordType = introspectedTable.getBaseRecordType();
        if (!baseRecordType.endsWith("PO")) {
            baseRecordType += "PO";
        }
        introspectedTable.setBaseRecordType(baseRecordType);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (enabledMybatisPlus) {
            Annotations annotation = Annotations.TABLE_NAME;
            annotation.options.clear();
            annotation.appendOptions("value", introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
            topLevelClass.addImportedType(annotation.javaType);
            topLevelClass.addAnnotation(annotation.asAnnotation());
        }
        if (enabledOpenapi3InModel | enabledSwaggerInModel) {
            addOpenAPI(introspectedTable, topLevelClass);
        }
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedFiles = new ArrayList<>();
        TopLevelClass oriClass = introspectedTable.getGeneratedJavaFiles()
                .stream()
                .filter(t -> t.getCompilationUnit() instanceof TopLevelClass)
                .map(t -> (TopLevelClass) t.getCompilationUnit())
                .findFirst().orElse(null);
        if (oriClass == null || !(enabledOpenApi3 | enabledSwagger)) {
            return generatedFiles;
        }
        TopLevelClass apiClass = new TopLevelClass(context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".dto." + oriClass.getType().getShortName().replace("PO", "DTO"));
        apiClass.setVisibility(JavaVisibility.PUBLIC);
        List<String> excludeImportTypes = new ArrayList<>(Arrays.stream(LombokPlugin.Annotations.values()).filter(t -> t != LombokPlugin.Annotations.DATA).map(t -> t.javaType.getFullyQualifiedName()).toList());
        excludeImportTypes.add(Annotations.TABLE_NAME.javaType.getFullyQualifiedName());
        List<String> excludeAnnotation = new ArrayList<>(Arrays.stream(LombokPlugin.Annotations.values()).filter(t -> t != LombokPlugin.Annotations.DATA).map(t->t.name).toList());
        excludeAnnotation.add(Annotations.TABLE_NAME.name);
        oriClass.getImportedTypes().stream()
                .filter(t -> !excludeImportTypes.contains(t.getFullyQualifiedName()))
                .forEach(apiClass::addImportedType);
        oriClass.getAnnotations().stream()
                .filter(t -> excludeAnnotation.stream().noneMatch(t::startsWith))
                .forEach(apiClass::addAnnotation);
        oriClass.getFields().forEach(apiClass::addField);
        apiClass.getMethods().forEach(apiClass::addMethod);
        addOpenAPI(introspectedTable, apiClass);
        generatedFiles.add(new GeneratedJavaFile(apiClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getJavaFormatter()));
        return generatedFiles;
    }

    private void addOpenAPI(IntrospectedTable introspectedTable, TopLevelClass topLevelClass) {
        Set<Annotations> annotations = new HashSet<>();
        for (Field f : topLevelClass.getFields()) {
            introspectedTable.getAllColumns().stream()
                    .filter(c -> c.getJavaProperty().equals(f.getName()))
                    .filter(c -> !openapiExcludeColumns.contains(c.getActualColumnName()))
                    .findFirst()
                    .ifPresent(c -> {
                        f.getJavaDocLines().clear();
                        if (c.getRemarks() != null && !c.getRemarks().isEmpty()) {
                            if (enabledOpenApi3 | enabledOpenapi3InModel) {
                                Annotations schemaAnnotation = Annotations.SCHEMA;
                                schemaAnnotation.options.clear();
                                schemaAnnotation.options.add("description = %s".formatted("\"" + c.getRemarks() + "\""));
                                String schemaAnnotationString = schemaAnnotation.asAnnotation();
                                if (f.getAnnotations().stream().noneMatch(t -> t.equals(schemaAnnotationString))) {
                                    f.addAnnotation(schemaAnnotation.asAnnotation());
                                    annotations.add(schemaAnnotation);
                                }
                            }
                            if (enabledSwagger | enabledSwaggerInModel) {

                            }
                        }
                    });
        }
        annotations.forEach(t -> topLevelClass.addImportedType(t.javaType));
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        enabledMybatisPlus = isTrue(properties.getProperty("mybatisPlus"));
        enabledOpenApi3 = isTrue(properties.getProperty("openapi3"));
        enabledOpenapi3InModel = isTrue(properties.getProperty("openapi3InModel"));

        enabledSwagger = isTrue(properties.getProperty("swagger"));
        enabledSwaggerInModel = isTrue(properties.getProperty("swaggerInModel"));

        openapiExcludeColumns = new HashSet<>(Arrays.asList(properties.getProperty("openapiExcludeColumns", "").split(",")));
    }

    private enum Annotations {
        TABLE_NAME("tableName", "@TableName", "com.baomidou.mybatisplus.annotation.TableName"),
        SCHEMA("schema", "@Schema", "io.swagger.v3.oas.annotations.media.Schema");

        private final String paramName;
        private final String name;
        private final FullyQualifiedJavaType javaType;
        private final List<String> options;

        Annotations(String paramName, String name, String className) {
            this.paramName = paramName;
            this.name = name;
            this.javaType = new FullyQualifiedJavaType(className);
            this.options = new ArrayList<>();
        }

        private static Annotations getValueOf(String paramName) {
            for (Annotations annotation : Annotations.values())
                if (String.CASE_INSENSITIVE_ORDER.compare(paramName, annotation.paramName) == 0)
                    return annotation;
            return null;
        }

        private static String quote(String value) {
            if (Boolean.TRUE.toString().equals(value) || Boolean.FALSE.toString().equals(value))
                return value;
            return value.replaceAll("[\\w]+", "\"$0\"");
        }

        private void appendOptions(String key, String value) {
            String keyPart = key.substring(key.indexOf(".") + 1);
            String valuePart = value.contains(",") ? String.format("{%s}", value) : value;
            this.options.add(String.format("%s = %s", keyPart, quote(valuePart)));
        }

        private String asAnnotation() {
            return options.isEmpty() ? name : (name + "(" + String.join(", ", options) + ")");
        }
    }
}