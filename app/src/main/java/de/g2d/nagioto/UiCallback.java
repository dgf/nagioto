package de.g2d.nagioto;

import de.g2d.nagioto.domain.Status;

public interface UiCallback {

    void onStatusResponse(Status status);

    void onError(Throwable throwable);

    void onError(String label,Throwable throwable);
}
