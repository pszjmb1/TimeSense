/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class DataBlock
/*     */ {
/*  50 */   private LinkedList<Short> leadI = null;
/*  51 */   private LinkedList<Short> leadII = null;
/*  52 */   private LinkedList<Short> leadIII = null;
/*  53 */   private LinkedList<Short> leadV1 = null;
/*  54 */   private LinkedList<Short> leadV2 = null;
/*  55 */   private LinkedList<Short> leadV3 = null;
/*  56 */   private LinkedList<Short> leadV4 = null;
/*  57 */   private LinkedList<Short> leadV5 = null;
/*  58 */   private LinkedList<Short> leadV6 = null;
/*  59 */   private LinkedList<Short> leadAvR = null;
/*  60 */   private LinkedList<Short> leadAvL = null;
/*  61 */   private LinkedList<Short> leadAvF = null;
/*  62 */   private boolean pacerImpulse = false;
/*  63 */   private boolean packetType = false;
/*  64 */   private boolean contactL = true;
/*  65 */   private boolean contactR = true;
/*  66 */   private boolean contactN = true;
/*  67 */   private boolean contactF = true;
/*  68 */   private boolean contactV1 = true;
/*  69 */   private boolean contactV2 = true;
/*  70 */   private boolean contactV3 = true;
/*  71 */   private boolean contactV4 = true;
/*  72 */   private boolean contactV5 = true;
/*  73 */   private boolean contactV6 = true;
/*  74 */   private short sampleRate = 0;
/*  75 */   private Date measured = null;
/*  76 */   private Date transmit = null;
/*     */ 
/*     */   public DataBlock(short sampleRate) {
/*  79 */     this.leadI = new LinkedList();
/*  80 */     this.leadII = new LinkedList();
/*  81 */     this.leadIII = new LinkedList();
/*  82 */     this.leadV1 = new LinkedList();
/*  83 */     this.leadV2 = new LinkedList();
/*  84 */     this.leadV3 = new LinkedList();
/*  85 */     this.leadV4 = new LinkedList();
/*  86 */     this.leadV5 = new LinkedList();
/*  87 */     this.leadV6 = new LinkedList();
/*  88 */     this.leadAvR = new LinkedList();
/*  89 */     this.leadAvL = new LinkedList();
/*  90 */     this.leadAvF = new LinkedList();
/*  91 */     this.sampleRate = sampleRate;
/*  92 */     this.measured = new Date();
/*  93 */     this.transmit = new Date();
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  97 */     Short value1 = null;
/*  98 */     Short value2 = null;
/*  99 */     value2 = (Short)this.leadI.removeLast();
/* 100 */     value1 = (Short)this.leadI.removeLast();
/* 101 */     this.leadI.clear();
/* 102 */     this.leadI.add(value1);
/* 103 */     this.leadI.add(value2);
/* 104 */     value2 = (Short)this.leadII.removeLast();
/* 105 */     value1 = (Short)this.leadII.removeLast();
/* 106 */     this.leadII.clear();
/* 107 */     this.leadII.add(value1);
/* 108 */     this.leadII.add(value2);
/* 109 */     value2 = (Short)this.leadIII.removeLast();
/* 110 */     value1 = (Short)this.leadIII.removeLast();
/* 111 */     this.leadIII.clear();
/* 112 */     this.leadIII.add(value1);
/* 113 */     this.leadIII.add(value2);
/* 114 */     value2 = (Short)this.leadV1.removeLast();
/* 115 */     value1 = (Short)this.leadV1.removeLast();
/* 116 */     this.leadV1.clear();
/* 117 */     this.leadV1.add(value1);
/* 118 */     this.leadV1.add(value2);
/* 119 */     value2 = (Short)this.leadV2.removeLast();
/* 120 */     value1 = (Short)this.leadV2.removeLast();
/* 121 */     this.leadV2.clear();
/* 122 */     this.leadV2.add(value1);
/* 123 */     this.leadV2.add(value2);
/* 124 */     value2 = (Short)this.leadV3.removeLast();
/* 125 */     value1 = (Short)this.leadV3.removeLast();
/* 126 */     this.leadV3.clear();
/* 127 */     this.leadV3.add(value1);
/* 128 */     this.leadV3.add(value2);
/* 129 */     value2 = (Short)this.leadV4.removeLast();
/* 130 */     value1 = (Short)this.leadV4.removeLast();
/* 131 */     this.leadV4.clear();
/* 132 */     this.leadV4.add(value1);
/* 133 */     this.leadV4.add(value2);
/* 134 */     value2 = (Short)this.leadV5.removeLast();
/* 135 */     value1 = (Short)this.leadV5.removeLast();
/* 136 */     this.leadV5.clear();
/* 137 */     this.leadV5.add(value1);
/* 138 */     this.leadV5.add(value2);
/* 139 */     value2 = (Short)this.leadV6.removeLast();
/* 140 */     value1 = (Short)this.leadV6.removeLast();
/* 141 */     this.leadV6.clear();
/* 142 */     this.leadV6.add(value1);
/* 143 */     this.leadV6.add(value2);
/* 144 */     value2 = (Short)this.leadAvR.removeLast();
/* 145 */     value1 = (Short)this.leadAvR.removeLast();
/* 146 */     this.leadAvR.clear();
/* 147 */     this.leadAvR.add(value1);
/* 148 */     this.leadAvR.add(value2);
/* 149 */     value2 = (Short)this.leadAvL.removeLast();
/* 150 */     value1 = (Short)this.leadAvL.removeLast();
/* 151 */     this.leadAvL.clear();
/* 152 */     this.leadAvL.add(value1);
/* 153 */     this.leadAvL.add(value2);
/* 154 */     value2 = (Short)this.leadAvF.removeLast();
/* 155 */     value1 = (Short)this.leadAvF.removeLast();
/* 156 */     this.leadAvF.clear();
/* 157 */     this.leadAvF.add(value1);
/* 158 */     this.leadAvF.add(value2);
/* 159 */     this.pacerImpulse = false;
/* 160 */     this.packetType = false;
/* 161 */     this.contactL = true;
/* 162 */     this.contactR = true;
/* 163 */     this.contactN = true;
/* 164 */     this.contactF = true;
/* 165 */     this.contactV1 = true;
/* 166 */     this.contactV2 = true;
/* 167 */     this.contactV3 = true;
/* 168 */     this.contactV4 = true;
/* 169 */     this.contactV5 = true;
/* 170 */     this.contactV6 = true;
/*     */   }
/*     */ 
/*     */   public void updateMonitor(char monitorValues) {
/* 174 */     if ((monitorValues & 0x1) == '\001') {
/* 175 */       this.contactV1 = false;
/*     */     }
/* 177 */     if ((monitorValues >> '\001' & 0x1) == 1) {
/* 178 */       this.contactV2 = false;
/*     */     }
/* 180 */     if ((monitorValues >> '\002' & 0x1) == 1) {
/* 181 */       this.contactV3 = false;
/*     */     }
/* 183 */     if ((monitorValues >> '\003' & 0x1) == 1) {
/* 184 */       this.contactV4 = false;
/*     */     }
/* 186 */     if ((monitorValues >> '\004' & 0x1) == 1) {
/* 187 */       this.contactV5 = false;
/*     */     }
/* 189 */     if ((monitorValues >> '\005' & 0x1) == 1) {
/* 190 */       this.contactV6 = false;
/*     */     }
/* 192 */     if ((monitorValues >> '\006' & 0x1) == 1) {
/* 193 */       this.contactN = false;
/*     */     }
/* 195 */     if ((monitorValues >> '\007' & 0x1) == 1)
/* 196 */       this.packetType = true;
/*     */     else {
/* 198 */       this.packetType = false;
/*     */     }
/* 200 */     if ((monitorValues >> '\b' & 0x1) == 1) {
/* 201 */       this.contactF = false;
/*     */     }
/* 203 */     if ((monitorValues >> '\t' & 0x1) == 1) {
/* 204 */       this.contactR = false;
/*     */     }
/* 206 */     if ((monitorValues >> '\n' & 0x1) == 1) {
/* 207 */       this.contactL = false;
/*     */     }
/* 209 */     if ((monitorValues >> '\017' & 0x1) == 1)
/* 210 */       this.pacerImpulse = true;
/*     */   }
/*     */ 
/*     */   public boolean addValues(short[] leads)
/*     */   {
/* 215 */     this.leadI.add(Short.valueOf(leads[0]));
/* 216 */     this.leadII.add(Short.valueOf(leads[1]));
/* 217 */     this.leadIII.add(Short.valueOf(leads[2]));
/* 218 */     this.leadAvL.add(Short.valueOf(leads[3]));
/* 219 */     this.leadAvF.add(Short.valueOf(leads[4]));
/* 220 */     this.leadAvR.add(Short.valueOf(leads[5]));
/* 221 */     this.leadV1.add(Short.valueOf(leads[6]));
/* 222 */     this.leadV2.add(Short.valueOf(leads[7]));
/* 223 */     this.leadV3.add(Short.valueOf(leads[8]));
/* 224 */     this.leadV4.add(Short.valueOf(leads[9]));
/* 225 */     this.leadV5.add(Short.valueOf(leads[10]));
/* 226 */     this.leadV6.add(Short.valueOf(leads[11]));
/*     */ 
/* 228 */     return this.leadI.size() == this.sampleRate + 2;
/*     */   }
/*     */ 
/*     */   public void setMeasured(Date date)
/*     */   {
/* 234 */     this.measured = date;
/*     */   }
/*     */ 
/*     */   public Date getMeasured() {
/* 238 */     return this.measured;
/*     */   }
/*     */ 
/*     */   public void setTransmit(Date date) {
/* 242 */     this.transmit = date;
/*     */   }
/*     */ 
/*     */   public Date getTransmit() {
/* 246 */     return this.transmit;
/*     */   }
/*     */ 
/*     */   public boolean isContactF()
/*     */   {
/* 253 */     return this.contactF;
/*     */   }
/*     */ 
/*     */   public boolean isContactL()
/*     */   {
/* 260 */     return this.contactL;
/*     */   }
/*     */ 
/*     */   public boolean isContactN()
/*     */   {
/* 267 */     return this.contactN;
/*     */   }
/*     */ 
/*     */   public boolean isContactR()
/*     */   {
/* 274 */     return this.contactR;
/*     */   }
/*     */ 
/*     */   public boolean isContactV1()
/*     */   {
/* 281 */     return this.contactV1;
/*     */   }
/*     */ 
/*     */   public boolean isContactV2()
/*     */   {
/* 288 */     return this.contactV2;
/*     */   }
/*     */ 
/*     */   public boolean isContactV3()
/*     */   {
/* 295 */     return this.contactV3;
/*     */   }
/*     */ 
/*     */   public boolean isContactV4()
/*     */   {
/* 302 */     return this.contactV4;
/*     */   }
/*     */ 
/*     */   public boolean isContactV5()
/*     */   {
/* 309 */     return this.contactV5;
/*     */   }
/*     */ 
/*     */   public boolean isContactV6()
/*     */   {
/* 316 */     return this.contactV6;
/*     */   }
/*     */ 
/*     */   public short[] getLeadAvF()
/*     */   {
/* 323 */     return toArray(this.leadAvF);
/*     */   }
/*     */ 
/*     */   public short[] getLeadAvL()
/*     */   {
/* 330 */     return toArray(this.leadAvL);
/*     */   }
/*     */ 
/*     */   public short[] getLeadAvR()
/*     */   {
/* 337 */     return toArray(this.leadAvR);
/*     */   }
/*     */ 
/*     */   public short[] getLeadI()
/*     */   {
/* 344 */     return toArray(this.leadI);
/*     */   }
/*     */ 
/*     */   public short[] getLeadII()
/*     */   {
/* 351 */     return toArray(this.leadII);
/*     */   }
/*     */ 
/*     */   public short[] getLeadIII()
/*     */   {
/* 358 */     return toArray(this.leadIII);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV1()
/*     */   {
/* 365 */     return toArray(this.leadV1);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV2()
/*     */   {
/* 372 */     return toArray(this.leadV2);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV3()
/*     */   {
/* 379 */     return toArray(this.leadV3);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV4()
/*     */   {
/* 386 */     return toArray(this.leadV4);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV5()
/*     */   {
/* 393 */     return toArray(this.leadV5);
/*     */   }
/*     */ 
/*     */   public short[] getLeadV6()
/*     */   {
/* 400 */     return toArray(this.leadV6);
/*     */   }
/*     */ 
/*     */   public boolean isPacerImpulse()
/*     */   {
/* 407 */     return this.pacerImpulse;
/*     */   }
/*     */ 
/*     */   public boolean isPacketType()
/*     */   {
/* 414 */     return this.packetType;
/*     */   }
/*     */ 
/*     */   private short[] toArray(LinkedList<Short> values) {
/* 418 */     short[] output = new short[500];
/*     */ 
/* 420 */     for (int i = 0; i < values.size() / 2 - 1; i++) {
/* 421 */       output[(5 * i)] = ((Short)values.get(i * 2)).shortValue();
/* 422 */       output[(5 * i + 1)] = (short)(int)(0.6D * ((Short)values.get(i * 2)).shortValue() + 0.4D * ((Short)values.get(2 * i + 1)).shortValue());
/* 423 */       output[(5 * i + 2)] = (short)(int)(0.2D * ((Short)values.get(2 * i)).shortValue() + 0.8D * ((Short)values.get(2 * i + 1)).shortValue());
/* 424 */       output[(5 * i + 3)] = (short)(int)(0.8D * ((Short)values.get(i * 2 + 1)).shortValue() + 0.2D * ((Short)values.get(2 * i + 2)).shortValue());
/* 425 */       output[(5 * i + 4)] = (short)(int)(0.4D * ((Short)values.get(2 * i + 1)).shortValue() + 0.6D * ((Short)values.get(2 * i + 2)).shortValue());
/*     */     }
/* 427 */     return output;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.DataBlock
 * JD-Core Version:    0.6.0
 */