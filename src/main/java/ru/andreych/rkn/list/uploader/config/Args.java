package ru.andreych.rkn.list.uploader.config;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"--routerAddress", "-ra"})
    private String routerAddress = "192.168.1.1";
    @Parameter(names = {"--routerPort", "-p"})
    private Integer port = 8728;
    @Parameter(names = {"--login", "-l"})
    private String login;
    @Parameter(names = {"--password", "-pw"})
    private String password;
    @Parameter(names = {"--listName", "-ln"})
    private String listName = "rkn";
    @Parameter(names = {"--apiUri", "-u"})
    private String apiUrl = "http://api.antizapret.info/group.php";

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRouterAddress() {
        return routerAddress;
    }

    public void setRouterAddress(String routerAddress) {
        this.routerAddress = routerAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
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
}
