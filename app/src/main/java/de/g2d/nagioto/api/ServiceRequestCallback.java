package de.g2d.nagioto.api;

import de.g2d.nagioto.domain.ServiceResponse;

/**
 * Created by sasse_h on 31.01.15.
 */
public interface ServiceRequestCallback {
    void onFinish(ServiceResponse services);
}
