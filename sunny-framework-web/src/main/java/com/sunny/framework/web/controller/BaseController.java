package com.sunny.framework.web.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class BaseController {

    public final static String RESPONSE_TIMESTAMP_KEY = "timestamp";
    public final static String RESPONSE_ERROR_KEY = "error";
    public final static String RESPONSE_DATA_KEY = "data";

    protected ResponseEntity<byte[]> downloadExcel(List<?> data, Class<?> head, String fileName) {
        return downloadExcel(data,head,fileName,null);
    }

    protected ResponseEntity<byte[]> downloadExcel(byte[] bytes, String fileName) {
        return responseEntity(bytes, httpHeaders(fileName), HttpStatus.OK);
    }

    protected ResponseEntity<byte[]> downloadExcel(List<?> data, Class<?> head, String fileName, String sheetName) {
        return responseEntity(body(data, head, sheetName), httpHeaders(fileName), HttpStatus.OK);
    }

    protected ResponseEntity<byte[]> responseEntity(byte[] body, HttpHeaders httpHeaders, HttpStatus status) {
        return new ResponseEntity<>(body, httpHeaders, status);
    }

    protected HttpHeaders httpHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("filename", fileName);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return headers;

    }

    protected byte[] body(List<?> data, Class<?> head, String sheetName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream, head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
        excelWriterBuilder.sheet(null, sheetName).doWrite(data);
        return outputStream.toByteArray();
    }

}
