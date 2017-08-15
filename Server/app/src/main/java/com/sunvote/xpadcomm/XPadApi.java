package com.sunvote.xpadcomm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.util.Log;

import com.sunvote.udptransfer.UDPModule;

public class XPadApi implements XPadApiInterface {
    private static String TAG = "XPadApi";
    private ComListener m_listener;
    private static XPadApi m_xpadApi = null;

    private SerialPort mSerialPort;
    private UDPModule clientSDK;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private ArrayList<VoteResultItem> sendQueen = new ArrayList<VoteResultItem>();
    private int serialNumber;
    private int allOkSerialNumber;

    private boolean isShowLog = true;
    private boolean isShowOnlineLog = false;
    // private boolean hasGetBaseInfo = false;

    private int writeFrimPageErrCnt = 0;

    // public SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    public synchronized static XPadApi getInstance() {
        if (m_xpadApi == null) {
            m_xpadApi = new XPadApi();
            m_xpadApi.init();
        }
        return m_xpadApi;
    }

    public void init() {
        try {
            allOkSerialNumber = -1;
          /*  mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();*/
            clientSDK = new UDPModule();
			mOutputStream = clientSDK.getOutputStream();
			mInputStream = clientSDK.getInputStream();

            clientSDK.enableModuleLog();

            Arrays.fill(broadcastData, (byte) 0x0);// 清空多包结果

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }


    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path = "/dev/ttyMT1";
            int baudrate = 115200;
			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public static void printDataBuf(byte[] buf, int len, String flag) {
        String tmpStr = new String();
        for (int i = 0; i < len; i++) {
            tmpStr += String.format("%x ", buf[i]);
        }
        Log.d(TAG, flag + ":" + tmpStr);
    }

    @Override
    public void setComListener(ComListener cl) {
        m_listener = cl;

    }

    private class VoteResultItem {
        public int serialNo;
        public int status;
        public int ansType; //
        public int ansCount;// 批次提交的选项数
        public int allOK;
        public byte[] data;
        public boolean sendOk;
        public int sendTimes = 0;

        public void sendData() {

            new Thread(new Runnable() {
                public void run() {

                    while (sendOk == false) {
                        Log.d(TAG, "send vote time:" + sendTimes + " serialNo:" + serialNo);
                        writeToCom(data);
                        sendTimes++;
                        if (sendTimes >= 3) {
                            m_listener.onVoteSubmitError();
                            break;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    /*
     * function:addToSendQueen params: 发送内容 功能：添加到发送队列
     */
    private void addToSendQueen(VoteResultItem item) {
        synchronized (sendQueen) {

            boolean isComb = false;// 合并
            if (item.ansType == AnsType_BatchSingle && item.allOK != 1) {
                for (int i = 0; i < sendQueen.size(); i++) {
                    VoteResultItem it = (VoteResultItem) sendQueen.get(i);
                    if (it.status != 1 && it.ansType == 21 && it.ansCount < 6) {
                        it.data[9 + it.ansCount * 3] = item.data[9];
                        it.data[10 + it.ansCount * 3] = item.data[10];
                        it.data[11 + it.ansCount * 3] = item.data[11];
                        it.ansCount++;
                        isComb = true;
                        Log.d(TAG, "addToSendQueen  isComb=true");
                        break;
                    }
                }
            }

            if (isComb == false) {
                sendQueen.add(item);
            }

            Log.d(TAG, "addToSendQueen snum=" + item.serialNo + "  size=" + sendQueen.size());
            boolean hasSend = false;
            for (int i = 0; i < sendQueen.size(); i++) {
                VoteResultItem it = (VoteResultItem) sendQueen.get(i);
                if (it.status == 1) {
                    hasSend = true;
                    break;
                }
            }

            if (hasSend == false) {
                VoteResultItem it = (VoteResultItem) sendQueen.get(0);
                it.status = 1;
                Log.d(TAG, "send Vote data");
                it.sendData();
            }

        }

    }

    /*
     * function:addToSendQueen params:流水号 功能：根据流水号删除队列已发送成功的项
     */
    private void removeSentItem(int serialNo) {
        synchronized (sendQueen) {
            for (int i = 0; i < sendQueen.size(); i++) { // 删除完成的
                VoteResultItem it = (VoteResultItem) sendQueen.get(i);
                if (it.status == 1 && it.serialNo == serialNo) {
                    it.sendOk = true;
                    sendQueen.remove(it);
                    Log.d(TAG, "removeSentItem snum=" + serialNo + "  size=" + sendQueen.size());
                    break;
                }
            }

            // 启动下一个
            for (int i = 0; i < sendQueen.size(); i++) {
                VoteResultItem it = (VoteResultItem) sendQueen.get(i);
                if (it.status == 0) {
                    it.status = 1;
                    it.sendData();
                    break;
                }
            }

            boolean isFinishedBatch = true;
            for (int i = 0; i < sendQueen.size(); i++) {
                VoteResultItem it = (VoteResultItem) sendQueen.get(i);
                if (it.serialNo == serialNo) {
                    isFinishedBatch = false;
                    break;
                }
            }
            // if(isFinishedBatch){
            // m_listener.onVoteSubmitSuccess();
            // }
        }
    }

    private void clearSentItem() {
        sendQueen.clear();
    }

    @Override
    public void getWorkMode() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x01;

        writeToCom(mBuffer);
    }

    @Override
    public void setWorkMode(int iMode) {

        byte[] mBuffer = new byte[0x1F + 4];
        // Arrays.fill(mBuffer, (byte) 0x55);
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x02;
        mBuffer[6] = (byte) iMode;

        writeToCom(mBuffer);
    }

    @Override
    public void getBaseStatus() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x03;
        writeToCom(mBuffer);
    }

    @Override
    public void getVoteStatus() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x04;
        writeToCom(mBuffer);
    }

    @Override
    public void getKeypadParam() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x05;
        writeToCom(mBuffer);
    }

    @Override
    public void setKeypadParam(int keyId, byte[] KEYSN) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x06;

        mBuffer[6] = (byte) ((keyId >> 8) & 0xFF);// keyId
        mBuffer[7] = (byte) (keyId & 0xFF);

        // mBuffer[8] =(byte) 0xFF;
        System.arraycopy(KEYSN, 0, mBuffer, 8, 6);

        byte[] parecode = new byte[4];
        Arrays.fill(parecode, (byte) 0xFF);
        System.arraycopy(KEYSN, 0, mBuffer, 14, 4);

        writeToCom(mBuffer);
    }

    @Override
    public void checkOnLine(int volt, int keyinStatus) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x07;

        mBuffer[6] = (byte) volt;
        mBuffer[7] = (byte) keyinStatus;
        writeToCom(mBuffer);
    }

    @Override
    public void execKeypadMatch(int iMode, int channal) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x08;

        mBuffer[6] = (byte) iMode;
        mBuffer[7] = (byte) channal;
        writeToCom(mBuffer);
    }

    @Override
    public void submitVote(int ansType, String info) {// byte[] ansData

        if (ansType == AnsType_Single) {// 单值
            byte[] mBuffer = new byte[0x1F + 4];
            Arrays.fill(mBuffer, (byte) 0x0);
            mBuffer[0] = (byte) 0xF5;
            mBuffer[1] = (byte) 0xAA;
            mBuffer[2] = (byte) 0xAA;
            mBuffer[3] = (byte) 0x1F;

            mBuffer[4] = 0x73;
            mBuffer[5] = (byte) serialNumber;// 流水号

            mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
            mBuffer[7] = (byte) ansType;
            int val = Integer.parseInt(info);
            mBuffer[8] = (byte) val;

            VoteResultItem it = new VoteResultItem();
            it.status = 0;
            it.ansType = ansType;
            it.ansCount = 1;
            it.serialNo = serialNumber++;
            it.data = mBuffer;
            addToSendQueen(it);
        } else if (ansType == AnsType_Select) {
            String[] ary = info.split(",");
            String val = ary[0];
            int tm = Integer.parseInt(ary[1]) / 20;

            byte[] mBuffer = new byte[0x1F + 4];
            Arrays.fill(mBuffer, (byte) 0x0);
            mBuffer[0] = (byte) 0xF5;
            mBuffer[1] = (byte) 0xAA;
            mBuffer[2] = (byte) 0xAA;
            mBuffer[3] = (byte) 0x1F;

            mBuffer[4] = 0x73;
            mBuffer[5] = (byte) serialNumber;// 流水号

            mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
            mBuffer[7] = (byte) ansType;

            int iVal = ChoiceValueToInt(val);
            mBuffer[8] = (byte) ((iVal >> 8) & 0xff);
            mBuffer[9] = (byte) (iVal & 0xff);

            mBuffer[10] = (byte) ((tm >> 8) & 0xff);
            mBuffer[11] = (byte) (tm & 0xff);

            VoteResultItem it = new VoteResultItem();
            it.status = 0;
            it.ansType = ansType;
            it.ansCount = 1;
            it.serialNo = serialNumber++;
            it.data = mBuffer;
            addToSendQueen(it);
        } else if (ansType == AnsType_BatchSingle) {

            // String[] ary= info.split(",");

            byte[] mBuffer = new byte[0x1F + 4];
            Arrays.fill(mBuffer, (byte) 0x0);
            mBuffer[0] = (byte) 0xF5;
            mBuffer[1] = (byte) 0xAA;
            mBuffer[2] = (byte) 0xAA;
            mBuffer[3] = (byte) 0x1F;

            mBuffer[4] = 0x73;
            mBuffer[5] = (byte) serialNumber;// 流水号

            mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
            mBuffer[7] = (byte) ansType;
            mBuffer[8] = 0;// ALLOK
            int pos = 9;

            String[] item = info.split(":");
            if (item.length > 1) {
                int num = Integer.parseInt(item[0]);
                int val = Integer.parseInt(item[1]);
                mBuffer[pos++] = (byte) (num >> 8);
                mBuffer[pos++] = (byte) num;
                mBuffer[pos++] = (byte) val;
            }

            VoteResultItem it = new VoteResultItem();
            it.status = 0;
            it.ansType = ansType;
            it.serialNo = serialNumber++;
            it.ansCount = 1;
            it.data = mBuffer;
            addToSendQueen(it);

        } else if (ansType == AnsType_SelectOther) {
            byte[] mBuffer = new byte[0x1F + 4];
            Arrays.fill(mBuffer, (byte) 0x0);
            mBuffer[0] = (byte) 0xF5;
            mBuffer[1] = (byte) 0xAA;
            mBuffer[2] = (byte) 0xAA;
            mBuffer[3] = (byte) 0x1F;

            mBuffer[4] = 0x73;
            mBuffer[5] = (byte) serialNumber;// 流水号

            mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
            mBuffer[7] = (byte) ansType;
            mBuffer[8] = 0;// ALLOK
            int pos = 9;

            String[] item = info.split(":");
            if (item.length > 1) {
                int num = Integer.parseInt(item[0]);
                String val = item[1];
                mBuffer[pos++] = (byte) (num >> 8);
                mBuffer[pos++] = (byte) num;

                mBuffer[pos++] = 0;// slot
                try {
                    byte[] name = val.getBytes("GB2312");

                    System.arraycopy(name, 0, mBuffer, pos, name.length > 16 ? 16 : name.length);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // 名字最多16
            }

            VoteResultItem it = new VoteResultItem();
            it.status = 0;
            it.ansType = ansType;
            it.serialNo = serialNumber++;
            it.ansCount = 1;
            it.data = mBuffer;
            addToSendQueen(it);
        }
    }

    private int ChoiceValueToInt(String val) {
        int ret = 0;
        for (int i = 0; i < val.length(); i++) {
            char cc = val.charAt(i);
            if (cc < 'I') {
                ret |= 1 << (8 + (cc - 'A'));
            } else {
                ret |= 1 << (cc - 'I');
            }

        }

        return ret;
    }
    @Override
    public void submitVoteAllOK() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x73;
        mBuffer[5] = (byte) serialNumber;// 流水号

        mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
        mBuffer[7] = (byte) AnsType_BatchSingle;
        mBuffer[8] = 1;// ALLOK

        VoteResultItem it = new VoteResultItem();
        it.status = 0;
        it.ansType = AnsType_BatchSingle;
        it.serialNo = serialNumber;
        allOkSerialNumber = serialNumber;
        it.ansCount = 1;
        it.allOK = 1;
        it.data = mBuffer;
        addToSendQueen(it);

        serialNumber++;
    }

    @Override
    public void cancelSubmitVoteAllOK() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x73;
        mBuffer[5] = (byte) serialNumber;// 流水号

        mBuffer[6] = 1;// MSGTYPE 1:ID mode, 2:SN
        mBuffer[7] = (byte) AnsType_BatchSingle;
        mBuffer[8] = 0;// ALLOK

        VoteResultItem it = new VoteResultItem();
        it.status = 0;
        it.ansType = AnsType_BatchSingle;
        it.serialNo = serialNumber;
        // allOkSerialNumber = serialNumber;
        it.ansCount = 1;
        it.allOK = 0;
        it.data = mBuffer;
        addToSendQueen(it);

        serialNumber++;
    }

    @Override
    public void submitSelectOther(String info) {
        // TODO Auto-generated method stub

    }

    @Override
    public void submitVoteBySn(byte[] keySn, String info) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x73;
        mBuffer[5] = 0x08;// 流水号

        mBuffer[6] = 2;// MSGTYPE

        mBuffer[7] = 1;// ANSTYPE

        System.arraycopy(keySn, 0, mBuffer, 8, 6);// keysn
        // System.arraycopy(ansData, 0, mBuffer, 8,
        // ansData.length<=16?ansData.length:16);
        // info???
        writeToCom(mBuffer);
    }

    @Override
    public void sendCmdData(int cmdId, byte[] data) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = (byte) 0xB0;// 0x30 表决器下载单包类指令

        mBuffer[5] = 0x0;// keyid 填 0
        mBuffer[6] = 0x0;// keyid

        mBuffer[7] = (byte) cmdId;// KCMD
        System.arraycopy(data, 0, mBuffer, 8, data.length);// keysn

        writeToCom(mBuffer);
    }

    @Override
    public void configMode() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 0x1F;

        mBuffer[4] = 0x70;
        mBuffer[5] = 0x09;

        writeToCom(mBuffer);

    }

    private byte[] firmFileBuffer;
    private int firmWritePage;
    private int firmWritePageMax;

    @Override
    public void startFirmUpdate(byte[] fileBuffer) {

        firmFileBuffer = fileBuffer;
        firmWritePage = 0;
        firmWritePageMax = firmFileBuffer.length / 32;
        if (firmWritePageMax % 32 > 0) {
            firmWritePageMax++;
        }

        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 10;

        mBuffer[4] = 0x78;
        mBuffer[5] = 0x01;

        writeToCom(mBuffer);
    }

    private void keepFirmMode() {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 10;

        mBuffer[4] = 0x78;
        mBuffer[5] = 0x02;

        writeToCom(mBuffer);
    }

    private void eraseFlash() {
        int file_len = firmFileBuffer.length;

        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 10;

        mBuffer[4] = 0x78;
        mBuffer[5] = 0x03;
        mBuffer[6] = 0;
        mBuffer[7] = (byte) (file_len / 1024);

        writeToCom(mBuffer);
    }

    private void writeFlash(int page) {
        byte[] mBuffer = new byte[38 + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 38;

        mBuffer[4] = 0x78;
        mBuffer[5] = 0x04;

        mBuffer[6] = (byte) (page / 256);
        mBuffer[7] = (byte) (page % 256);
        int pos = 0;
        for (int i = 0; i < 32; i++) {
            pos = page * 32 + i;
            if (pos == firmFileBuffer.length - 1) {
                break;
            }
            mBuffer[8 + i] = firmFileBuffer[pos];
        }

        writeToCom(mBuffer);
    }

    /*
     * status 1: success 2 : fail
     */
    private void exitFirmUpdate(int status) {
        byte[] mBuffer = new byte[0x1F + 4];
        Arrays.fill(mBuffer, (byte) 0x0);
        mBuffer[0] = (byte) 0xF5;
        mBuffer[1] = (byte) 0xAA;
        mBuffer[2] = (byte) 0xAA;
        mBuffer[3] = (byte) 10;

        mBuffer[4] = 0x78;
        mBuffer[5] = 5;
        mBuffer[6] = (byte) status;
        writeToCom(mBuffer);
    }

    private void writeToCom(byte[] data) {
        if (mOutputStream != null) {
            try {
                int crcValue = Crc16.getUnsignedShort(Crc16.crc16(data, data.length - 4 - 2));
                data[data.length - 2] = (byte) (crcValue >> 8);
                data[data.length - 1] = (byte) (crcValue);

                if (isShowLog) {
                    if (data[4] == 0x70 && data[5] == 0x07) {
                        // Log.d(TAG, "check online");
                        if (isShowOnlineLog) {
                            printDataBuf(data, data.length, "send:");
                        }
                    } else {
                        printDataBuf(data, data.length, "send:");
                    }
                }

                mOutputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            m_listener.onSendData(data, data.length);
        }
    }

    private byte[] SerDataRx = new byte[1024];
    int iSerRxN = 0;
    private byte[] comBuffer = new byte[1024];

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    Arrays.fill(comBuffer, (byte) 0x0);// 清空结果
                    if (mInputStream == null)
                        return;
                    size = mInputStream.read(comBuffer);

                    if (size > 0 && m_listener != null) {
                        int recvLen = size;
                        int rxDataLen = 0;
                        for (int i = 0; i < recvLen; i++) {
                            if (iSerRxN >= 512) {
                                Log.d(TAG, "SerDataRx out of bounds");
                                printDataBuf(SerDataRx, iSerRxN - 1, "recv err data ");
                                iSerRxN = 0;
                                break;
                            }
                            int dd = Crc16.getUnsignedByte(comBuffer[i]);

                            SerDataRx[iSerRxN] = comBuffer[i];// 先保存数据
                            switch (iSerRxN) {
                                case 0:
                                    if (dd == 0xF5) {
                                        iSerRxN++;
                                    }
                                    break;
                                case 1:
                                    if (dd == 0xAA)
                                        iSerRxN++;
                                    else {
                                        if (dd == 0xF5)
                                            iSerRxN = 1;
                                        else
                                            iSerRxN = 0;
                                    }
                                    break;
                                case 2:
                                    if (dd == 0xAA)
                                        iSerRxN++;
                                    else {
                                        if (dd == 0xF5)
                                            iSerRxN = 1;
                                        else
                                            iSerRxN = 0;
                                    }
                                    break;
                                case 3:// len
                                    if (dd > 128) // C_SERMAXN
                                        iSerRxN = 0;
                                    else {
                                        iSerRxN++;
                                    }
                                    break;
                                default:
                                    iSerRxN++;
                                    rxDataLen = SerDataRx[3] + 4;// len
                                    if (rxDataLen <= 3)
                                        break;
                                    if (iSerRxN == rxDataLen) {
                                        // 数据接收完整
                                        if (Crc16.checkPack(SerDataRx)) {
                                            if (isShowLog) {
                                                int tmp = SerDataRx[4] & 0xff;
                                                int tmp2 = SerDataRx[5];
                                                if (tmp == 0xf0 && tmp2 == 0x7) {
                                                    if (isShowOnlineLog) {
                                                        printDataBuf(SerDataRx, rxDataLen, "recv");
                                                    }
                                                } else {
                                                    printDataBuf(SerDataRx, rxDataLen, "recv");
                                                }
                                            }
                                            int crcValue = Crc16
                                                    .getUnsignedShort(Crc16.crc16(SerDataRx, rxDataLen - 4 - 2));
                                            int originCrcValue = 0;
                                            originCrcValue |= (SerDataRx[rxDataLen - 2] & 0xff) << 8;
                                            originCrcValue |= SerDataRx[rxDataLen - 1] & 0xff;

                                            if (crcValue != originCrcValue) {
                                                System.out.println("recv package crc error!\n");

                                            } else {
                                                checkComData(SerDataRx, rxDataLen);
                                            }
                                            for (int j = 0; j < SerDataRx.length; j++) {
                                                SerDataRx[j] = 0;
                                            }
                                            iSerRxN = 0;
                                        }

                                    }
                                    break;
                            }// switch
                        } // for

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        @SuppressLint("NewApi")
        private void checkComData(byte[] data, int size) {

            m_listener.onComData(data, size);
            int cmd = data[4] & 0xFF;

            switch (cmd) {
                case CMD_CHECK_BASE_STATUS_RESPONSE:// 查询状态类指令应答
                    int cmd1 = data[5] & 0xFF;
                    if (cmd1 == 1) {// 返回当前工作模式和版本
                        ModelInfo info = new ModelInfo();
                        info.mode = data[6];
                        info.hModel = data[7] & 0xff;
                        info.sVer = data[8] + "." + data[9] + "." + data[10];
                        m_listener.onModelEvent(info);
                    } else if (cmd1 == 3) { // 查询基础信标 结果
                        onBaseInfo(data);

                    } else if (cmd1 == 4) { // 查询投票信标 结果

                        onVoteInfo(data);

                    } else if (cmd1 == 5 || cmd1 == 6) {// 返回当前键盘参数结果
                        KeypadInfo info = new KeypadInfo();
                        info.ok = data[6];
                        info.chan = data[7];
                        info.keyId = ((data[8] & 0xff) << 8) | (data[9] & 0xff);
                        byte[] sn = Arrays.copyOfRange(data, 10, 16);
                        info.keySn = getKeySn(sn);
                        byte[] mc = Arrays.copyOfRange(data, 16, 19);
                        info.matchCode = new String(mc);
                        m_listener.onKeyPadEvent(info);

                        // if(!hasGetBaseInfo){ //第一次启动时取
                        // hasGetBaseInfo = true;
                        // getBaseStatus();
                        // }

                    } else if (cmd1 == 7) {// 查询键盘在线状态结果
                        OnLineInfo info = new OnLineInfo();
                        info.onLine = data[6];
                        info.idMode = data[7];
                        info.chan = data[8];
                        info.rssi = data[9];
                        info.tx = data[10];
                        info.rx = data[11];
                        info.baseId = data[12];
                        info.keyId = data[13] << 8 | data[14];
                        byte[] sn = Arrays.copyOfRange(data, 15, 21);
                        info.keySn = getKeySn(sn);

                        m_listener.onOnLineEvent(info);

                    } else if (cmd1 == 8 || cmd1 == 9) {
                        KeypadInfo info = new KeypadInfo();
                        info.cmd1 = cmd1;
                        info.ok = data[6];
                        info.chan = data[7];
                        info.keyId = ((data[8] & 0xff) << 8) | (data[9] & 0xff);
                        byte[] sn = Arrays.copyOfRange(data, 10, 16);
                        info.keySn = getKeySn(sn);
                        byte[] mc = Arrays.copyOfRange(data, 16, 19);
                        info.matchCode = new String(mc);
                        m_listener.onKeyPadEvent(info);
                    }

                    break;
                case CMD_BASE_STATUS_CHANGE:// 基础信标变化(主动下发)

                    responseBaseStatusChange(data, size);

                    onBaseInfo(data);
                    break;
                case CMD_VOTE_STATUS_CHANGE:// 投票信标变化

                    responseVoteStatusChange(data, size);
                    clearSentItem();
                    onVoteInfo(data);

                    break;

                case CMD_VOTE_SEND_SUCCESS_RESPONSE:
                    responseVoteSendSuccess(data, size);
                    break;

                case CMD_KEY_MANAGE:// 表决器管理类指令,单包，透传
                    CmdDataInfo info = new CmdDataInfo();
                    info.cmd = data[7];
                    info.data = Arrays.copyOfRange(data, 8, 28);
                    m_listener.onCmdData(info);
                    break;
                case CMD_MULTI_PCKAGE_DOWNLOAD:
                    onMultiPackageInfo(data, size);
                case CMD_FIRM_UPDATE_RESPONSE:
                    switch (data[5]) {
                        case 2:// '模块询问是否进入DFU
                            keepFirmMode();
                            eraseFlash();
                            break;
                        case 3:// 擦除应答
                            if (data[8] != 1) {
                                Log.e(TAG, "eraseFlash fail");
                                m_listener.onFirmUpdateResult(false, "擦除固件失败！");
                                break;
                            }
                            Log.e(TAG, "eraseFlash ok");
                            m_listener.onFirmUpdateInfo("擦除固件完成,开始写入...");
                            writeFrimPageErrCnt = 0;
                            writeFlash(firmWritePage);
                            break;
                        case 4:// 写应答
                            if (data[8] == 1) {
                                firmWritePage++;
                                writeFrimPageErrCnt = 0;
                                if (firmWritePage < firmWritePageMax) {
                                    m_listener.onFirmUpdate(firmWritePage * 100 / firmWritePageMax);
                                    Log.d(TAG, "write page ok! " + firmWritePage + "/" + firmWritePageMax + "  =="
                                            + firmWritePage * 100 / firmWritePageMax + "%");

                                    writeFlash(firmWritePage);
                                } else {
                                    m_listener.onFirmUpdateInfo("写入完成");
                                    m_listener.onFirmUpdate(100);
                                    Log.i(TAG, "write 100%");
                                    sleep(2000);
                                    exitFirmUpdate(1);
                                    Log.i(TAG, "exitFirmUpdate");
                                    m_listener.onFirmUpdateResult(true, null);
                                }
                            } else {
                                writeFrimPageErrCnt++;
                                m_listener.onFirmUpdateInfo("与入页:" + firmWritePage + "失败, 次数：" + writeFrimPageErrCnt);

                                writeFlash(firmWritePage);
                                Log.e(TAG, "write page fail! page:" + firmWritePage);
                                if (writeFrimPageErrCnt == 20) {
                                    m_listener.onFirmUpdateResult(false, "写入扇区" + firmWritePage + "失败！");
                                    exitFirmUpdate(2);

                                }
                            }
                            break;
                        case 5: // 退出升级
                            Log.d(TAG, "exit update!");
                            m_listener.onFirmUpdateInfo("退出升级模式成功！");
                            sleep(3000);
                            Log.d(TAG, "sleep 3s getWorkMode!");
                            for (int i = 0; i < 5; i++) {
                                getWorkMode();
                                sleep(1000);
                            }
                            getBaseStatus();

                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }

        }

        private void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        //// 回应模块基础信标变化，模块停止重发，否则模块会不断发送
        private void responseBaseStatusChange(byte[] data, int size) {
            byte[] retData = Arrays.copyOf(data, size);
            retData[4] = (byte) 0xF1;
            writeToCom(retData);
        }

        // 处理基础信标变化
        private void onBaseInfo(byte[] data) {

            // Arrays.fill(broadcastData, (byte) 0x0);//清空多包结果

            BaseInfo info = new BaseInfo();
            info.baseId = data[5] & 0xff;// baseID
            info.idMode = data[6] & 0xff;
            info.confId = ((data[7] & 0xff) << 8) | (data[8] & 0xff);

            info.billId = data[9] & 0xff;
            info.authCode = (data[10] & 0xff) << 8 | (data[11] & 0xff);
            info.login = data[12] & 0xff;
            info.report = data[13] & 0xff;
            info.offTime = data[14] & 0xff;
            info.attrib = data[15] & 0xff;
            byte[] bname = Arrays.copyOfRange(data, 16, 16 + 12);
            info.baseName = new String(bname);

            m_listener.onBaseEvent(info);
        }

        // 回应模块投票信标变化，模块停止重发，否则模块会不断发送
        private void responseVoteStatusChange(byte[] data, int size) {
            byte[] retData = Arrays.copyOf(data, size);
            retData[4] = (byte) 0xF2;
            writeToCom(retData);
        }

        // 回应发送成功通知
        private void responseVoteSendSuccess(byte[] data, int size) {

            byte[] retData = Arrays.copyOf(data, size);
            retData[4] = (byte) 0xF3;
            writeToCom(retData);

            int serialNo = data[5] & 0xff;
            removeSentItem(serialNo);

            if (serialNo == allOkSerialNumber) {
                m_listener.onVoteSubmitAllOkSuccess();
            } else {
                m_listener.onVoteSubmitSuccess();
            }
        }

        // 处理投票信标变化
        private void onVoteInfo(byte[] data) {
            byte[] dt = Arrays.copyOfRange(data, 4, data.length - 4 - 1);// 从len开始，和文档下标统一
            VoteInfo info = new VoteInfo();
            info.baseId = dt[1] & 0xff;
            info.nowT = dt[2] & 0xff << 8 | dt[3] & 0xff;
            info.dataPos = dt[4] & 0xff;
            info.mode = dt[5] & 0xff;
            info.mode1_msgType = dt[6] & 0xff;
            if (info.mode == VoteType_Stop) { // 停止
                if (info.mode1_msgType == 2) { // 有结果

                    info.resultInfo.resultType = dt[7] & 0xff;
                    info.resultInfo.bits = dt[8];
                    info.resultInfo.num0 = (dt[9] << 8 | dt[10]) & 0xffff;
                    info.resultInfo.num1 = (dt[11] << 8 | dt[12]) & 0xffff;
                    info.resultInfo.num2 = (dt[13] << 8 | dt[14]) & 0xffff;
                    info.resultInfo.num3 = (dt[15] << 8 | dt[16]) & 0xffff;
                    info.resultInfo.num4 = (dt[17] << 8 | dt[18]) & 0xffff;
                    info.resultInfo.num5 = (dt[19] << 8 | dt[20]) & 0xffff;
                    info.resultInfo.num6 = (dt[21] << 8 | dt[22]) & 0xffff;
                }
            } else if (info.mode == VoteType_BatchVote) {
                printDataBuf(data, data.length, "batchvote:");
                info.less = dt[11] & 0xff;
                info.mode2_modify = dt[23] & 0xff;
                info.mode3_secret = dt[12] & 0xff;
                info.voteid = dt[21] & 0xff;
            } else if (info.mode == VoteType_Choice) {
                info.mode2_modify = dt[7];
                info.mode3_secret = dt[8];
                info.mode4 = dt[9];
                info.mode5 = dt[10];
                info.mode6 = dt[11];
                info.mode7 = dt[12];
            } else {
                info.mode2_modify = dt[7];
                info.mode3_secret = dt[8];
                info.mode4 = dt[9];
                info.voteid = dt[11];
                info.file = dt[12];
                info.init = dt[13];
            }
            m_listener.onVoteEvent(info);
        }

    }

    private byte[] broadcastData = new byte[1024];
    private byte[] fileData;
    private int fileLen;
    private int lastPackH = -1;
    private short needBits = 0;
    private int okBits = 0;
    private int currDownloadMsgId;
    private int broadMsgId = -1;
    private long lastOnMultiPackageDataTime;

    private void onMultiPackageInfo(byte[] data, int size) {
        byte[] dt = Arrays.copyOfRange(data, 3, data.length - 3 - 1);// 从len开始，和文档下标统一
        int downCmd = dt[4];
        int downType = dt[5];
        int downId = dt[6];
        int dcmd = dt[7];

        if (downCmd == 1) { // 1:进入/退出下载
            resopnseMultiPackageMode(data, size);
            if (dcmd == 1) {
                Log.d(TAG, " 1:进入下载    。。。。。downType = " + downType);
                if (downType == 40) {
                    lastPackH = -1;
                    int packH = dt[8];
                    int packL = dt[9] + 1;
                    fileLen = (packH * 16 + packL) * 16;
                    Log.d(TAG, "下载文件，长度：" + fileLen);
                    fileData = new byte[fileLen];
                    Arrays.fill(fileData, (byte) 0x0);
                } else if (downType == 10) {

                }
            } else {
                Log.d(TAG, dcmd + ": 退出下载。。。。。。。。。");
                if (downType == 40) {
                    if (System.currentTimeMillis() - lastOnMultiPackageDataTime > 500) {
                        lastOnMultiPackageDataTime = System.currentTimeMillis();
                        m_listener.onMultiPackageData(fileData, fileLen - 4);
                    }

                }
            }
        } else if (downCmd == 2) { // 2:下载数据

            int msgid = (byte) dt[6];
            byte packH = dt[7];// 数据段 编号 当前最大15(16片)
            byte packL = dt[8];// 数据片编号
            Log.d(TAG, "下载。。。msgId:" + msgid + "  packH:" + packH + " apckL:" + packL);

            if (downType == 40) {// 文件下载
                if (lastPackH != packH) {
                    Log.i(TAG, "new packH.......");
                    okBits = 0;
                    lastPackH = packH;
                }

                System.arraycopy(dt, 9, fileData, (packH * 16 + packL) * 16, 16);
                okBits |= (1 << packL);
                Log.d(TAG, "packL:" + packL + "   okBits:" + okBits);

            } else {// 广播
                needBits = calcBits(packH);
                if (broadMsgId != msgid) {
                    Arrays.fill(broadcastData, (byte) 0x0);
                    broadMsgId = msgid;
                    okBits = 0;
                }
                System.arraycopy(dt, 9, broadcastData, packL * 16, 16);
                okBits |= (1 << packL);
                Log.d(TAG, "packL:" + packL + "   okBits:" + okBits + "  needBits:" + needBits);
                if (okBits == needBits) {
                    if (broadMsgId != currDownloadMsgId) {
                        m_listener.onMultiPackageData(broadcastData, (packH + 1) * 16);
                        currDownloadMsgId = broadMsgId;
                    }
                    Arrays.fill(broadcastData, (byte) 0x0);
                    broadMsgId = msgid;
                    okBits = 0;

                }
            }
        } else if (downCmd == 3) { // 3：询问
            resopnseMultiPackageQuery(data, size);
            Log.d(TAG, " 3：询问。。。。。。。。。");
        }
    }

    private short calcBits(byte packh) {
        short bits = 0;
        for (int i = 0; i <= packh; i++) {
            bits |= (1 << i);
        }
        return bits;
    }

    private void resopnseMultiPackageMode(byte[] data, int size) {
        byte[] retData = Arrays.copyOf(data, size);
        retData[4] = (byte) 0xC0;
        writeToCom(retData);
    }

    private void resopnseMultiPackageQuery(byte[] data, int size) {
        byte[] retData = Arrays.copyOf(data, size);
        retData[4] = (byte) 0xC0;
        int tmpOk = ~okBits;
        retData[11] = (byte) tmpOk;
        retData[12] = (byte) (tmpOk >> 8);
        writeToCom(retData);
    }

    public static String getVoteType(int mode) {
        switch (mode) {
            case VoteType_Stop:
                return "VoteType_Stop";
            case VoteType_Signin:
                return "VoteType_Signin";
            case VoteType_Vote:
                return "VoteType_Vote";
            case VoteType_BatchVote:
                return "VoteType_BatchVote";
            case VoteType_BatchSelect:
                return "VoteType_BatchSelect";

            default:
                break;
        }
        return "" + mode;
    }

    public static String getKeySn(byte[] data) {
        String sn = "";
        String CS = "0123456789ABCDEF";
        for (int i = 0; i < 6; i++) {
            sn += CS.charAt((data[i] >> 4) & 0xF);
            sn += CS.charAt((data[i] >> 0) & 0xF);
        }
        return sn;

    }


}
