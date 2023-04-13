package com.example;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.example.listener.DynamicReadListener;
import com.example.template.UserTemplate;
import com.example.util.EasyExcels;

/**
 * @author zyw
 */
public class Start {

    public static void main(String[] args) {
        test1();

    }


    public static void test1() {
        String path = "C:\\Users\\user\\Desktop\\easyExcelReadTest.xlsx";
        EasyExcels.read(path, UserTemplate.class)
                .forEach(System.out::println);
    }

    public static void test2() {
        String path = "C:\\Users\\user\\Desktop\\easyExcelReadTest.xlsx";
        DynamicReadListener<UserTemplate> userTemplateReadListener = DynamicReadListener.of(UserTemplate.class);

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
