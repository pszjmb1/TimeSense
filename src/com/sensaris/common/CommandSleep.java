/*    */ package com.sensaris.common;
/*    */ 
/*    */ public class CommandSleep extends Command
/*    */   implements Runnable
/*    */ {
/*    */   long _duration_ms;
/*    */ 
/*    */   public CommandSleep(String name, long duration_ms)
/*    */   {
/* 19 */     super(1, name);
/* 20 */     this._duration_ms = duration_ms;
/*    */   }
/*    */ 
/*    */   public void run() {
/* 24 */     synchronized (this._completion_rules)
/*    */     {
/*    */       try
/*    */       {
/* 28 */         wait(this._duration_ms); } catch (Exception e) {
/*    */       }
/* 30 */       this._b_completed = true;
/* 31 */       this._completion_rules.notifyAll();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void exec_command(Object obj)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       Thread.currentThread(); Thread.sleep(this._duration_ms); } catch (Exception e) {
/*    */     }
/* 41 */     this._b_completed = true;
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.CommandSleep
 * JD-Core Version:    0.6.0
 */