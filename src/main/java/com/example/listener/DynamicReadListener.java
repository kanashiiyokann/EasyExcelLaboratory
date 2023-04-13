package com.example.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.alibaba.excel.converters.DefaultConverterLoader;
import com.alibaba.excel.exception.ExcelCommonException;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zyw
 */
@Slf4j
public class DynamicReadListener<T> implements ReadListener<Object> {
    private final Class<T> tClass;
    private final List<Field> excelPropertyList;
    private Map<String, Integer> headIndexMap;
    private final Map<ConverterKeyBuild.ConverterKey, Converter<?>> defaultConverterMap;

    @Getter
    private final List<T> list = new ArrayList<>(16);

    public DynamicReadListener(Class<T> tClass) {
        this.tClass = tClass;
        this.excelPropertyList = getAllExcelProperty(tClass);
        for (Field field : this.excelPropertyList) {
            field.setAccessible(true);
        }

        defaultConverterMap = DefaultConverterLoader.loadDefaultReadConverter();

    }


    private List<Field> getAllExcelProperty(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>(16);

        Class<?> theClass = clazz;
        while (!theClass.equals(Object.class)) {

            fieldList.addAll(Arrays.asList(theClass.getDeclaredFields()));
            theClass = theClass.getSuperclass();
        }
        return fieldList.stream().filter(e -> e.getAnnotation(ExcelProperty.class) != null)
                .collect(Collectors.toList());
    }


    @Override
    @SneakyThrows
    public void invoke(Object obj, AnalysisContext context) {
        T t = tClass.newInstance();
        Map<Integer, Cell> data = context.readRowHolder().getCellMap();
        for (Field field : this.excelPropertyList) {

            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            List<Cell> cellDataList = Arrays.stream(excelProperty.value()).map(headIndexMap::get).filter(Objects::nonNull)
                    .map(data::get).collect(Collectors.toList());

            for (Cell readCellData : cellDataList) {

                Object val = this.readData(context, field, readCellData);
                field.set(t, val);
                if (val != null) {
                    break;
                }
            }

        }

        list.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> cellDataMap, AnalysisContext context) {
        this.headIndexMap = new HashMap<>(cellDataMap.size());
        cellDataMap.forEach((k, v) -> this.headIndexMap.put(v.getStringValue(), k));
    }


    @SneakyThrows
    private Object readData(AnalysisContext context, Field field, Cell cell) {
        if (!(cell instanceof ReadCellData)) {
            return null;
        }
        ReadCellData<?>  readCellData=(ReadCellData<?>)cell;

        ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);

        Converter<?> converter;

        if (excelProperty != null && !Objects.equals(excelProperty.converter(), AutoConverter.class)) {
            Class<? extends Converter<?>> convertClazz = excelProperty.converter();
            try {
                converter = convertClazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new ExcelCommonException(
                        "Can not instance custom converter:" + convertClazz.getName());
            }
        } else {
            ConverterKeyBuild.ConverterKey key = new ConverterKeyBuild.ConverterKey(field.getType(), readCellData.getType());
            converter = defaultConverterMap.get(key);
        }
        ExcelContentProperty excelContentProperty = new ExcelContentProperty();
        excelContentProperty.setField(field);
        excelContentProperty.setConverter(converter);
        Object val = converter.convertToJavaData(readCellData, excelContentProperty, context.currentReadHolder().globalConfiguration());
        if (val instanceof String && ((String) val).trim().length() == 0) {
            return null;
        }
        return val;
    }
}
