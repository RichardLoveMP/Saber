package com.beyondli.bean;

import java.util.Date;

public class CurrentMovementBean {
    private double pitch;
    private double roll;
    private double yaw;

    // the x,y,z velocity here are in world coordinate!
    private double x_velocity;
    private double y_velocity;
    private double z_velocity;

    private Date date;

    public void printStatus() {
       // System.out.println("Movement status at " + date +
         //       ": vx = " + x_velocity + ", vy = " + y_velocity + " vz = " + z_velocity +
           //     "; and pitch = " + pitch + ", roll = " + roll + ", yaw = " + yaw);
        System.out.printf("Movements: vx = %.4f, vy = %.4f, vz = %.4f, pitch = %.4f, roll = %.4f, yaw = %.4f \n", x_velocity, y_velocity, z_velocity, pitch, roll, yaw);
    }

    public double getMagnitude() {
        return Math.sqrt(x_velocity*x_velocity + y_velocity*y_velocity + z_velocity*z_velocity);
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getX_velocity() {
        return x_velocity;
    }

    public void setX_velocity(double x_velocity) {
        this.x_velocity = x_velocity;
    }

    public double getY_velocity() {
        return y_velocity;
    }

    public void setY_velocity(double y_velocity) {
        this.y_velocity = y_velocity;
    }

    public double getZ_velocity() {
        return z_velocity;
    }

    public void setZ_velocity(double z_velocity) {
        this.z_velocity = z_velocity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String fuckFrontEnd() {
        return "a" + pitch + "b" + roll + "c" + yaw; //+ "d" + x_velocity + "g" + y_velocity + "j" + z_velocity;
    }
}
