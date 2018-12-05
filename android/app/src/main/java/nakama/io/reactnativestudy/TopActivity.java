
package nakama.io.reactnativestudy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class TopActivity extends AppCompatActivity {
  public static final String SUB_BUNDLE_URL = "http://172.16.1.58:8080/index.sub.bundle.js";

  private ReactRootView mainReactRootView;
  private ReactRootView subReactRootView;

  private ReactInstanceManager mainReactInstanceManager;
  private ReactInstanceManager subReactInstanceManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_top);
    final FrameLayout rootLayout = findViewById(R.id.root_layout);

    //SetUp Main React App
    mainReactRootView = new ReactRootView(this);
    mainReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .addPackage(new MainReactPackage())
      .setCurrentActivity(this)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mainReactRootView.startReactApplication(mainReactInstanceManager, "MainReactNativeApp");
    rootLayout.addView(mainReactRootView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    //SetUp Sub React App
    new AsyncTask<Void, Void, Boolean>() {

      @Override
      protected Boolean doInBackground(Void... voids) {
        return fetchSubBundle();
      }

      @Override
      protected void onPostExecute(Boolean isSuccessful) {
        Log.d("ReactSample", "Loaded sub bundle. isSuccessful:" + isSuccessful);
        if (!isSuccessful) return;

        subReactInstanceManager = ReactInstanceManager.builder()
          .setApplication(getApplication())
          .setJSBundleFile(getSubAppBundleFile().getAbsolutePath())
          .addPackage(new MainReactPackage())
          .setCurrentActivity(TopActivity.this)
          .setInitialLifecycleState(LifecycleState.RESUMED)
          .build();


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        params.gravity = Gravity.TOP;
        params.setMargins(30, 30, 30, 0);

        subReactRootView = new ReactRootView(TopActivity.this);
        subReactRootView.startReactApplication(subReactInstanceManager, "SubReactNativeApp");
        subReactRootView.setLayoutParams(params);

        rootLayout.addView(subReactRootView);
      }
    }.execute();

  }

  private File getSubAppBundleFile() {
    return new File(TopActivity.this.getFilesDir(), "index.android.sub.bundle");
  }

  private boolean fetchSubBundle() {
    try {
      ResponseBody body = new OkHttpClient()
        .newCall(new Request.Builder().url(SUB_BUNDLE_URL).build())
        .execute()
        .body();
      if (body == null) return false;
      Log.d("ReactSample", "Save bundle into " + getSubAppBundleFile().getAbsoluteFile());

      BufferedInputStream input = new BufferedInputStream(body.byteStream());
      OutputStream output = new FileOutputStream(getSubAppBundleFile());

      byte[] data = new byte[1024];
      int count;
      while ((count = input.read(data)) != -1) {
        output.write(data, 0, count);
      }

      output.flush();
      output.close();
      input.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    mainReactInstanceManager.onHostPause(this);
    if (subReactInstanceManager != null) {
      subReactInstanceManager.onHostPause(this);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mainReactInstanceManager.onHostResume(this);
    if (subReactInstanceManager != null) {
      subReactInstanceManager.onHostResume(this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mainReactInstanceManager.onHostDestroy(this);
    if (subReactInstanceManager != null) {
      subReactInstanceManager.onHostDestroy(this);
    }

    mainReactRootView.unmountReactApplication();
    if (subReactRootView != null) {
      subReactRootView.unmountReactApplication();
    }
  }
}
