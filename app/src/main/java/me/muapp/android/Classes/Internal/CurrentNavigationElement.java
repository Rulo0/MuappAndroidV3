package me.muapp.android.Classes.Internal;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Created by rulo on 24/03/17.
 */

public class CurrentNavigationElement {
    private MenuItem itm;
    private Fragment frag;

    public CurrentNavigationElement(MenuItem itm, Fragment frag) {
        this.itm = itm;
        this.frag = frag;
    }

    public MenuItem getItm() {
        return itm;
    }

    public Fragment getFrag() {
        return frag;
    }
}

