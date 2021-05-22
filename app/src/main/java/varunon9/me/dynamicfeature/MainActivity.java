package varunon9.me.dynamicfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void onAltAccoModuleStateUpdate(SplitInstallSessionState state) {
        switch (state.status()) {
            case SplitInstallSessionStatus.DOWNLOADING:
                long totalBytes = state.totalBytesToDownload();
                long progress = state.bytesDownloaded();
                // Update progress bar.
                System.out.println("Remaining bytes: " + totalBytes);
                System.out.println("Progress: " + progress + "%");
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

            case SplitInstallSessionStatus.INSTALLED:

                // After a module is installed, you can start accessing its content or
                // fire an intent to start an activity in the installed module.
                // For other use cases, see access code and resources from installed modules.

                // If the request is an on demand module for an Android Instant App
                // running on Android 8.0 (API level 26) or higher, you need to
                // update the app context using the SplitInstallHelper API.
                switchToHostApp();
        }
    }

    private void switchToHostApp() {
        Intent intent = new Intent();
        intent.setClassName("varunon9.me.dynamicfeature", "com.ingoibibo.ingornaltacco.AltAccoActivity");
        startActivity(intent);
    }
}