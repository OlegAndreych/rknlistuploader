package ru.andreych.rkn.list.uploader.mikrotik;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class MikrotikConnector implements AutoCloseable {

    private final ApiConnection connection;

    public MikrotikConnector(String address, String login, String password) throws MikrotikApiException {
        final ApiConnection connection = ApiConnection.connect(address);
        connection.login(login, password);
        this.connection = connection;
    }

    public List<Map<String, String>> getListContent(String listName) throws MikrotikApiException {
        return connection.execute(format("/ip/firewall/address-list/print where list=%s", listName));
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public void removeAddresses(Collection<String> ids) throws MikrotikApiException {
        for (String id : ids) {
            connection.execute(format("/ip/firewall/address-list/remove .id=%s", id));
        }
    }

    public void addAddresses(Collection<String> addresses, String listName) throws MikrotikApiException {
        for (String address : addresses) {
            connection.execute(format("/ip/firewall/address-list/add address=%s list=%s", address, listName));
        }
    }
}
