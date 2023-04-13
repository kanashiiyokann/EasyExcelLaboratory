package com.example.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.example.listener.DynamicReadListener;

import java.util.List;

/**
 * @author zyw
 */
public class EasyExcels {

    public static <T> List<T> read(String path, Class<T> tClass) {

        DynamicReadListener<T> userTemplateReadListener = DynamicReadListener.of(tClass);

        ExcelReaderSheetBuilder sheetBuilder = EasyExcelFactory.read(path)
                //.headRowNumber(1)
                //  .head(UserTemplate.class)
                .registerReadListener(userTemplateReadListener)
                //.registerConverter()
                .sheet();

        sheetBuilder.doRead();

        return userTemplateReadListener.getList();


    }
}
