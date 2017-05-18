package me.muapp.android.Classes.Internal;

import android.support.v4.app.Fragment;

/**
 * Created by rulo on 24/03/17.
 */

public class SelectedNavigationElement {
    private Integer pos;
    private Fragment frag;

    public SelectedNavigationElement(Integer pos, Fragment frag) {
        this.pos = pos;
        this.frag = frag;
    }

    public Integer getPos() {
        return pos;
    }

    public Fragment getFrag() {
        return frag;
    }
}

