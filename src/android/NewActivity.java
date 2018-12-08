package cl.entel.plugins.huellero;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import com.zy.lib.morpho.ui.BioCapture;
import com.zy.lib.morpho.ui.IBioCapture;
import com.zy.lib.morpho.ui.ZyRequest;
import com.zy.lib.morpho.ui.ZyResponse;

import android.content.Context;
import android.content.Intent;


public class NewActivity extends Activity {

    private byte[] byteArray;
    private static final String TAG = "New_Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        capturar();
    }

    private void capturar() {
        Log.i(TAG, "Starting capture");
        try {
            IBioCapture iBioCapture = new BioCapture(this, new IBioCapture.ICallback() {                        
                @Override
                public void onSuccess(ZyResponse zyResponse) {
                    Log.i(TAG, "Success");
                    Bitmap imgFP;

                    imgFP = zyResponse.wsq;

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",imgFP);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "Complete");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"{\"bitmap\":\""+"on complete"+"\"}}");
                    pluginResult.setKeepCallback(true);                    
                }

                @Override
                public void onStart() {
                    Log.i(TAG, "Start");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,"{\"bitmap\":\""+"onstart"+"\"}}");
                    pluginResult.setKeepCallback(true);                    
                }
    
                @Override
                public void onError(ZyResponse obj) {
                
                    Log.i(TAG, "Error");
                    Log.i(TAG, obj.deError);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("error",obj.deError);
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                    finish();                    
                }

            });

            ZyRequest zyRequest = new ZyRequest();
            iBioCapture.capturar(zyRequest);

        } catch (Exception e) {
            Log.i(TAG, "Exception");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("error",e.getMessage());
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();                    
        }
        
    }
}
























