package varunon9.me.dynamicfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;

import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

// Docs: https://developer.android.com/guide/playcore/feature-delivery/on-demand
public class MainActivity extends AppCompatActivity {

    // to use in onActivityResult
    private static final int ALT_ACCO_REQUEST_CODE = 0;
    // Initializes a variable to later track the session ID for a given request.
    int altAccoSessionId = 0;
    SplitInstallManager splitInstallManager;

    // to show progress
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    // Creates a listener for request status updates.
    SplitInstallStateUpdatedListener listener = state -> {
        if (state.sessionId() == altAccoSessionId) {
            // Read the status of the request to handle the state update.
            onAltAccoModuleStateUpdate(state);
        }
    };

    public void onVisitHostAppClick(View view) {
        downloadAltAccoModule();
    }

    private void downloadAltAccoModule() {
        dialog.setTitle("Downloading host app dynamically");
        dialog.show();
        // Creates an instance of SplitInstallManager.
        splitInstallManager =
                SplitInstallManagerFactory.create(this);

        // Creates a request to install a module.
        SplitInstallRequest request =
                SplitInstallRequest
                        .newBuilder()
                        // You can download multiple on demand modules per
                        // request by invoking the following method for each
                        // module you want to install.
                        .addModule("ingornaltacco")
                        .build();

        splitInstallManager
                // Submits the request to install the module through the
                // asynchronous startInstall() task. Your app needs to be
                // in the foreground to submit the request.
                .startInstall(request)
                // You should also be able to gracefully handle
                // request state changes and errors. To learn more, go to
                // the section about how to Monitor the request state.
                .addOnSuccessListener(sessionId -> {
                    altAccoSessionId = sessionId;
                })
                .addOnFailureListener(exception -> {
                    exception.printStackTrace();
                    dialog.setMessage("Error addOnFailureListener: " + exception.getMessage());
                });
        // Registers the listener.
        splitInstallManager.registerListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When your app no longer requires further updates, unregister the listener.
        splitInstallManager.unregisterListener(listener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        // Emulates installation of future on demand modules using SplitCompat.
        // Necessary to access AltAccoActivity once ingornaltacco module is downloaded
        // Docs: https://developer.android.com/guide/playcore/feature-delivery/on-demand#access_downloaded_modules
        SplitCompat.install(this);
    }

    private void onAltAccoModuleStateUpdate(SplitInstallSessionState state) {
        switch (state.status()) {
            case SplitInstallSessionStatus.DOWNLOADING:
                long totalBytes = state.totalBytesToDownload();
                long downloaded = state.bytesDownloaded();
                int progressPercentage = (int) ((downloaded / totalBytes) * 100);
                // Update progress bar.
                System.out.println("Remaining bytes: " + totalBytes);
                String progress = "Progress: " + progressPercentage + "%";
                System.out.println(progress);
                dialog.setMessage(progress);
                break;

            case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                // Displays a confirmation for the user to confirm the request.
                try {
                    splitInstallManager.startConfirmationDialogForResult(
                            state,
                            /* activity = */ this,
                            // You use this request code to later retrieve the user's decision.
                            /* requestCode = */ ALT_ACCO_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;

            case SplitInstallSessionStatus.INSTALLED:
                // After a module is installed, you can start accessing its content or
                // fire an intent to start an activity in the installed module.
                // For other use cases, see access code and resources from installed modules.

                // If the request is an on demand module for an Android Instant App
                // running on Android 8.0 (API level 26) or higher, you need to
                // update the app context using the SplitInstallHelper API.
                switchToHostApp();
                break;
            case SplitInstallSessionStatus.DOWNLOADED:
                System.out.println("Download successful, Installing host app...");
                dialog.setMessage("Download successful, Installing host app...");
                switchToHostApp();
                break;
            case SplitInstallSessionStatus.FAILED:
                dialog.setMessage("Failed to download host app" + state.errorCode());
                break;

            default:
                dialog.setMessage("Failed to download, unknown error occurred. Status code: " + state.status() + ", Error code: " + state.errorCode());
        }
    }

    private void switchToHostApp() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent();
        intent.setClassName("varunon9.me.dynamicfeature", "com.ingoibibo.ingornaltacco.AltAccoActivity");
        startActivity(intent);
    }
}