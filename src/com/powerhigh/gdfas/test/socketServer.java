package com.powerhigh.gdfas.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.powerhigh.gdfas.module.front.communicateWithFront;
import com.powerhigh.gdfas.util.Util;
public class socketServer{
	
	class sendThread extends Thread{
		Socket socket = null;
		public sendThread(Socket s){
			socket = s;
		}
		public void run(){
			while(true){
				try {
					String up = "FE000100000000000000";//前缀
					//1类任务数据主动上报(F49)
					//687200720068C88096DA07020B6E01000100010001060100020003000400050006005916
					//2类任务数据主动上报(F81+F82)
					//680201020168C88096DA07020B6E010002000100010A001520110801050000110000120000930000940000950100020A00152011080105000011000012000093000094000095BC16
					
					up += "680201020168C88096DA07020B6E010002000100010A001520110801050000110000120000930000940000950100020A00152011080105000011000012000093000094000095BC16";
					socket.getOutputStream().write(Util.strstobyte(up));
					
					sleep(60000);
				} catch (Exception e) {
					e.printStackTrace();
					if(socket!=null){
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						socket = null;
					}
					break;
				}
			}
		}
	}
	
	class readThread extends Thread{
		Socket socket = null;
		public readThread(Socket s){
			socket = s;
		}
		public void run(){
			while(true){
				try {
					byte[] bt = new byte[4096];
				    int tempi = (socket.getInputStream()).read(bt);
				      
				    byte[] temp_bt = new byte[tempi];
				    System.arraycopy(bt,0,temp_bt,0,tempi);
				    String s = Util.bytetostrs(temp_bt);
					if(s.equalsIgnoreCase("FE000100000000000000")){
						socket.getOutputStream().write(Util.strstobyte("FE000100000000000000"));
					}
					sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
					if(socket!=null){
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						socket = null;
					}
					break;
				}
			}
		}
	}
	public static void main(String[] args)throws Exception{
		socketServer ss = new socketServer();
		ServerSocket server = new ServerSocket(16001);
		System.out.println("server started");
		while(true){
			Socket socket = server.accept();
			System.out.println("socket client comming!");
			
			readThread readT = ss.new readThread(socket);
			readT.start();			

			sendThread sendT = ss.new sendThread(socket);
			sendT.start();
		}
	}
}