/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.UnknownHostException;
/*     */ 
/*     */ public class SocketClient
/*     */ {
/*  19 */   private Socket socket = null;
/*  20 */   private final int port = 1337;
/*     */ 
/*  22 */   private final int timeout = 5000;
/*     */ 
/*  25 */   BufferedReader inStream = null;
/*  26 */   PrintWriter outStream = null;
/*     */   StringBuilder sb;
/*     */ 
/*     */   public void open(String address)
/*     */     throws UnknownHostException, IOException
/*     */   {
/*  36 */     getClass(); getClass(); open(address, 1337, 5000);
/*     */   }
/*     */ 
/*     */   public void open(String address, int port)
/*     */     throws UnknownHostException, IOException
/*     */   {
/*  47 */     getClass(); open(address, port, 5000);
/*     */   }
/*     */ 
/*     */   public void open(String address, int port, int timeout)
/*     */     throws UnknownHostException, IOException
/*     */   {
/*  62 */     init(address, port, timeout);
/*     */   }
/*     */ 
/*     */   private void init(String address, int port, int timeout) throws UnknownHostException, IOException
/*     */   {
/*  67 */     this.socket = new Socket(InetAddress.getByName(address), port);
/*  68 */     this.socket.setSoTimeout(timeout);
/*  69 */     this.sb = new StringBuilder();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  76 */     this.socket.close();
/*     */   }
/*     */ 
/*     */   public String send(String command)
/*     */     throws IOException, SocketTimeoutException
/*     */   {
/*  89 */     if (null == this.outStream) {
/*  90 */       this.outStream = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
/*     */     }
/*  92 */     this.outStream.println(command);
/*     */ 
/*  94 */     this.outStream.flush();
/*     */ 
/* 108 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.SocketClient
 * JD-Core Version:    0.6.0
 */