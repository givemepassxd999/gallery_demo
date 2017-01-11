package com.gg.givemepass.contentproviderdemo;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by rick.wu on 2017/1/11.
 */

public class AlbumMenuDialog extends Dialog{
    public AlbumMenuDialog(Context context) {
        super(context, R.style.display_dialog);
        setContentView(R.layout.album_menu_dialog);
    }
}
