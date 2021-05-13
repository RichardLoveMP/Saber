package com.beyondli.bean;

import com.beyondli.Utilities;

import java.util.Date;

public class WorldCoordinateBean {
    // TODO 我们得记得限制一下区间
    private double pitch;
    private double roll;
    private double yaw;

    private double x_position;
    private double y_position;
    private double z_position;

    private Date date;

    public void printStatus() {
        //System.out.println("Movement status at " + date +
          //      ": x = " + x_position + ", y = " + y_position + " z = " + z_position +
            //    "; and pitch = " + pitch + ", roll = " + roll + ", yaw = " + yaw);
        System.out.printf("World Position: x = %.4f, y = %.4f, z = %.4f, pitch = %.4f, roll = %.4f, yaw = %.4f \n\n", x_position, y_position, z_position, pitch, roll, yaw);
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

    public double getX_position() {
        return x_position;
    }

    public void setX_position(double x_position) {
        this.x_position = Utilities.cutInterval(x_position, -300, 300);
    }

    public double getY_position() {
        return y_position;
    }

    public void setY_position(double y_position) {
        this.y_position = Utilities.cutInterval(y_position, -300, 300);
    }

    public double getZ_position() {
        return z_position;
    }

    public void setZ_position(double z_position) {
        this.z_position = Utilities.cutInterval(z_position, -300, 300);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String fuckFrontEnd() {
        return "d" + x_position + "g" + y_position + "j" + z_position;
    }
}
