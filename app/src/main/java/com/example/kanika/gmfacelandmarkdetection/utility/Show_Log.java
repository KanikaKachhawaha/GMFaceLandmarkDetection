package com.example.kanika.gmfacelandmarkdetection.utility;

/**
 * Created by kanika on 8/6/18.
 */
public class Show_Log {

        public static void Logcat(String TAG, String Message, int LogType) {
            switch (LogType) {
                // Case 1- To Show Message as Debug
                case 1:
                    android.util.Log.d(TAG, Message);
                    break;
                // Case 2- To Show Message as Error
                case 2:
                    android.util.Log.e(TAG, Message);
                    break;
                // Case 3- To Show Message as Information
                case 3:
                    android.util.Log.i(TAG, Message);
                    break;
                // Case 4- To Show Message as Verbose
                case 4:
                    android.util.Log.v(TAG, Message);
                    break;
                // Case 5- To Show Message as Assert
                case 5:
                    android.util.Log.w(TAG, Message);
                    break;
                // Case Default- To Show Message as System Print
                default:
                    System.out.println(Message);
                    break;
            }
        }

}
