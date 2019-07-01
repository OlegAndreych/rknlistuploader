package ru.andreych.rkn.list.uploader.config;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"--routerAddress", "-ra"}, description = "Router address")
    private String routerAddress = "192.168.1.1";
    @Parameter(names = {"--routerPort", "-p"}, description = "Router API port")
    private Integer port = 8728;
    @Parameter(names = {"--listName", "-ln"}, description = "Address list name")
    private String listName = "rkn";
    @Parameter(names = {"--apiUri", "-u"}, description = "Antizapret API url")
    private String apiUrl = "http://api.antizapret.info/group.php";
    @Parameter(names = {"--mikrotikTimeout", "-mt"}, description = "Mikrotik synchronous command timeout")
    private int mikrotikTimeout = 300_000;
    @Parameter(names = {"--apiConnTimeout", "-act"}, description = "Antizapret API connection timeout in milliseconds")
    private Long antizapretConnectionTimeout = 300_000L;
    @Parameter(names = {"--apiSockReadTimeout", "-asrt"}, description = "Antizapret API socket read timeout in milliseconds")
    private Long antizapretConnectionSocketReadTimeout = 300_000L;
    @Parameter(names = {"--apiSockWriteTimeout", "-aswt"}, description = "Antizapret API socket write timeout in milliseconds")
    private Long antizapretConnectionSocketWriteTimeout = 300_000L;
    @Parameter(names = {"--password", "-pw"}, description = "Router password")
    private String password;
    @Parameter(names = {"--login", "-l"}, description = "Router login")
    private String login;
    @Parameter(names = {"-h", "--help"}, help = true, description = "Usage description")
    private boolean help;
    @Parameter(names = {"-hp", "--hashPath"}, description = "Path to file with addresses hash")
    private String hashFilePath = "/var/lib/rknlistuploader/hash";

    public Long getAntizapretConnectionSocketReadTimeout() {
        return this.antizapretConnectionSocketReadTimeout;
    }

    public Long getAntizapretConnectionSocketWriteTimeout() {
        return this.antizapretConnectionSocketWriteTimeout;
    }

    public boolean isHelp() {
        return this.help;
    }

    public String getLogin() {
        return this.login;
    }


    public String getPassword() {
        return this.password;
    }


    public String getRouterAddress() {
        return this.routerAddress;
    }


    public Integer getPort() {
        return this.port;
    }


    public String getListName() {
        return this.listName;
    }


    public String getApiUrl() {
        return this.apiUrl;
    }


    @Override
    public String toString() {
        return "Args{" +
                "routerAddress='" + this.routerAddress + '\'' +
                ", port=" + this.port +
                ", login='" + this.login + '\'' +
                ", password='" + "***" + '\'' +
                ", listName='" + this.listName + '\'' +
                ", apiUrl='" + this.apiUrl + '\'' +
                '}';
    }

    public int getMikrotikTimeout() {
        return this.mikrotikTimeout;
    }

    public Long getAntizapretConnectionTimeout() {
        return this.antizapretConnectionTimeout;
    }

    public String getHashFilePath() {
        return this.hashFilePath;
    }
}
