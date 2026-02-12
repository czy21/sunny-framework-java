package com.sunny.generator.mybatis.plugins;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class DomainPlugin extends PluginAdapter {

    private static final String mybatisPlusSwitch = "mybatisPlus";

    private TopLevelClass topLevelClass;

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
        this.topLevelClass = topLevelClass;
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedFiles = new ArrayList<>();
        if (topLevelClass == null || !(enabledOpenApi3 | enabledSwagger)) return generatedFiles;
        TopLevelClass apiClass = new TopLevelClass(context.getModelGeneratorConfiguration().getTargetPackage() + ".dto." + topLevelClass.getType().getShortName() + "DTO");
        apiClass.setVisibility(JavaVisibility.PUBLIC);
        List<String> excludeImportTypes = new ArrayList<>(Arrays.stream(LombokPlugin.Annotations.values()).filter(t -> t != LombokPlugin.Annotations.DATA).map(t -> t.javaType.getFullyQualifiedName()).toList());
        excludeImportTypes.add(Annotations.TABLE_NAME.javaType.getFullyQualifiedName());
        List<String> excludeAnnotation = new ArrayList<>(Arrays.stream(LombokPlugin.Annotations.values()).filter(t -> t != LombokPlugin.Annotations.DATA).map(t -> t.name).toList());
        excludeAnnotation.add(Annotations.TABLE_NAME.name);
        topLevelClass.getImportedTypes().stream()
                .filter(t -> !excludeImportTypes.contains(t.getFullyQualifiedName()))
                .forEach(apiClass::addImportedType);
        topLevelClass.getAnnotations().stream()
                .filter(t -> excludeAnnotation.stream().noneMatch(t::startsWith))
                .forEach(apiClass::addAnnotation);
        topLevelClass.getFields().forEach(t -> apiClass.addField(new Field(t)));
        apiClass.getMethods().forEach(t -> apiClass.addMethod(new Method(t)));
        addOpenAPI(introspectedTable, apiClass);
        generatedFiles.add(new GeneratedJavaFile(apiClass, context.getModelGeneratorConfiguration().getTargetProject(),false));
        return generatedFiles;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        if (enabledMybatisPlus) {
            FullyQualifiedJavaType baseMapperJavaType = new FullyQualifiedJavaType("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            baseMapperJavaType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
            interfaze.addImportedType(baseMapperJavaType);
            interfaze.addSuperInterface(baseMapperJavaType);
            interfaze.getMethods().clear();
        }
        return super.clientGenerated(interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (enabledMybatisPlus) {
            document.getRootElement().getElements().removeIf(t -> t instanceof XmlElement && !"resultMap".equals(((XmlElement) t).getName()));
        }
        document.getRootElement().getElements().add(new TextElement(""));
        return super.sqlMapDocumentGenerated(document, introspectedTable);
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
                        String remark = c.getRemarks().orElse(null);
                        if (remark != null && !remark.isEmpty()) {
                            if (enabledOpenApi3 | enabledOpenapi3InModel) {
                                Annotations schemaAnnotation = Annotations.SCHEMA;
                                schemaAnnotation.options.clear();
                                schemaAnnotation.options.add("description = %s".formatted("\"" + remark + "\""));
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
        enabledMybatisPlus = isTrue(properties.getProperty(mybatisPlusSwitch, context.getProperties().getProperty(mybatisPlusSwitch)));
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