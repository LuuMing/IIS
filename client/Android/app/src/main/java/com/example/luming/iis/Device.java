package com.example.luming.iis;

/**
 * Created by luming on 2018/11/23.
 */

public class Device {
    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Device(String name, String ip, Integer port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    private String name;
    private String ip;
    private Integer port;

}
