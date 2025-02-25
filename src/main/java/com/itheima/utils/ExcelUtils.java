package com.itheima.utils;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtils {

    /**
     * Excel导出
     *
     * @param response      response
     * @param fileName      文件名
     * @param sheetName     sheetName
     * @param list          数据List
     * @param pojoClass     对象Class
     */
    public static void exportExcel(HttpServletResponse response, String fileName, String sheetName, List<?> list,
                                   Class<?> pojoClass) throws IOException {
        if(StringUtils.isBlank(fileName)){
            //当前日期
            fileName = DateUtils.format(new Date());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" +  fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), pojoClass).registerConverter(new LongStringConverter()).sheet(sheetName).doWrite(list);
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response      response
     * @param fileName      文件名
     * @param sheetName     sheetName
     * @param sourceList    原数据List
     * @param targetClass   目标对象Class
     */
    public static void exportExcelToTarget(HttpServletResponse response, String fileName, String sheetName, List<?> sourceList,
                                           Class<?> targetClass) throws Exception {
        List targetList = new ArrayList<>(sourceList.size());
        for(Object source : sourceList){
            Object target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        exportExcel(response, fileName, sheetName, targetList, targetClass);
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param fileName      文件名
     * @param sheetName     sheetName
     * @param sourceList    原数据List
     * @param targetClass   目标对象Class
     */
    public static InputStream exportExcelToInputStream(String fileName, String sheetName, List<?> sourceList,
                                                       Class<?> targetClass) throws Exception {
        List targetList = new ArrayList<>(sourceList.size());
        for(Object source : sourceList){
            Object target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        return exportExcelInputStream(fileName, sheetName, targetList, targetClass);
    }

    /**
     * 导出 Excel 文件并返回 InputStream
     *
     * @param fileName   文件名
     * @param sheetName  工作表名称
     * @param list       数据列表
     * @param pojoClass  POJO 类型
     * @return InputStream 对象
     * @throws IOException
     */
    public static InputStream exportExcelInputStream( String fileName, String sheetName, List<?> list, Class<?> pojoClass) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            // 当前日期
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }

        // 创建 ByteArrayOutputStream 用于写入 Excel 文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 使用 EasyExcel 写入 Excel 文件到 ByteArrayOutputStream
        EasyExcel.write(baos, pojoClass)
                .registerConverter(new LongStringConverter())
                .sheet(sheetName)
                .doWrite(list);

        // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        return bais;
    }
}