package com.sunvote.xpadapp.db.modal;

public class BillInfo {
	
	public static final int BillType_Signin=1;
	public static final int BillType_Vote=2;
	public static final int BillType_Evaluate=3;
	public static final int BillType_Election=5;
	
	
	public int billId;//议题ID
	public int billNo;//议题编号
	public int billType;//1 签到 2表决 3 评议  5选举
	public int subType; //billType 为2,3 时： 0快速单项     1单项标题    2单项内容    3多项标题   4多项内容
	public String title; //投票标题
	public String memo; //投票内容
	public String billFile;//议案文件名
	public String billOptions;//评议选项
	
	public int voteResult;
	public int voteStatus;
}
