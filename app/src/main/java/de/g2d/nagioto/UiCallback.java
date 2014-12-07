package de.g2d.nagioto;

import java.util.List;

import de.g2d.nagioto.domain.Host;
import de.g2d.nagioto.domain.Service;
import de.g2d.nagioto.domain.Status;

public interface UiCallback {

    void onStatusResponse(Status status);

}
