package de.g2d.nagioto;

import java.util.List;

import de.g2d.nagioto.domain.Cgi2Response;
import de.g2d.nagioto.domain.Server;

public interface StatusCallback {
    void onFinish(Cgi2Response cgiResponse);

    void onServerList(List<Server> servers);
}
