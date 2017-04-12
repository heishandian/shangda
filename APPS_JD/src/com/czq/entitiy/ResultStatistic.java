package com.czq.entitiy;

/**
 * @author apple
 * 
 */
public class ResultStatistic {
	private long id;
	private int sewageId;
	private String sewageName;
	private float waterFlow;
	private float reduceCOD;
	private float reduceNH3N;
	private float reduceP;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getSewageId() {
		return sewageId;
	}
	public void setSewageId(int sewageId) {
		this.sewageId = sewageId;
	}
	public String getSewageName() {
		return sewageName;
	}
	public void setSewageName(String sewageName) {
		this.sewageName = sewageName;
	}
	public float getWaterFlow() {
		return waterFlow;
	}
	public void setWaterFlow(float waterFlow) {
		this.waterFlow = waterFlow;
	}
	public float getReduceCOD() {
		return reduceCOD;
	}
	public void setReduceCOD(float reduceCOD) {
		this.reduceCOD = reduceCOD;
	}
	public float getReduceNH3N() {
		return reduceNH3N;
	}
	public void setReduceNH3N(float reduceNH3N) {
		this.reduceNH3N = reduceNH3N;
	}
	public float getReduceP() {
		return reduceP;
	}
	public void setReduceP(float reduceP) {
		this.reduceP = reduceP;
	}
	
}
