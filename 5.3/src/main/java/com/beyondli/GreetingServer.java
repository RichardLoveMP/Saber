package com.beyondli;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.beyondli.bean.CurrentMovementBean;
import com.beyondli.bean.MPUReadBean;
import com.beyondli.common.config.WebSocketServer;

public class GreetingServer extends Thread{
    private ServerSocket serverSocket;

    public GreetingServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(1000);  // 0 means infinity
    }

    public void run() {

        CoordinateTransform coordinateTransform = new CoordinateTransform();
        while (true) {

            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(2345);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            //创建字节数组
            byte[] data=new byte[1024];
            //创建数据包对象，传递字节数组
            DatagramPacket dp=new DatagramPacket(data, data.length);
            //调用ds对象的方法receive传递数据包
            try {
                ds.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //获取发送端的IP地址对象
            String ip=dp.getAddress().getHostAddress();

            //获取发送的端口号
            int port=dp.getPort();

            //获取接收到的字节数
            int length=dp.getLength();
            String nmsl = new String(data,0, length);
            System.out.println(nmsl+"...."+ip+":"+port);
            MPUReadBean bean = Utilities.MPUDataParser(nmsl);
            coordinateTransform.updateSensorStatus(bean);
            ds.close();

            try {
                //WebSocketServer.sendInfo(cnmd1 + cnmd2, "20");
                WebSocketServer.sendInfo(coordinateTransform.getMessageToSend(), "20");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建数据包传输对象DatagramSocket 绑定端口号


        /*
        while (true) {
            try {
                Socket server = serverSocket.accept();
   //            System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

            //    System.out.println(in.readUTF());
                List<Byte> list = new ArrayList<>();
                for (int i=0; i<2; i++) {
                    list.add(in.readByte());
                }
                int messageLength = Integer.parseInt(Utilities.Byte2ASCII(list));
                list.clear();
                for (int i=0; i<messageLength+4; i++) {
                    list.add(in.readByte());
                }
                String nmsl = Utilities.Byte2ASCII(list);
                MPUReadBean bean;
                bean = Utilities.MPUDataParser(nmsl);
               // bean.printStatus();
                coordinateTransform.updateSensorStatus(bean);
                CurrentMovementBean movementBean = coordinateTransform.getCurrentMovement();
                String cnmd1 = movementBean.fuckFrontEnd();
                String cnmd2 = coordinateTransform.getWorldCoordinate().fuckFrontEnd();
                try {
                    //WebSocketServer.sendInfo(cnmd1 + cnmd2, "20");
                    WebSocketServer.sendInfo(coordinateTransform.getMessageToSend(), "20");
                } catch (IOException e) {
                    e.printStackTrace();
                }

             //   DataOutputStream out = new DataOutputStream(server.getOutputStream());
               // out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
                 //       + "\nGoodbye!");
                server.close();


            } catch (SocketTimeoutException s) {
                System.out.println("socket time out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }

         */
    }

    public static void main(String [] args) {
        int port = args.length != 0 ? Integer.parseInt(args[0]) : 2345;
        try {
            Thread t = new GreetingServer(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
