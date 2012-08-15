/*    */ package com.sensaris.common;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class Log
/*    */ {
/*    */   static String _header;
/* 28 */   public static boolean _trace = false;
/* 29 */   public static int _level = 1;
/*    */ 
/*    */   public static void init(String header)
/*    */   {
/* 37 */     _header = header;
/*    */   }
/*    */ 
/*    */   public static void level(int level) {
/* 41 */     _level = level;
/*    */   }
/*    */ 
/*    */   public static void msg(int level, String log_msg)
/*    */   {
/* 51 */     if (_trace)
/*    */     {
/* 53 */       if (level <= _level)
/* 54 */         System.out.println(Utils.iso_date(System.currentTimeMillis()) + " " + log_msg);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void log(int level, String log_msg)
/*    */   {
/* 64 */     if (_trace)
/* 65 */       msg(level, Utils.iso_date(System.currentTimeMillis()) + " " + log_msg);
/*    */   }
/*    */ 
/*    */   public static void msg(String log_msg)
/*    */   {
/* 74 */     if (_trace)
/* 75 */       System.out.println(Utils.iso_date(System.currentTimeMillis()) + " " + log_msg);
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Log
 * JD-Core Version:    0.6.0
 */