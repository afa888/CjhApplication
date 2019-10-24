package com.example.plugin1;

import android.content.Context;

import com.example.library.IDynamic;

public class Dynamic implements IDynamic {

    @Override
    public String getStringForResId(Context context) {
        return context.getResources().getString(R.string.myplugin1_hello_world);
    }
}
