package com.example.cjhapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.IDynamic;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

//通过DexClassloader加载apk ，然后调用一个类
public class MainActivity extends AppCompatActivity {
    private String dexpath = null;    //apk文件地址
    private File fileRelease = null;  //释放目录
    private DexClassLoader classLoader = null;

    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    private String apkName = "plugin1-release.apk";    //apk名称

    TextView tv;

    ImageView iv;
    RelativeLayout rl;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            Utils.extractAssets(newBase, apkName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        iv = (ImageView) findViewById(R.id.iv);
        rl = (RelativeLayout) findViewById(R.id.rl);
        File extractFile = this.getFileStreamPath(apkName);
        dexpath = extractFile.getPath();

        fileRelease = getDir("dex", 0); //0 表示Context.MODE_PRIVATE

        Log.e("DEMO", "dexpath:" + dexpath);
        Log.e("DEMO", "fileRelease.getAbsolutePath():" +
                fileRelease.getAbsolutePath());

        classLoader = new DexClassLoader(dexpath,
                fileRelease.getAbsolutePath(), null, getClassLoader());

        //普通调用，反射的方式
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("com.example.plugin1.Bean");
                    Object beanObject = mLoadClassBean.newInstance();

                    Method getNameMethod = mLoadClassBean.getMethod("getName");
                    getNameMethod.setAccessible(true);
                    String name = (String) getNameMethod.invoke(beanObject);

                    tv.setText(name);
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });


        //普通调用，反射的方式，调用资源 字符
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadResources();
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("com.example.plugin1.Dynamic");
                    Object beanObject = mLoadClassBean.newInstance();

                    IDynamic dynamic = (IDynamic) beanObject;
                    String content = dynamic.getStringForResId(MainActivity.this);
                    tv.setText(content);
                    Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });


        //普通调用，反射的方式，调用资源 图片
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadResources();
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("com.example.plugin1.UIUtil");

                    Drawable drawable = (Drawable) RefInvoke.invokeStaticMethod(mLoadClassBean, "getImageDrawable", Context.class, MainActivity.this);

                    iv.setImageDrawable(drawable);
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });

        //普通调用，反射的方式，调用资源 布局
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadResources();
                Class mLoadClassBean;
                try {
                    mLoadClassBean = classLoader.loadClass("com.example.plugin1.UIUtil");
                    View view = (View) RefInvoke.invokeStaticMethod(mLoadClassBean, "getLayout", Context.class, MainActivity.this);
                    rl.addView(view);
                } catch (Exception e) {
                    Log.e("DEMO", "msg:" + e.getMessage());
                }
            }
        });
    }

    protected void loadResources() {
        try {
            //
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexpath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mResources = new Resources(mAssetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        if (mAssetManager == null) {
            Log.e("DEMO3", "mAssetManager is null");
            return super.getAssets();
        }

        Log.e("DEMO3", "mAssetManager is not null");
        return mAssetManager;
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            Log.e("DEMO3", "mResources is null");
            return super.getResources();
        }

        Log.e("DEMO3", "mResources is not null");
        return mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        if (mTheme == null) {
            Log.e("DEMO3", "Theme is null");
            return super.getTheme();
        }

        Log.e("DEMO3", "Theme is not null");
        return mTheme;
    }
}
