package com.example;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.example.listener.DynamicReadListener;
import com.example.template.UserTemplate;

/**
 * @author zyw
 */
public class Start {

    public static void main(String[] args) {

        String path = "C:\\Users\\user\\Desktop\\easyExcelReadTest.xlsx";
        DynamicReadListener<UserTemplate> userTemplateReadListener = new DynamicReadListener<>(UserTemplate.class);

        ExcelReaderSheetBuilder sheetBuilder = EasyExcelFactory.read(path)
                //.headRowNumber(1)
                //  .head(UserTemplate.class)
                .registerReadListener(userTemplateReadListener)
                //.registerConverter()
                .sheet();

        sheetBuilder.doRead();

        userTemplateReadListener.getList()
                .forEach(System.out::println);

    }
}
