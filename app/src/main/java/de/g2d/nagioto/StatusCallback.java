package de.g2d.nagioto;

import java.util.List;

import de.g2d.nagioto.domain.Host;

public interface StatusCallback {

    void onServerList(List<Host> servers);
}
