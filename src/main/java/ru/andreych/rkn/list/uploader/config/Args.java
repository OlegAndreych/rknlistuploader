package ru.andreych.rkn.list.uploader.config;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"--routerAddress", "-ra"}, description = "Router address")
    private String routerAddress = "192.168.1.1";
    @Parameter(names = {"--routerPort", "-p"}, description = "Router API port")
    private Integer port = 8728;
    @Parameter(names = {"--login", "-l"}, description = "Router login")
    private String login;
    @Parameter(names = {"--password", "-pw"}, description = "Router password")
    private String password;
    @Parameter(names = {"--listName", "-ln"}, description = "Address list name")
    private String listName = "rkn";
    @Parameter(names = {"--apiUri", "-u"}, description = "Antizapret API url")
    private String apiUrl = "http://api.antizapret.info/group.php";
    @Parameter(names = {"--mikrotikTimeout", "-mt"}, description = "Mikrotik synchronous command timeout")
    private int mikrotikTimeout = 300_000;
    @Parameter(names = {"-h", "--help"}, help = true, description = "Usage description")
    private boolean help;

    public boolean isHelp() {
        return help;
    }

    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }


    public String getRouterAddress() {
        return routerAddress;
    }


    public Integer getPort() {
        return port;
    }


    public String getListName() {
        return listName;
    }


    public String getApiUrl() {
        return apiUrl;
    }


    @Override
    public String toString() {
        return "Args{" +
                "routerAddress='" + routerAddress + '\'' +
                ", port=" + port +
                ", login='" + login + '\'' +
                ", password='" + "***" + '\'' +
                ", listName='" + listName + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                '}';
    }

    public int getMikrotikTimeout() {
        return mikrotikTimeout;
    }
}
