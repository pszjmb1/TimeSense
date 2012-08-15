/*    */ package com.sensaris.common.frame;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class Frame
/*    */ {
/*    */   public static HashMap<String, Frame> frameList;
/*    */   protected String _type;
/*    */   protected int _frameItemNb;
/*    */   protected int _frameLen;
/*    */   protected ArrayList<Sensor> _sensorList;
/*    */ 
/*    */   static void addFrame(String type, Frame frame)
/*    */   {
/* 19 */     if (null == frameList)
/* 20 */       frameList = new HashMap();
/* 21 */     frameList.put(type, frame);
/*    */   }
/*    */ 
/*    */   public static void addFrame(Frame frame) {
/* 25 */     if (null == frameList)
/* 26 */       frameList = new HashMap();
/* 27 */     frameList.put(frame._type, frame);
/*    */   }
/*    */ 
/*    */   public static Frame getFrame(String type) {
/* 31 */     if (null != frameList)
/* 32 */       return (Frame)frameList.get(type);
/* 33 */     return null;
/*    */   }
/*    */ 
/*    */   public Frame(String type)
/*    */   {
/* 50 */     this._type = type;
/* 51 */     this._sensorList = new ArrayList();
/* 52 */     addFrame(this);
/*    */   }
/*    */ 
/*    */   public void addSensor(Sensor sensor)
/*    */   {
/* 57 */     this._sensorList.add(sensor);
/*    */   }
/*    */ 
/*    */   public Sensor getSensor(String type)
/*    */   {
/* 64 */     for (Sensor sensor : this._sensorList)
/*    */     {
/* 66 */       if (sensor._type.equals(type))
/* 67 */         return sensor;
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ 
/*    */   public ArrayList<Sensor> listSensor() {
/* 73 */     return this._sensorList;
/*    */   }
/*    */ 
/*    */   public int getFrameLen()
/*    */   {
/* 79 */     return this._frameLen;
/*    */   }
/*    */ 
/*    */   public void setFrameLen(int frameLen) {
/* 83 */     this._frameLen = frameLen;
/*    */   }
/*    */ 
/*    */   public int getFrameItemNb()
/*    */   {
/* 92 */     return this._frameItemNb;
/*    */   }
/*    */ 
/*    */   public void setFrameItemNb(int frameItemNb) {
/* 96 */     this._frameItemNb = frameItemNb;
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.frame.Frame
 * JD-Core Version:    0.6.0
 */