package com.sunvote.xpadcomm;

public interface XPadApiInterface {

	//事件类型

	public enum BaseEventType{
		BaseEventWorkMode,
		BaseEventBaseInfo

	}

	//状态类型
	/*
	 * 下送类
	 * */
	public static final int CMD_BASE_STATUS_CHANGE = 0x71;//基础信标变化
	public static final int CMD_VOTE_STATUS_CHANGE = 0x72;//投票信标变化



	public static final int CMD_KEY_MANAGE = 0x30;	//表决器管理类指令,自定义透传指令
	/*
     *上传类
     */
	public static final int CMD_VOTE_RESULT_SEND_RESPONSE = 0xF3;//投票结果上传应答

	public static final int CMD_VOTE_SEND_SUCCESS_RESPONSE = 0x73;//传输入成功通知

	/*
	 * 状态类
	 */
	public static final int CMD_CHECK_BASE_STATUS_RESPONSE = 0xF0;//查询和设置各类应答

	//下载多包
	public static final int CMD_MULTI_PCKAGE_DOWNLOAD = 0x40;//


	public static final int CMD_FIRM_UPDATE_RESPONSE = 0xF8;//固件升级应答
	/*******************************************************************************
	 ******************************************************************************/


	public static final int	VoteType_Stop = 0 ;
	public static final int	VoteType_Signin = 1;
	public static final int VoteType_Vote = 2;
	public static final int VoteType_Evaluate = 3;
	public static final int VoteType_KeypadTest = 9;
	public static final int VoteType_Choice = 10;
	public static final int VoteType_BatchVote = 20;
	public static final int VoteType_BatchSelect = 22;


	public static final int AnsType_Single = 1;//单值
	public static final int AnsType_Select = 2;//选择
	public static final int AnsType_BatchSingle = 21;//批次单值结果
	public static final int AnsType_SelectOther = 26;//


	/*
	 * 状态类
	 */
	public void getWorkMode();//查询模块工作模式
	public void setWorkMode(int iMode);//设置模块工作模式

	public void getBaseStatus();//查询基础信标
	public void getVoteStatus();//查询投票信标

	public void getKeypadParam(); //查询键盘参数
	public void setKeypadParam(int keyId,byte[] KEYSN ); //设置键盘参数


	public void checkOnLine(int volt,int keyinStatus);//查询在线状态

	public void execKeypadMatch(int iMode,int channal);//执行配对
	public void configMode();//进入配置模式
	/*
	 * 上传类
	 */
	public void submitVote(int ansType, String info);//ID模式结果
	public void submitVoteBySn(byte[] keySn,String info); //SN模式结果
	public void submitVoteAllOK();
	public void cancelSubmitVoteAllOK();
	public void submitSelectOther(String info);

	/*
	 * 固件升级
	 */
	public void startFirmUpdate(byte[] fileBuffer);



	/*
	 * 透传
	 */
	public void sendCmdData(int cmdId, byte[] data);

	/*
	 * 设置串口监听
	 */
	public void setComListener(ComListener cl);



	public class BaseInfo{
		public int baseId;//基站编号
		public int idMode;//基站识别模式，1编号、2序列号
		public int confId;//会议资料UID编号，1-65535，高位字节在前
		public int billId;//议题编号
		public int authCode;//授权号，2字节，高位在前，0-0xFFFF
		public int login;//登录申请模式（后台签到模式），是否需要IC卡、登录码（
		public int report;//表决器报告状态模式和指定语言
		public int offTime;//自动关机时间
		public int attrib;//表决器特性：背光模式+蜂鸣器模式等等
		public String baseName; //基站名称，最多12字节
	}

	public class ModelInfo{
		public int mode;//模块当前主从模式      1 基站模式（主）  2 键盘模式（从）
		public int hModel;//硬件型号，数字
		public String sVer;//固件版本，3位数字，例如 0.1.0

	}

	public class KeypadInfo{
		public int cmd1;//8:match 9:config
		public int ok;
		public int chan;
		public int keyId;//键盘编号，高位在前
		public String keySn;//6字节键盘序列号
		public String matchCode;//4字节配对码
	}

