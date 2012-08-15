/*    */ package com.sensaris.common;
/*    */ 
/*    */ import com.sensaris.senslink.device.Command_Manager;
/*    */ 
/*    */ public class CommandDevice extends Command
/*    */   implements Runnable
/*    */ {
/*    */   String _op;
/*    */ 
/*    */   public CommandDevice(String name, String operation)
/*    */   {
/* 18 */     super(1, name);
/*    */ 
/* 20 */     this._op = operation;
/*    */   }
/*    */   public void run() {
/* 23 */     synchronized (this._completion_rules)
/*    */     {
/* 28 */       this._b_completed = true;
/* 29 */       this._completion_rules.notifyAll();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void exec_command(String device_name)
/*    */   {
/*    */     try {
/* 36 */       Command_Manager.command_buffer.put(this._op + " " + device_name);
/* 37 */       this._b_completed = true;
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.CommandDevice
 * JD-Core Version:    0.6.0
 */