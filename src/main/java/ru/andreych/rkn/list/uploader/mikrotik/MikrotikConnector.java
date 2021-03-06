package ru.andreych.rkn.list.uploader.mikrotik;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.SocketFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static me.legrange.mikrotik.ApiConnection.DEFAULT_COMMAND_TIMEOUT;

public class MikrotikConnector implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger(MikrotikConnector.class);

    private final ApiConnection connection;

    public MikrotikConnector(final String address, final int port, final String login, final String password, final int timeout) throws MikrotikApiException {
        final ApiConnection connection = ApiConnection.connect(SocketFactory.getDefault(), address, port, DEFAULT_COMMAND_TIMEOUT);
        connection.setTimeout(timeout);
        connection.login(login, password);
        this.connection = connection;
    }

    public List<Map<String, String>> getListContent(final String listName) throws MikrotikApiException {
        LOG.info("Getting \"{}\" list content.", listName);
        final List<Map<String, String>> list = this.connection.execute(format("/ip/firewall/address-list/print where list=%s", listName));
        LOG.info("\"{}\" list content size is {} items.", listName, list.size());

        return list;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    public void removeAddresses(final Collection<String> ids) throws MikrotikApiException {
        final int size = ids.size();
        int i = 0;
        for (final String id : ids) {
            this.connection.execute(format("/ip/firewall/address-list/remove .id=%s", id));
            i++;
            if (i % 1000 == 0) {
                LOG.info("Removed {} out of {}.", i, size);
            }
        }
    }

    public void addAddresses(final Collection<String> addresses, final String listName) throws MikrotikApiException {
        final int size = addresses.size();
        int i = 0;
        for (final String address : addresses) {
            this.connection.execute(format("/ip/firewall/address-list/add address=%s list=%s", address, listName));
            i++;
            if (i % 1000 == 0) {
                LOG.info("Added {} out of {}.", i, size);
            }
        }
    }
}
