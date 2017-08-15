package com.sunvote.udptransfer.stream;

import android.support.annotation.NonNull;

import com.sunvote.udptransfer.core.LocalUDPDataSender;
import com.sunvote.udptransfer.work.SDKProcessWork;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:实现对应用层调用的输入输出流。
 * 这里是UDP模拟实现输出流
 */

public class UDPOutputStream extends OutputStream {
    @Override
    public void write(int i) throws IOException {
        LocalUDPDataSender.getInstance().send(new byte[]{(byte) i}, 1);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public void flush() throws IOException {
        super.flush();
    }

    @Override
    public void write(@NonNull byte[] buffer) throws IOException {
        write(buffer,0,buffer.length);
    }

    @Override
    public void write(@NonNull byte[] buffer, int offset, int count) throws IOException {
        byte[] temp = new byte[count];
        System.arraycopy(buffer, offset, temp, 0, count);
//        LocalUDPDataSender.getInstance().send(temp, temp.length);
        SDKProcessWork.getInstance().execute(temp,count);

    }
}
