package br.com.pd.acesso.modbus;

import java.nio.ByteBuffer;

public class ModbusRtu {
    private final byte address;

    public ModbusRtu(byte address) {
        this.address = address;
    }

    public byte[] buildWriteSingleRegister(int registerAddress, int value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.put(address);
        buf.put((byte) 0x06); // function write single register
        buf.putShort((short) registerAddress);
        buf.putShort((short) value);
        int crc = crc16(buf.array(), 0, 6);
        buf.putShort((short) crc);
        return buf.array();
    }

    public static int crc16(byte[] data, int offset, int len) {
        int crc = 0xFFFF;
        for (int i = offset; i < offset + len; i++) {
            crc ^= Byte.toUnsignedInt(data[i]);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ 0xA001;
                } else {
                    crc = crc >> 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}



