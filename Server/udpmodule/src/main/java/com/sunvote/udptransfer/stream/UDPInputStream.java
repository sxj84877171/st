package com.sunvote.udptransfer.stream;

import android.support.annotation.NonNull;

import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description: 实现对应用层调用的输入输出流。
 * 这里是UDP模拟实现输入流
 */
public class UDPInputStream extends InputStream {

    public static final String TAG = UDPInputStream.class.getSimpleName();

    private Thread thread = null;
    private LinkedList<Byte> linkedList = new LinkedList<Byte>();
    public static long MAX_CACHE_SIZE = 4 * 1024;
    private Lock lock = new ReentrantLock();
    private Condition empty = lock.newCondition();
    private boolean close = false;

    public UDPInputStream() {
    }


    public void pushDatas(byte[] datas, int length) {
        try {
            try {
                lock.lock();
                if (linkedList.size() < MAX_CACHE_SIZE) {
                    for (int i = length - 1; i >= 0; i--) {
                        linkedList.push(datas[i]);
                    }
                } else {
                    LogUtil.v(UDPModule.TAG, "SDK还没有接收完数据，缓冲区的数据大小：" + linkedList.size() + "，丢失数据：", datas,length);
                }
            } finally {
                empty.signal();
                lock.unlock();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "UDP receiver message", e);
            close = true;
        }
    }


    @Override
    public int read() throws IOException {
        if (thread == null) {
            throw new IOException("thread is dead.");
        }
        try {
            lock.lock();
            while (linkedList.isEmpty()) {
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return linkedList.pop();
        } finally {
            lock.unlock();
        }
    }


    public boolean isClose() {
        return close;
    }

    @Override
    public void close() throws IOException {
        close = true;
        super.close();
    }

    @Override
    public int read(@NonNull byte[] buffer) throws IOException {
        try {
            lock.lock();
            if (buffer == null) {
                throw new IOException("buffer is empty");
            }
            if (close) {
                throw new IOException("the stream has closed");
            }

            while (linkedList.isEmpty()) {
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int index = 0;
            while (index < buffer.length && !linkedList.isEmpty()) {
                buffer[index++] = linkedList.pop();
            }
            LogUtil.v(UDPModule.TAG, "SDK接收数据，接收的数据大小：" + index + "，数据：", buffer,index);
            return index;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        try {
            lock.lock();
            if (buffer == null) {
                throw new IOException("buffer is empty");
            }

            while (linkedList.isEmpty()) {
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int index = 0;
            while (index < byteCount && !linkedList.isEmpty()) {
                index++;
                buffer[byteOffset++] = linkedList.pop();
            }
            return index;
        } finally {
            lock.unlock();
        }
    }

}
