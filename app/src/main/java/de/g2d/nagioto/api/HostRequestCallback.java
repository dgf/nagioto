package de.g2d.nagioto.api;

import de.g2d.nagioto.domain.HostResponse;

/**
 * Created by sasse_h on 31.01.15.
 */
public interface HostRequestCallback {
    void onFinish(HostResponse hosts);
}
