package cl.entel.plugins.huellero;

// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import android.graphics.Bitmap;
import android.util.Log;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import android.util.Base64;

public class Huellero extends CordovaPlugin {

    private static final String TAG = "HUELLERO";
    
    private byte[] byteArray;

    private CallbackContext callbackContext = null;
    
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext newCallbackContext) {
        Log.i(TAG, "execute");

        Context context = cordova.getActivity().getApplicationContext();
        if ("capturar".equals(action)) {
            callbackContext = newCallbackContext;
            cordova.setActivityResultCallback (this);

            Intent intent = new Intent(context, NewActivity.class);
            this.cordova.getActivity().startActivityForResult(intent, 1);

            return true;
        }
        callbackContext.error("No existe metodo: " + action);
        Log.i(TAG, "error");
        return false;
    }



    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Activity Result");

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                Log.i(TAG, "Activity Result OK");

                byte[] resultBm = data.getByteArrayExtra("result");

                Log.i(TAG, "converting to B64");               
                
                    
                String encoded = encode(resultBm);

                if(encoded != null){
                    Log.i(TAG, "got b64");
                    Log.i(TAG, encoded);
                }

                
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, encoded);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);

            }
            if (resultCode == Activity.RESULT_CANCELED) {

                Log.i(TAG, "Activity Result FAILED");

                String error =data.getStringExtra("error");

                Log.i(TAG, error);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, error);
                callbackContext.sendPluginResult(pluginResult);
            }
        }
    }

    private String encode(byte[] d) {
        if (d == null) {
            return null;
        }
        int idx;
        byte[] data = new byte[(d.length + 2)];
        System.arraycopy(d, 0, data, 0, d.length);
        byte[] dest = new byte[((data.length / 3) * 4)];
        int sidx = 0;
        int didx = 0;
        while (sidx < d.length) {
            dest[didx] = (byte) ((data[sidx] >>> 2) & 63);
            dest[didx + 1] = (byte) (((data[sidx + 1] >>> 4) & 15) | ((data[sidx] << 4) & 63));
            dest[didx + 2] = (byte) (((data[sidx + 2] >>> 6) & 3) | ((data[sidx + 1] << 2) & 63));
            dest[didx + 3] = (byte) (data[sidx + 2] & 63);
            sidx += 3;
            didx += 4;
        }
        for (idx = 0; idx < dest.length; idx++) {
            if (dest[idx] < (byte) 26) {
                dest[idx] = (byte) (dest[idx] + 65);
            } else if (dest[idx] < (byte) 52) {
                dest[idx] = (byte) ((dest[idx] + 97) - 26);
            } else if (dest[idx] < (byte) 62) {
                dest[idx] = (byte) ((dest[idx] + 48) - 52);
            } else if (dest[idx] < (byte) 63) {
                dest[idx] = (byte) 43;
            } else {
                dest[idx] = (byte) 47;
            }
        }
        for (idx = dest.length - 1; idx > (d.length * 4) / 3; idx--) {
            dest[idx] = (byte) 61;
        }
        return new String(dest);
    }

}