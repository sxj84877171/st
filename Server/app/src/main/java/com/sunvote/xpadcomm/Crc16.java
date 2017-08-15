package com.sunvote.xpadcomm;

public class Crc16 {

	private static int[] crc_ta= { //CRC余式表
			0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
			0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
	};


	//标准Crc16效验
	public static short crc16(byte[] data,int len)//uchar *ptr, uchar len
	{ 	short Crc;
		byte da;
		int i=4;

		Crc=0;
		while(len--!=0)
		{
			//da=Crc>>12; /* 暂存CRC的高四位 */
			da=   (byte)(Crc>>>12) ; /* 暂存CRC的高四位 */

			// Crc<<=4; /* CRC左移4位，相当于取CRC的低12位）*/
			Crc<<=4; /* CRC左移4位，相当于取CRC的低12位）*/

			//  Crc^=crc_ta[da^(*ptr/16)]; /* CRC的高4位和本字节的前半字节相加后查表计算CRC，  然后加上上一次CRC的余数 */
			Crc^=crc_ta[(da^ (data[i]>>>4)) & 0xf]; /* CRC的高4位和本字节的前半字节相加后查表计算CRC，  然后加上上一次CRC的余数 */

			//da=Crc>>12; /* 暂存CRC的高4位 */
			da= (byte)(Crc>>>12); /* 暂存CRC的高4位 */

			// Crc<<=4; /* CRC左移4位， 相当于CRC的低12位） */
			Crc<<=4; /* CRC左移4位， 相当于CRC的低12位） */

			// Crc^=crc_ta[da^ (*ptr&0x0f)];
			Crc^=crc_ta[ (da ^ data[i])&0x0f]; /* CRC的高4位和本字节的后半字节相加后查表计算CRC，
                                      然后再加上上一次CRC的余数 */

			// ptr++;
			i++;
		}
		return (Crc);
	}

	public static boolean crc16Check(byte[] data){

		int xda , xdapoly ;
		int i,j, xdabit ;
		xda = 0xFFFF ;
		xdapoly = 0xA001 ; // (X**16 + X**15 + X**2 + 1)
		for(i=0;i<data.length-2;i++)
		{
			xda ^= data[i] ;
			for(j=0;j<8;j++) {
				xdabit =( int )(xda & 0x01) ;
				xda >>= 1 ;
				if( xdabit==1 )
					xda ^= xdapoly ;
			}
		}

		return  data[data.length-2] == (int)(xda & 0xFF)  &&  data[data.length-1] == (int)(xda>>8) ;

	}

	public static int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
		return data&0x0FF;
	}

	public static int getUnsignedShort (short data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
		return data&0x0FFFF;
	}

	private static void printDataBuf(byte[] buf,int length, String flag){
		String tmpStr = new String() ;
		for(int i=0;i<length;i++){
			tmpStr += String.format("%x ", buf[i]);
		}
		System.out.println(flag+  ":" + tmpStr);
	}

	public static boolean checkPack(byte[] buf){
		if(buf.length>4 && getUnsignedByte(buf[0])==0xF5 && getUnsignedByte(buf[1])==0xAA && getUnsignedByte(buf[2])==0xAA ){
			return true;
		}
		return false;
	}


}
