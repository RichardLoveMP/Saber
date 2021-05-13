package com.beyondli;

import com.beyondli.bean.MPUReadBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.io.*;

public class Utilities {
    static public double cutInterval(double n, double min, double max) {
        double result;
        result = Math.min(n, max);
        result = Math.max(result, min);
        return result;
    }

    static public String Byte2ASCII(List<Byte> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<list.size(); i++) {
            if ((byte) list.get(i) < 32) continue;
            stringBuilder.append((char) (byte) list.get(i));
        }
        return stringBuilder.toString();
    }

    static public MPUReadBean MPUDataParser(String s) {
        // ax=-0.03&ay=-0.11&az=10.38&gx=0.06&gy=0.00&gz=0.02
        int i1 = s.indexOf("ax=");
        int i2 = s.indexOf("&ay=");
        int i3 = s.indexOf("&az=");
        int i4 = s.indexOf("&gx=");
        int i5 = s.indexOf("&gy=");
        int i6 = s.indexOf("&gz=");
        //String ax = s.substring(i1+3, i2);
        //BigDecimal sss = new BigDecimal(ax);
        //double nmsl = sss.floatValue();
        MPUReadBean bean = new MPUReadBean();
        // Integer.parseInt can't handle negative decimals, so use BigDecimal;
        bean.setX_acc((new BigDecimal(s.substring(i1+3, i2))).floatValue());
        bean.setY_acc((new BigDecimal(s.substring(i2+4, i3))).floatValue());
        bean.setZ_acc((new BigDecimal(s.substring(i3+4, i4))).floatValue());
        bean.setGx((new BigDecimal(s.substring(i4+4, i5)).floatValue()));
        bean.setGy((new BigDecimal(s.substring(i5+4, i6)).floatValue()));
        bean.setGz((new BigDecimal(s.substring(i6+4, s.length())).floatValue()));

        return bean;
    }

    static public MPUReadBean MPUReadsAdding(MPUReadBean a, MPUReadBean b) throws AssertionError {
        // NOTE: this result bean's date is same as a
        // it may cause serious overflow if not properly used
        MPUReadBean bean = new MPUReadBean();
        bean.setX_acc(a.getX_acc() + b.getX_acc());
        bean.setY_acc(a.getY_acc() + b.getY_acc());
        bean.setZ_acc(a.getZ_acc() + b.getZ_acc());
        bean.setGx(a.getGx() + b.getGx());
        bean.setGy(a.getGy() + b.getGy());
        bean.setGz(a.getGz() + b.getGz());
        bean.setDate(a.getDate());
        try {
            assert (bean.getDate().getTime() > 10000);
        } catch (AssertionError e) {
            e.printStackTrace();
        }

        return bean;
    }
    

    public static void writeLocalStrOne(String str,String path){
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            if(str != null && !"".equals(str)){
                FileWriter fw = new FileWriter(file, true);
                fw.write(str);//写入本地文件中
                fw.flush();
                fw.close();
                System.out.println("执行完毕!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
