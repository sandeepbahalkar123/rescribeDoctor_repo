package com.smart.pen.core.model;

import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDevice;

import com.smart.pen.core.symbol.DeviceType;

/**
 * 设备对象
 *
 * @author Xiaoz
 * @date 2015年6月11日 下午2:25:58
 * <p>
 * Description
 */
public class DeviceObject {
    /**
     * 设备名字
     */
    public String name;
    /**
     * 设备地址
     */
    public String address;
    /**
     * 设备大版本
     */
    public int verMajor;
    /**
     * 设备小版本
     */
    public int verMinor;
    private DeviceType type = DeviceType.UNKNOWN;

    public DeviceObject(BluetoothDevice device) {
        this.type = DeviceType.BLE;
        this.name = device.getName();
        this.address = device.getAddress();

        //指定规则的名字过滤
        if (name.startsWith("Pen")) {
            //只显示最后6位识别码
            name = "Pen" + name.substring(name.length() - 6, name.length());
        }
    }

    public DeviceObject(UsbDevice device) {
        this.type = DeviceType.USB;
        this.name = device.getDeviceName();
    }

    /**
     * 获取设备类型
     *
     * @return
     */
    public DeviceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + " " + name + " " + address + " " + verMajor + " " + verMinor;
    }
}
