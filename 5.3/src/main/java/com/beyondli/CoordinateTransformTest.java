package com.beyondli;

import com.beyondli.CoordinateTransform;
import com.beyondli.bean.CurrentMovementBean;
import com.beyondli.bean.MPUReadBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
* CoordinateTransform Tester. 
* 
* @author <Authors name> 
* @since <pre>4 2021</pre>
* @version 1.0 
*/


public class CoordinateTransformTest {

    private CoordinateTransform transform;

    @Before
    public void before() throws Exception {
        System.out.println("Testing coordination transforming... Initializing...");
        try {
            transform = new CoordinateTransform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() throws Exception {
        System.out.println("Test Finished!");
    }

    /**
     *
     * Method: calculateAngleFromAcceleration(MPUReadBean mpu)
     *
     */
    @Test
    public void testGetLastUpdateTime() throws Exception {
        try {
            System.out.println(transform.getLastUpdateTime() + " (getTime: " + transform.getLastUpdateTime().getTime() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




} 
