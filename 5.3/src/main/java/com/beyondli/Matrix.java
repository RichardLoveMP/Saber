package com.beyondli;

//import org.apache.commons.math3.linear.*;

public class Matrix {
    public static double[][] matrixMultiplication(double a[][], double b[][]) {
        //当a的列数与矩阵b的行数不相等时，不能进行点乘，返回null
        if (a[0].length != b.length)
            return null;
        //c矩阵的行数y，与列数x
        int y = a.length;
        int x = b[0].length;
        double c[][] = new double[y][x];
        for (int i = 0; i < y; i++)
            for (int j = 0; j < x; j++)
                //c矩阵的第i行第j列所对应的数值，等于a矩阵的第i行分别乘以b矩阵的第j列之和
                for (int k = 0; k < b.length; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }   // https://blog.csdn.net/Waria/article/details/77417751

    @Deprecated
    public static double[][] inverseMatrix(double a[][]) {
        // only for test
        // can't handle cases if the input matrix is near singular
        return new double[3][3];
    }


}
