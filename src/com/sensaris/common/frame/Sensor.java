/*     */ package com.sensaris.common.frame;
/*     */ 
/*     */ public class Sensor
/*     */ {
/*     */   protected String _type;
/*     */   protected String _frame_type;
/*     */   protected String _unit_name;
/*     */   protected Class _data_type;
/*     */   protected Object _max_value;
/*     */   protected Object _min_value;
/*     */   protected Object _resolution;
/*     */   protected String _japetFrameValueKeys;
/*     */   protected String _db_type;
/*     */ 
/*     */   public Sensor(String frame_type, String type)
/*     */   {
/*  56 */     this._frame_type = frame_type;
/*  57 */     this._type = type;
/*     */   }
/*     */ 
/*     */   public String getJapetFrameValueKey()
/*     */   {
/*  62 */     return this._japetFrameValueKeys;
/*     */   }
/*     */ 
/*     */   public void setJapetFrameValueKey(String japetFrameValueKeys) {
/*  66 */     this._japetFrameValueKeys = japetFrameValueKeys;
/*     */   }
/*     */ 
/*     */   public void setUnitName(String val) {
/*  70 */     this._unit_name = val;
/*     */   }
/*     */ 
/*     */   public Object getUnitName() {
/*  74 */     return this._unit_name;
/*     */   }
/*     */ 
/*     */   public void setDataType(Class val) {
/*  78 */     this._data_type = val;
/*     */   }
/*     */ 
/*     */   public Class getDataType() {
/*  82 */     return this._data_type;
/*     */   }
/*     */ 
/*     */   public void setMaximumRange(Object val) {
/*  86 */     this._max_value = val;
/*     */   }
/*     */ 
/*     */   public Object getMaximumRange() {
/*  90 */     return this._max_value;
/*     */   }
/*     */ 
/*     */   public void setMinimumRange(Object val) {
/*  94 */     this._min_value = val;
/*     */   }
/*     */ 
/*     */   public Object getMinimumRange() {
/*  98 */     return this._min_value;
/*     */   }
/*     */ 
/*     */   public void setResolution(Object val) {
/* 102 */     this._resolution = val;
/*     */   }
/*     */ 
/*     */   public Object getResolution() {
/* 106 */     return this._resolution;
/*     */   }
/*     */ 
/*     */   public void setFrameType(String val)
/*     */   {
/* 111 */     this._frame_type = val;
/*     */   }
/*     */ 
/*     */   public String getFrameType() {
/* 115 */     return this._frame_type;
/*     */   }
/*     */ 
/*     */   public void setDbType(String val)
/*     */   {
/* 124 */     this._db_type = val;
/*     */   }
/*     */ 
/*     */   public String getDbType() {
/* 128 */     return this._db_type;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.frame.Sensor
 * JD-Core Version:    0.6.0
 */