	public class OnLineInfo{
		public int onLine; //键盘是否在线 1 在线 2 离线
		public int idMode;//基站的识别模式
		public int chan; //当前频点号
		public int rssi; //接收到基站的信号强度RSSI值，负数，越小表示信号越大
		public int tx; //1表示刚才1秒内有提交数据过  0 表示没有
		public int rx; //1 表示刚才1秒内收到过基站的投票指令（特指投票中） 0 表示没有
		public int baseId;
		public int keyId;
		public String keySn;
		public int comError=0;

	}



	public class SigninInfo{
		public int signinMode;	//1 按键签到模式, 2 Uid签到 , 3 SN
		public int showUserInfo;
	}

	public class VoteInfo{

		public int baseId;//[1]基站编号
		public int nowT;//[2,3]
		public int dataPos;//[4]
		public int mode;// [5] 表决模式
		public int mode1_msgType;// [6] mode=0时，就是MSGTYPE 2为投票结果显示

		public int mode2_modify;  // [7]投票参数  0不可修改      1可修改
		public int mode3_secret;//[8]投票参数	保密
		public int mode4;//[9]投票参数
		public int mode5;//
		public int mode6;
		public int mode7;
		public int voteid; //[11] >1 指定议案编号   0，不指定，相当于快速表决
		public int file;  //[12]是否允许切换标题和内容  0 不允许 1允许
		public int init;	//[13]初始是标题还是内容 0 标题 1内容

		public int less;//[11]


		public ResultInfo resultInfo = new ResultInfo();

		public class ResultInfo{//结果
			public int resultType;//[7]   结果显示类型 。 如  0为 签到 1为表决
			public int bits;// [8]
			public int num0;//[9-10]
			public int num1;//[11,12]
			public int num2;//[13,14]
			public int num3;//[15,16]
			public int num4;//[17,18]
			public int num5;//[19,20]
			public int num6;//[21,22]
		}

		//Deprecated blow
		public int voteMode;// 表决模式
		public int modifyMode;//修改模式
		public int secretMode;//保密模式
		public int quickTitleMode;//在Mode1=3的时候，是使用哪种标准
		public String title; //在Mode1=3的时候，是表决项目的标题，14字节字符，可支持7个汉字
		public int voteNo;//议案号（0-255），0表示不启用键盘记录表决结果，1到255表示启用键盘记录表决结果（但具体最大值看键盘）
	}



	public class BatchVoteInfo{
		public int start; //6-7    开始项目编号，2字节，高位在前，从1开始，小于项目表最大项目数
		public int end;	//8-9 结束项目编号，2字节，高位在前，一般比START值大，和START值相同时候表示仅选一项目
		public int mode1; //10特殊模式，脱机调研模式或快速批次投票
		public int less; //迫先模式 0 不迫选   1 迫选，所有议题必需做出选择     2 调研模式
		public int secret; //
		public int fixballot;//是否带票数限定，，对批次表决、批次评议、批次自定义评议有效       0 不限定    1 带票数限定
		public byte[] limit = new byte[8];   //如果是票数限定，8字节依次是每个等级限定的票数
		public int rule;  //无项目名称表的批次表决、批次评议、批次评分时候使用规则     批次表决时候，1=3键表决，2=2键表决     批次评议时候，是评议规则表的规则号，从1开始
		//批次评分时候，是评分规则表中的规则好，从1开始
		public int modify;   //0 不允许修改  1 允许修改，


	}

	/*
	 * 选举投票信标
	 */
	public class BatchSelectInfo{
		public int type ;//  人机界面模式，不同型号可能不同     0缺省，使用打勾模式     1 使用赞成、反对模式      2 使用累计投票制
		public int random; //
		public int select;//要选出的人数（赞成票数）
		public int other; //是否允许另选他人  0 不允许      1 允许（在名称表中选） 2 允许，另外输入
		public int less;   //迫选，是否允许少选       0 允许        1 不允许，必须要选出SELECT个人员            2 允许少选，但限定最少必选人数（InfoWyse M52Li定制），参数在第16字节
		public int secrecy;
		public int modify;
		public int start;
		public int end;
		public int equityMode;
		public int minSelect;


	}


	/*
	 * 评议投票信标
	 */

	public class EvaluateInfo{
		public int mode1;
		public int modify;
		public int secrecy;
		public int mode4;
	}

	public class KeypadTestInfo{
		public int mode1;
		public int mode2;
		public int mode3;
	}

	public class CmdDataInfo{
		public int cmd;
		public byte[] data;
	}

}
