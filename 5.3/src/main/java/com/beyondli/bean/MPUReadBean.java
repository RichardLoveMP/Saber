package com.beyondli.bean;

import java.util.Date;

/**
 * Author: Group 26
 * Date: 2021/05/14
 */
public class MPUReadBean {



    private double x_acc;
    private double y_acc;
    private double z_acc;
    private double gx;
    private double gy;
    private double gz;
    private Date date;
    private double idx;

    public MPUReadBean() {}
    public MPUReadBean(double x_acc, double y_acc, double z_acc, double gx, double gy, double gz) {
        this.x_acc = x_acc;
        this.y_acc = y_acc;
        this.z_acc = z_acc;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
    }

    public void printStatus() {
        System.out.printf("MPU data: ax = %.4f, ay = %.4f, az = %.4f, gx = %.4f, gy = %.4f, gz = %.4f", x_acc, y_acc, z_acc, gx, gy, gz);
        System.out.printf("\n");
    }


    public double getTotalMagnitude() {
        return Math.sqrt(x_acc*x_acc + y_acc*y_acc + z_acc*z_acc);
    }

    public double getX_acc() {
        return x_acc;
    }

    public double getY_acc() {
        return y_acc;
    }

    public double getZ_acc() {
        return z_acc;
    }

    public double getGx() {
        return gx;
    }

    public double getGy() {
        return gy;
    }

    public double getGz() {
        return gz;
    }

    public void setX_acc(double x_acc) {
        this.x_acc = x_acc;
    }

    public void setY_acc(double y_acc) {
        this.y_acc = y_acc;
    }

    public void setZ_acc(double z_acc) {
        this.z_acc = z_acc;
    }

    public void setGx(double gx) {
        this.gx = gx;
    }

    public void setGy(double gy) {
        this.gy = gy;
    }

    public void setGz(double gz) {
        this.gz = gz;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getIdx() {
        return idx;
    }

    public void setIdx(double idx) {
        this.idx = idx;
    }
}
