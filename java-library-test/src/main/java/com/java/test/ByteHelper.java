package com.java.test;

import java.nio.ByteBuffer;

public class ByteHelper {
    private ByteBuffer byteBuffer = null;
    private Object object = null;

    public ByteHelper(byte[] originBuffer) {
        byteBuffer = ByteBuffer.wrap(originBuffer);
    }

    public int size(){
        return byteBuffer.capacity();
    }

    public ByteHelper(int size) {
        byteBuffer = ByteBuffer.allocate(size);
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public ByteHelper put(byte b){
        byteBuffer.put(b);
        return this;
    }

    public ByteHelper putShort(short value){
        byteBuffer.putShort(value);
        return this;
    }

    public ByteHelper put(byte[] bb){
        byteBuffer.put(bb);
        return this;
    }


    public ByteHelper put(byte[] src, int offset, int length) {
        byteBuffer.put(src,offset,length);
        return this;
    }

    public byte[] toBytes(){
        return byteBuffer.array();
    }

//    public ByteHelper get() {
//        object = byteBuffer.get();
//        return this;
//    }
//
//    public ByteHelper get(final int length) {
//        byte[] bytes = new byte[length];
//        byteBuffer.get(bytes);
//        object = bytes;
//        return this;
//    }
    public Builder position(final int offset,final int length) {
        ByteBuffer tmp = byteBuffer.asReadOnlyBuffer();
        tmp.position(offset);
        return new Builder(tmp,length);
    }


    public static class Builder{
        private ByteBuffer object = null;
        private int length = 0;
        Builder(ByteBuffer obj,int len){
            object = obj;
            this.length = len;
        }
        public ByteBuffer getByteBuffer() {
            return object;
        }

        public byte[] subBuffer(){
            if (length <= 0)
                return null;
            byte[] bytes = new byte[length];
            //object.flip();
            object.get(bytes);
            return bytes;
        }


        public int toInt(){
            if (length == 2){
                return object.getShort();
            }if (length == 4){
                return object.getInt();
            }else if(length == 1){
                return object.get();
            }
            return 0;
        }

        public String toHexString(){
            if (length > 1){
                byte[] bArray = new byte[length];
                object.get(bArray);
                object = null;
                StringBuffer sb = new StringBuffer(bArray.length);
                String sTemp;
                for (int i = 0; i < bArray.length; i++) {
                    sTemp = Integer.toHexString(0xFF & bArray[i]);
                    if (sTemp.length() < 2)
                        sb.append(0);
                    sb.append(sTemp.toUpperCase());
                }
                return sb.toString();
            }else if(length == 1){
                String s = Integer.toHexString(object.get() & 0xFF);
                if (s.length() == 1) {
                    return "0" + s;
                } else {
                    return s;
                }
            }
            return null;

        }
    }

}
