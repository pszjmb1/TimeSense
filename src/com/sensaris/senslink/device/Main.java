/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Buffer;
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.DataSender;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Request_Listener;
/*     */ import com.sensaris.common.SensLinkServer_MHealth_cnx;
/*     */ import com.sensaris.senslink.SensLinkFrameDefSensPod;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.Socket;
/*     */ 
/*     */ public class Main
/*     */ {
/*     */   static final String server_name = "SensCore";
/*     */   static final String version = "2.1.45";
/* 107 */   public static String config_file = "SensCore.conf";
/*     */ 
/* 109 */   static String SensLink_id = Long.toString(System.currentTimeMillis());
/*     */ 
/* 111 */   static String SL_Server_address = "sensaris.com";
/*     */ 
/* 113 */   static int SL_Server_port = 32381;
/*     */ 
/* 115 */   static int first_listen_port = 32440;
/* 116 */   static int range_listen_port = 10;
/*     */   static String SensLink_port_in;
/*     */   static String SensLink_port_out;
/*     */   static Socket listen_cmd_socket;
/*     */   static Command_Manager command_manager;
/*     */   static DataSender data_sender;
/* 133 */   static boolean debug_in = false;
/* 134 */   static boolean debug_out = false;
/*     */ 
/* 136 */   static boolean forward_db = true;
/* 137 */   static boolean access_SLS = true;
/*     */   static String SensLink_port;
/* 141 */   static int frame_max_size = 100;
/*     */ 
/* 143 */   public static boolean b_SensLinkServer_mhealth = false;
/* 144 */   public static String SensLinkServer_MHealth_address = null;
/* 145 */   public static int SensLinkServer_MHealth_port = 32300;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 153 */     System.out.println("SensCore");
/* 154 */     System.out.println("2.1.45");
/* 155 */     init_params(args);
/* 156 */     init_config(config_file);
/*     */ 
/* 158 */     SensLinkFrameDefSensPod.init();
/*     */ 
/* 161 */     BT_Manager bt = BT_Manager.get_bt_manager_ref();
/* 162 */     if (null == bt)
/*     */     {
/* 164 */       Log.log(0, "Error -- cannot access local bluetooth - exit");
/* 165 */       return;
/*     */     }
/* 167 */     Command.init();
/*     */ 
/* 169 */     Log._trace = true;
/*     */ 
/* 172 */     command_manager = Command_Manager.get_command_manager_ref();
/*     */ 
/* 175 */     Command_Manager.command_buffer.put("search");
/*     */ 
/* 177 */     b_SensLinkServer_mhealth = null != SensLinkServer_MHealth_address;
/*     */ 
/* 182 */     Request_Listener reqlst = new Request_Listener(null, first_listen_port, range_listen_port);
/* 183 */     reqlst.start();
/*     */ 
/* 185 */     SensLinkServer_MHealth_cnx.instance();
/*     */ 
/* 187 */     while (!command_manager.stop_SensLink) {
/*     */       try {
/* 189 */         Thread.sleep(2000L);
/*     */       } catch (InterruptedException e1) {
/* 191 */         Log.log(1, "Error -- cannot wait");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 273 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   private static void send_connect() {
/* 277 */     send_status("1");
/*     */   }
/*     */   private static void send_disconnect() {
/* 280 */     send_status("0");
/*     */   }
/*     */ 
/*     */   private static void send_status(String status)
/*     */   {
/*     */   }
/*     */ 
/*     */   private static void SL_Server_get_port()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 295 */       Socket negotiation_socket = new Socket(SL_Server_address, SL_Server_port);
/*     */       try
/*     */       {
/* 298 */         OutputStreamWriter out = new OutputStreamWriter(negotiation_socket.getOutputStream());
/* 299 */         out.write(SensLink_id + "\n");
/* 300 */         out.flush();
/* 301 */         InputStreamReader in = new InputStreamReader(negotiation_socket.getInputStream());
/* 302 */         char[] msg = new char[11];
/* 303 */         in.read(msg);
/* 304 */         String[] socket_params = String.valueOf(msg).split(":");
/*     */         try
/*     */         {
/* 307 */           if ((socket_params.length == 2) && (!socket_params[0].equalsIgnoreCase("0")) && (!socket_params[1].equalsIgnoreCase("0")))
/*     */           {
/* 310 */             SensLink_port_in = socket_params[1];
/* 311 */             SensLink_port_out = socket_params[0];
/*     */           } else {
/* 313 */             throw new IOException("SL_Server_get_port: check com_port failed");
/*     */           }
/*     */         }
/*     */         catch (IOException e) {
/* 317 */           System.out.println("Error -- " + e.getMessage());
/* 318 */           throw e;
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/* 322 */         System.out.println("Error -- SL_Server_get_port: get com_port failed");
/* 323 */         negotiation_socket.close();
/* 324 */         throw e;
/*     */       }
/* 326 */       negotiation_socket.close();
/*     */     }
/*     */     catch (IOException e) {
/* 329 */       System.out.println("Error -- SL_Server_get_port: open failed");
/* 330 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void SL_Server_open_port()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 341 */       listen_cmd_socket = new Socket(SL_Server_address, Integer.valueOf(SensLink_port_out).intValue());
/*     */     }
/*     */     catch (IOException e) {
/* 344 */       System.out.println("Error -- SL_Server_open_port: open failed");
/* 345 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void SDB_Writer_open_port()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 359 */       DataSender.socket = new DatagramSocket();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 363 */       System.out.println("Error -- SDB_Writer_open_port: open failed.");
/* 364 */       Log.log(0, "Error -- SDB_Writer_open_port: open failed, " + e);
/* 365 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close_socket()
/*     */   {
/*     */     try
/*     */     {
/* 374 */       Log.log(2, "Socket closing");
/* 375 */       send_disconnect();
/*     */ 
/* 378 */       listen_cmd_socket.close();
/*     */ 
/* 380 */       DataSender.socket.close();
/*     */     } catch (IOException e) {
/* 382 */       Log.log(1, "--Error closing socket \nwith message: \n" + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void stop_SL()
/*     */   {
/* 393 */     command_manager.bt_Manager.disconnect_all_devices();
/* 394 */     send_disconnect();
/*     */     try {
/* 396 */       Thread.sleep(100L);
/*     */     }
/*     */     catch (InterruptedException e) {
/* 399 */       e.printStackTrace();
/*     */     }
/* 401 */     command_manager.stop_SensLink = true;
/* 402 */     data_sender.running_send = false;
/*     */   }
/*     */ 
/*     */   private static void init_params(String[] args) {
/* 406 */     int size = args.length;
/* 407 */     for (int i = 0; i < size; i++)
/* 408 */       if (args[i].equals("-debug")) {
/* 409 */         if (size - (i + 1) > 0) {
/* 410 */           if (args[(i + 1)].equals("in")) {
/* 411 */             debug_in = true;
/* 412 */           } else if (args[(i + 1)].equals("out")) {
/* 413 */             debug_out = true;
/*     */           } else {
/* 415 */             debug_in = true;
/* 416 */             debug_out = true;
/*     */           }
/*     */         } else {
/* 419 */           debug_in = true;
/* 420 */           debug_out = true;
/*     */         }
/* 422 */       } else if (args[i].equals("-file")) {
/* 423 */         config_file = args[(i + 1)];
/*     */       }
/* 426 */       else if (args[i].equals("-help")) {
/* 427 */         System.out.println(name());
/* 428 */         System.out.println(version());
/* 429 */         System.out.println("Usage: " + name() + ".jar [OPTIONS]");
/* 430 */         System.out.println("with OPTIONS are some of these (separated by space):");
/* 431 */         System.out.println("-help\t\tthis help menu");
/* 432 */         System.out.println("-file\t\tspecify a config file (default config file is " + config_file + ")");
/* 433 */         System.out.println("");
/* 434 */         System.out.println("From Sensaris, http://sensaris.com (C) 2010");
/* 435 */         System.out.println("");
/*     */       }
/*     */   }
/*     */ 
/*     */   public static String name()
/*     */   {
/* 449 */     return "SensCore";
/*     */   }
/*     */ 
/*     */   public static String version()
/*     */   {
/* 457 */     return "2.1.45";
/*     */   }
/*     */   private static void init_config(String file) {
/*     */     try {
/* 461 */       BufferedReader in = new BufferedReader(new FileReader(file));
/* 462 */       String line = in.readLine();
/* 463 */       while (line != null) {
/* 464 */         if (!line.startsWith("//")) {
/* 465 */           String[] line_array = line.split(" : ");
/* 466 */           if (line_array[0].trim().equalsIgnoreCase("SensLink_id"))
/* 467 */             SensLink_id = line_array[1].trim();
/* 468 */           else if (line_array[0].equalsIgnoreCase("SDB_Writer_address"))
/* 469 */             DataSender.SDB_Writer_address = line_array[1].trim();
/* 470 */           else if (line_array[0].equalsIgnoreCase("SDB_Writer_port")) {
/* 471 */             DataSender.SDB_Writer_port = Integer.parseInt(line_array[1].trim());
/*     */           }
/* 473 */           else if (line_array[0].equalsIgnoreCase("SL_SensLink_ListenPort")) {
/* 474 */             first_listen_port = Integer.parseInt(line_array[1].trim());
/*     */           }
/* 476 */           else if (line_array[0].equalsIgnoreCase("SensLinkServer_MHealth_address")) {
/* 477 */             SensLinkServer_MHealth_address = line_array[1].trim();
/*     */           }
/* 479 */           else if (line_array[0].equalsIgnoreCase("SensLinkServer_MHealth_port")) {
/* 480 */             SensLinkServer_MHealth_port = Integer.parseInt(line_array[1].trim());
/*     */           }
/* 482 */           else if (line_array[0].equalsIgnoreCase("SDB_Writer_MHealth_address")) {
/* 483 */             com.sensaris.common.DataSenderMhealth.SDB_Writer_MHealth_address = line_array[1].trim();
/*     */           }
/* 485 */           else if (line_array[0].equalsIgnoreCase("SDB_Writer_JAPET_address")) {
/* 486 */             com.sensaris.common.DataSenderJapet.SDB_Writer_JAPET_address = line_array[1].trim();
/*     */           }
/*     */         }
/*     */ 
/* 490 */         line = in.readLine();
/*     */       }
/*     */     } catch (FileNotFoundException e) {
/* 493 */       Log.log(0, "-- Warning config file " + file + " not found" + "\nwith message: \n" + e.getMessage());
/*     */ 
/* 496 */       Log.log(1, ">>> I use default values.");
/*     */     } catch (IOException e) {
/* 498 */       Log.log(0, "--Error reading file " + file + "\nwith message: \n" + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void exit()
/*     */   {
/* 508 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.Main
 * JD-Core Version:    0.6.0
 */