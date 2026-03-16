package org.example.hominganimal.domain.device.valueobject;

public enum DeviceStatus {
    OFFLINE(0, "离线"),
    ONLINE(1, "在线");

    private final int code;
    private final String desc;
    DeviceStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static DeviceStatus fromCode(int code) {
        for (DeviceStatus s : values()) {
            if (s.code == code) return s;
        }
        return OFFLINE;
    }
}
