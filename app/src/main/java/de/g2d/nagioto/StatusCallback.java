package de.g2d.nagioto;

import de.g2d.nagioto.domain.Cgi2Response;

public interface StatusCallback {
    void onFinish(Cgi2Response cgiResponse);
}
