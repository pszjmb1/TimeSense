/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Buffer
/*     */ {
/*     */   private int capacity;
/*     */   private Vector elements;
/*     */   private Vector queue;
/*     */ 
/*     */   public Buffer(int capacity)
/*     */   {
/*  45 */     this.capacity = capacity;
/*  46 */     this.elements = new Vector(capacity);
/*  47 */     this.queue = new Vector();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  55 */     return this.elements.size();
/*     */   }
/*     */ 
/*     */   public synchronized void put(Object element)
/*     */   {
/*  74 */     if (this.elements.size() == this.capacity) {
/*  75 */       Thread caller = Thread.currentThread();
/*  76 */       this.queue.addElement(caller);
/*  77 */       while ((size() == this.capacity) || (caller != this.queue.firstElement())) {
/*     */         try
/*     */         {
/*  80 */           wait();
/*     */         }
/*     */         catch (InterruptedException e) {
/*  83 */           Log.log(0, "Buf " + e);
/*     */         }
/*     */       }
/*  86 */       this.queue.removeElement(caller);
/*     */     }
/*  88 */     this.elements.addElement(element);
/*  89 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized Object get()
/*     */   {
/*  99 */     Thread caller = Thread.currentThread();
/*     */ 
/* 101 */     if (this.elements.isEmpty()) {
/* 102 */       this.queue.addElement(caller);
/* 103 */       while ((this.elements.isEmpty()) || (caller != this.queue.firstElement())) {
/*     */         try
/*     */         {
/* 106 */           wait();
/*     */         }
/*     */         catch (InterruptedException e) {
/* 109 */           Log.log(0, "Buf " + e);
/*     */         }
/*     */       }
/* 112 */       this.queue.removeElement(caller);
/*     */     }
/* 114 */     Object element = this.elements.firstElement();
/* 115 */     this.elements.removeElement(element);
/* 116 */     notifyAll();
/* 117 */     return element;
/*     */   }
/*     */ 
/*     */   public boolean is_full()
/*     */   {
/* 127 */     return this.capacity <= this.elements.size();
/*     */   }
/*     */ 
/*     */   public synchronized Object get_no_remove()
/*     */   {
/* 142 */     Thread caller = Thread.currentThread();
/* 143 */     if (this.elements.isEmpty()) {
/* 144 */       this.queue.addElement(caller);
/* 145 */       while ((this.elements.isEmpty()) || (caller != this.queue.firstElement())) {
/*     */         try
/*     */         {
/* 148 */           wait();
/*     */         } catch (InterruptedException e) {
/* 150 */           Log.log(0, "Buf " + e);
/*     */         }
/*     */       }
/* 153 */       this.queue.removeElement(caller);
/*     */     }
/* 155 */     Object element = this.elements.firstElement();
/* 156 */     return element;
/*     */   }
/*     */ 
/*     */   public synchronized void remove(Object element)
/*     */   {
/* 165 */     this.elements.removeElement(element);
/* 166 */     notifyAll();
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Buffer
 * JD-Core Version:    0.6.0
 */