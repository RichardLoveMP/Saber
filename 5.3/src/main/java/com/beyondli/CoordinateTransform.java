package com.beyondli;

import com.alibaba.fastjson.JSONObject;
import com.beyondli.bean.CurrentMovementBean;
import com.beyondli.bean.MPUReadBean;
import com.beyondli.bean.WorldCoordinateBean;



import java.sql.Time;
import java.util.Date;
import java.lang.Math.*;

//import org.apache.commons.math3.linear.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class CoordinateTransform {
    private CurrentMovementBean cm1;
    private CurrentMovementBean cm2;
    private CurrentMovementBean cm;
    private CurrentMovementBean cmYin;
    private WorldCoordinateBean worldCoordinate;
    private MPUReadBean MPUErrorCorrection;
    private MPUReadBean MPUCurrentRead;
    private MPUReadBean worldAcceleration;


    private double accelerationMagnitude;
    private Date lastUpdateTime;

    public CoordinateTransform() {
        lastUpdateTime = new Date();
        cm1 = new CurrentMovementBean();
        cm2 = new CurrentMovementBean();
        cm = new CurrentMovementBean();
        cmYin = new CurrentMovementBean();
        worldCoordinate = new WorldCoordinateBean();
        MPUCorrectionInit();

        worldAcceleration = new MPUReadBean();

        accelerationMagnitude = 9.8;
    }


    private void MPUCorrectionInit() {
        // TODO 现在是hardcode，以后要改
        MPUReadBean temp = new MPUReadBean();
        temp.setX_acc(0.05);
        temp.setY_acc(0.06);
      //  temp.setZ_acc(-10.3);
        temp.setGx(-0.06);
        temp.setGz(-0.01);
        temp.setDate(new Date(0));
        this.MPUErrorCorrection = temp;
    }

    private void correctMPURead(MPUReadBean input) {
        MPUReadBean mpu = Utilities.MPUReadsAdding(input, MPUErrorCorrection);
        double threshold = 0.4;
        double threshold2 = 0.1;
        if (Math.abs(mpu.getX_acc()) < threshold) mpu.setX_acc(0);
        if (Math.abs(mpu.getY_acc()) < threshold) mpu.setY_acc(0);
        if (Math.abs(mpu.getZ_acc()) < threshold) mpu.setZ_acc(0);
        if (Math.abs(mpu.getGx()) < threshold2) mpu.setGx(0);
        if (Math.abs(mpu.getGy()) < threshold2) mpu.setGy(0);
        if (Math.abs(mpu.getGz()) < threshold2) mpu.setGz(0);
        this.MPUCurrentRead = mpu;
    }


    public CurrentMovementBean calculateAngleFromAcceleration() {
        // Higher accuracy in static condition. Doesn't require an integral, so the error doesn't
        // accumulate
        MPUReadBean mpu = this.MPUCurrentRead;

        cm1.setYaw(0);
        cm1.setRoll(Math.atan(mpu.getY_acc() / mpu.getZ_acc()));
        cm1.setPitch(-Math.atan(mpu.getX_acc() /
                Math.sqrt(mpu.getY_acc()* mpu.getY_acc() + mpu.getZ_acc() * mpu.getZ_acc())));
        accelerationMagnitude = mpu.getTotalMagnitude();
        return cm1;
    }

    public CurrentMovementBean calculateAngleFromGyroscope() {
        // The error might accumulate due to the integration!
        MPUReadBean mpu = this.MPUCurrentRead;

        Date newTime = new Date();
        double deltaTime = newTime.getTime() - lastUpdateTime.getTime();
        deltaTime /= 1000;
      //  System.out.println(deltaTime);

        double gx = mpu.getGx();
        double gy = mpu.getGy();
        double gz = mpu.getGz();
        double r = cm2.getRoll();
        double p = cm2.getPitch();
        double y = cm2.getYaw();
        // for the following 3 var, r p y stand for roll, pitch, yaw respectively
        double matrix[][] = new double[][] {
                {1, sin(p)*sin(r)/cos(p),       (cos(r)*sin(p))/cos(p)},
                {0, cos(r), -sin(r)},
                {0, sin(r)/cos(p),  cos(r)/cos(p)}
        };

        double gyroMeasurements[][] = new double[][] {
                {MPUCurrentRead.getGx()},
                {MPUCurrentRead.getGy()},
                {MPUCurrentRead.getGz()}
        };

        double drdt = 1 * gx
                + (sin(cm2.getPitch()) * sin(cm2.getRoll()) / cos(cm2.getPitch())) * gy
                + (cos(cm2.getRoll()) * sin(cm2.getPitch()) / cos(cm2.getPitch())) * gz;
        double dpdt = 0
                + (cos(cm2.getRoll())) * gy
                + (-1) * sin(cm2.getRoll()) * gz;
        double dydt = 0
                + (sin(cm2.getRoll()) / cos(cm2.getPitch())) * gy
                + (cos(cm2.getRoll()) / cos(cm2.getPitch())) * gz;

        cm2.setRoll(cm2.getRoll() + deltaTime * drdt);
        cm2.setPitch(cm2.getPitch() + deltaTime * dpdt);
        // yaw 无法通过加速度传感器来融合数据，所以误差会越积累越大
        // 解决方法是加一项偏置，使yaw永远向0回归
        double reduceCoefficient = 0.99;
        if (MPUCurrentRead.getTotalMagnitude() < 11) reduceCoefficient = 0.9;
        cm2.setYaw((cm2.getYaw() + deltaTime * dydt)*reduceCoefficient);

        return cm2;
    }

    private CurrentMovementBean angleDataFusion() {
        double weight1 = 20.0 / Utilities.cutInterval(Math.pow(this.accelerationMagnitude/9.0, 40), 200, 20000);
        System.out.println(weight1);
        double weight2 = 1;
        double newPitch = (weight1 * cm1.getPitch() + weight2 * cm2.getPitch()) / (weight1 + weight2);
        double newRoll = (weight1 * cm1.getRoll() + weight2 * cm2.getRoll()) / (weight1 + weight2);
        double newYaw = 0;//cm2.getYaw();  //(weight1 * cm1.getYaw() + weight2 * cm2.getYaw()) / (weight1 + weight2);
        cm1.setPitch(newPitch);
        cm1.setRoll(newRoll);
        cm1.setYaw(newYaw);
        cm2.setPitch(newPitch);
        cm2.setRoll(newRoll);
        cm2.setYaw(newYaw);
        cm.setPitch(newPitch);
        cm.setRoll(newRoll);
        cm.setYaw(newYaw);
        return cm;
    }

    private void calculateVelocity() {
        Date newTime = new Date();
        double deltaTime = newTime.getTime() - lastUpdateTime.getTime();
        deltaTime /= 30;

        // rotation matrix along x, y, z axis (axis in world coordinate, not local coordinate)
        double r = this.cm.getRoll();
        double p = this.cm.getPitch();
        double y = this.cm.getYaw();
        double xRotationMatrixInverse[][] = new double[][] {
                {1,                        0,       0},
                {0, (1-sin(r)*sin(r))/cos(r), -sin(r)},
                {0,                   sin(r),  cos(r)}
        };
        double yRotationMatrixInverse[][] = new double[][] {
                {(1-sin(p)*sin(p))/cos(p), 0, sin(p)},
                {0,                        1,      0},
                {-sin(p),                  0, cos(p)}
        };
        double zRotationMatrixInverse[][] = new double[][] {
                {(1-sin(y)*sin(y))/cos(y), sin(y), 0},
                {sin(y),                   cos(y), 0},
                {0,                        0,      1}
        };

        double accMeasurements[][] = new double[][] {
                {MPUCurrentRead.getX_acc()},
                {MPUCurrentRead.getY_acc()},
                {MPUCurrentRead.getZ_acc()}
        };

        double temp1[][] = Matrix.matrixMultiplication(xRotationMatrixInverse, accMeasurements);
        double temp2[][] = Matrix.matrixMultiplication(yRotationMatrixInverse, temp1);
        double realAcceleration[][] = Matrix.matrixMultiplication(zRotationMatrixInverse, temp2);

        worldAcceleration.setX_acc(realAcceleration[0][0]);
        worldAcceleration.setY_acc(realAcceleration[1][0]);
        worldAcceleration.setZ_acc(realAcceleration[2][0]);

        double reduceCoefficient = 0.95;
        double fuckGravity = 10.3;
        if (this.MPUCurrentRead.getTotalMagnitude() < 11) {
            System.out.println("nmsl");
      //      realAcceleration[0][0] *= 0.5;
        //    realAcceleration[1][0] *= 0.5;
          //  realAcceleration[2][0] *= 0.5;
            reduceCoefficient = 0.55;
    //        fuckGravity /= 2;
        }
        // 如果是加速度与运动方向是反的，那么这个加速度的值除以二
        double reduceCoefficientX = 1;
        double reduceCoefficientY = 1;
        double reduceCoefficientZ = 1;
        double oldX_velocity = cm.getX_velocity();
        double oldY_velocity = cm.getY_velocity();
        double oldZ_velocity = cm.getZ_velocity();

        if (cm.getX_velocity() * realAcceleration[0][0] < 0) {reduceCoefficientX = 0.14;}
        if (cm.getY_velocity() * realAcceleration[1][0] < 0) {reduceCoefficientY = 0.14;}
        if (cm.getZ_velocity() * realAcceleration[2][0] < 0) {reduceCoefficientZ = 0.14;}

        // 如果是加速度与速度同向，则隐藏层属性与普通层一样
        if (cm.getX_velocity() * realAcceleration[0][0] < 0) {
            cmYin.setX_velocity(cmYin.getX_velocity() + deltaTime * realAcceleration[0][0]);
        }

        if (Math.abs(cmYin.getX_velocity()) > 2 * 2 ) {

        }


        // 加一个隐藏cm计算，以便“既能避免倒车现象，又能防止对转向不灵敏”
        System.out.println(realAcceleration[0][0] + " " + realAcceleration[1][0] + " " + (realAcceleration[2][0] - fuckGravity) + " " + deltaTime * realAcceleration[0][0] * reduceCoefficientX);

        cm.setX_velocity((cm.getX_velocity() + deltaTime * realAcceleration[0][0] * reduceCoefficientX) * reduceCoefficient);
        cm.setY_velocity((cm.getY_velocity() + deltaTime * realAcceleration[1][0] * reduceCoefficientY) * reduceCoefficient);
        cm.setZ_velocity((cm.getZ_velocity() + deltaTime * (realAcceleration[2][0] - fuckGravity) * reduceCoefficientZ) * reduceCoefficient);  // TODO 这里硬编码了
        if (cm.getMagnitude() < 1.2) {
            cm.setX_velocity(0);
            cm.setY_velocity(0);
            cm.setZ_velocity(0);  // TODO 这里硬编码了
        }
        cm.setDate(newTime);

    }

    private void calculateWorldCoordinate() {
        Date newTime = new Date();
        double deltaTime = newTime.getTime() - lastUpdateTime.getTime();
        deltaTime /= 1000;

        double v_x = cm.getX_velocity();
        double v_y = cm.getY_velocity();
        double v_z = cm.getZ_velocity();
        double x = worldCoordinate.getX_position();
        double y = worldCoordinate.getY_position();
        double z = worldCoordinate.getZ_position();

        worldCoordinate.setRoll(cm.getRoll());
        worldCoordinate.setPitch(cm.getPitch());
        worldCoordinate.setYaw(cm.getYaw());

        worldCoordinate.setX_position(Utilities.cutInterval(x + v_x*deltaTime, -6.0, 6.0));
        worldCoordinate.setY_position(Utilities.cutInterval(y + v_y*deltaTime, -6, 6));
        worldCoordinate.setZ_position(Utilities.cutInterval(z + v_z*deltaTime, -6, 6));
        worldCoordinate.setDate(newTime);


    }

    // the following will be used by outside user

    public Date getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public CurrentMovementBean getCurrentMovement() {
        return cm;
    }

    @Deprecated
    public CurrentMovementBean getCurrentMovement(MPUReadBean mpu) {
        // don't use. only god knows what bug this func has.
        this.MPUCurrentRead = mpu;
        calculateAngleFromGyroscope();
        calculateAngleFromAcceleration();
        return angleDataFusion();
    }

    public void updateSensorStatus(MPUReadBean mpu) {
        // call this function as frequent as you can to reduce inaccuracy
        this.correctMPURead(mpu);   // set MPUCurrentRead
        calculateAngleFromAcceleration();
        calculateAngleFromGyroscope();
        angleDataFusion();
        calculateVelocity();
        calculateWorldCoordinate();
        lastUpdateTime = new Date();
        this.MPUCurrentRead.printStatus();
        this.cm.printStatus();
        this.worldCoordinate.printStatus();

    }

    public WorldCoordinateBean getWorldCoordinate() {
        return worldCoordinate;
    }

    public String getMessageToSend() {
        double ax = this.worldAcceleration.getX_acc();
        double ay = this.worldAcceleration.getY_acc();
        double az = this.worldAcceleration.getZ_acc();
        double vx = this.cm.getX_velocity();
        double vy = this.cm.getY_velocity();
        double vz = this.cm.getZ_velocity();
        double x = this.worldCoordinate.getX_position();
        double y = this.worldCoordinate.getY_position();
        double z = this.worldCoordinate.getZ_position();
        double pitch = this.cm.getPitch();
        double roll = this.cm.getRoll();
        double yaw = this.cm.getYaw();

        int punchingLeft = Math.abs(ay) > 5 ? 1 : 0;



        String string1 = "{\"ax\":" + ax + ", \"ay\":" + ay + ", \"az\":" + az + ",";
        String string2 = " \"vx\":" + vx + ", \"vy\":" + vy + ", \"vz\":" + vz + ",";
        String string3 = " \"x\":" + x + ", \"y\":" + y + ", \"z\":" + z + ",";
        String string4 = " \"pitch\":" + pitch + ", \"roll\":" + roll + ", \"yaw\":" + yaw + ",";
        String string5 = " \"punchingLeft\":" + punchingLeft + ", \"punchingRight\":" + punchingLeft + "}";

        String string = string1 + string2 + string3 + string4 + string5;

        // debug
        Utilities.writeLocalStrOne(string + "\n", "E:/log.txt");

        return string;
     //   JSONObject jsonObject = new JSONObject(string);
    }


}
