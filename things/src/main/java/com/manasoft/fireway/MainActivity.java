package com.manasoft.fireway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String FIREBASE_ONLINE_ENDPOINT = "/online";
    public static final String FIREBASE_INFO_CONNECTED = ".info/connected";
    private static final int INTERVAL_LED_START = 10000;
    private static final int INTERVAL_LED_START_TEMP = 3000;
    private Boolean stop = false;
    //Pin BCM
    private static final String GPIO_PIN_LED1_R = "BCM2";
    private static final String GPIO_PIN_LED1_O = "BCM3";
    private static final String GPIO_PIN_LED1_V = "BCM4";

    private static final String GPIO_PIN_LED2_R="BCM17";
    private static final String GPIO_PIN_LED2_O="BCM27";
    private static final String GPIO_PIN_LED2_V="BCM22";
    // GPIO PIN
    private Gpio mLedGpio_1R;
    private Gpio mLedGpio_1O;
    private Gpio mLedGpio_1V;
    private Gpio mLedGpio_2R;
    private Gpio mLedGpio_2O;
    private Gpio mLedGpio_2V;

    //Firebase Referesse and Node
    private static final String NODE_LED1R ="/p1/led_rouge";
    private static final String NODE_LED1O ="/p1/led_orange";
    private static  final String NODE_LED1V="/p1/led_vert";
    private static final String NODE_LED2R ="/p2/led_rouge";
    private static final String NODE_LED2O ="/p2/led_orange";
    private static  final String NODE_LED2V="/p2/led_vert";
    private static final String NODE_STOP = "/stop";
    DatabaseReference mRefLed1R ;
    DatabaseReference mRefLed1O ;
    DatabaseReference mRefLed1V ;
    DatabaseReference mRefLed2R ;
    DatabaseReference mRefLed2O ;
    DatabaseReference mRefLed2V ;
    DatabaseReference onlineRef;
    DatabaseReference currentUserRef;
    DatabaseReference mRefStop;
    // Physical Pin #33 on Raspberry Pi3
    private Handler mHandler = new Handler();

    TextView info ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent i = new Intent(getApplicationContext(),ElectricityMonitorActivity.class);
        //startActivity(i);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRefLed1R = database.getReference(NODE_LED1R);
        mRefLed1O = database.getReference(NODE_LED1O);
        mRefLed1V = database.getReference(NODE_LED1V);
        mRefLed2R = database.getReference(NODE_LED2R);
        mRefLed2O = database.getReference(NODE_LED2O);
        mRefLed2V = database.getReference(NODE_LED2V);
        onlineRef = database.getReference().child(FIREBASE_INFO_CONNECTED);
        currentUserRef= database.getReference().child(FIREBASE_ONLINE_ENDPOINT);
        mRefStop = database.getReference(NODE_STOP);
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot:" + dataSnapshot);
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.setValue(true);
                    currentUserRef.onDisconnect().setValue(false);
                }
            }
            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.d(TAG, "DatabaseError:" + databaseError);
            }
        });
        mRefStop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot:" + dataSnapshot);
                stop = dataSnapshot.getValue(Boolean.class);

            }
            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.d(TAG, "DatabaseError:" + databaseError);
            }
        });


        info = findViewById(R.id.hello);
        // Step 1. Create GPIO connection.
        PeripheralManagerService service = new PeripheralManagerService();

        try {

            // Open Pins GPIO
            mLedGpio_1R = service.openGpio(GPIO_PIN_LED1_R);
            mLedGpio_1O = service.openGpio(GPIO_PIN_LED1_O);
            mLedGpio_1V = service.openGpio(GPIO_PIN_LED1_V);
            mLedGpio_2R = service.openGpio(GPIO_PIN_LED2_R);
            mLedGpio_2O = service.openGpio(GPIO_PIN_LED2_O);
            mLedGpio_2V = service.openGpio(GPIO_PIN_LED2_V);


            //Initial configuration as an output,initialisation en sortie de toutes les broches
            // on initialise toutes les LED éteintes au début du programme
            // (sauf les deux feux rouges)
            mLedGpio_1R.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpio_1R.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed1R.setValue(true);

            mLedGpio_1O.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio_1O.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed1O.setValue(false);

            mLedGpio_1V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio_1V.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed1V.setValue(false);

            mLedGpio_2R.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpio_2R.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed2R.setValue(true);

            mLedGpio_2O.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio_2O.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed2O.setValue(false);

            mLedGpio_2V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio_2V.setActiveType(Gpio.ACTIVE_HIGH);
            mRefLed2V.setValue(false);

            // Step 4. Repeat using a handler.
            //mHandler.post(mBlinkRunnable);
            mHandler.post(mFirewayRunnable);
        } catch (Exception e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Step 4. Remove handler events on close.
       // mHandler.removeCallbacks(mBlinkRunnable);
        mHandler.removeCallbacks(mFirewayRunnable);
        stop =false;
        // Step 5. Close the resource.
        if (mLedGpio_1R != null||
                mLedGpio_1V != null ||
                mLedGpio_1O != null || mLedGpio_2R != null||
                mLedGpio_2V != null|| mLedGpio_2O != null) {
            try {
               mLedGpio_1V.close();
               mLedGpio_1R.close();
               mLedGpio_1O.close();
               mLedGpio_2V.close();
               mLedGpio_2R.close();
               mLedGpio_2O.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }



    private Runnable mFirewayRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed


            if (mLedGpio_1R == null || mLedGpio_1O == null||
                    mLedGpio_1V == null|| mLedGpio_2R == null||
                    mLedGpio_2O == null|| mLedGpio_2V == null) {
                return;
            }
            try {
                    if(stop){
                        mLedGpio_1R.setValue(true);
                        mRefLed2R.setValue(true);
                        mRefLed1R.setValue(true);
                        mLedGpio_2R.setValue(true);
                        mRefLed1V.setValue(false);
                        mLedGpio_1V.setValue(false);
                        mRefLed1O.setValue(false);
                        mLedGpio_1O.setValue(false);
                        mRefLed2O.setValue(false);
                        mLedGpio_2O.setValue(false);
                        mRefLed2V.setValue(false);
                        mLedGpio_2V.setValue(false);
                        // Step 4. Schedule another event after delay.
                        mHandler.postDelayed(mSequence1, INTERVAL_LED_START);
                    }else {
                        // première séquence
                        //LED 1
                        mLedGpio_1R.setValue(false);
                        mRefLed1R.setValue(false);

                        mLedGpio_1O.setValue(false);
                        mRefLed1O.setValue(false);

                        mLedGpio_1V.setValue(true);
                        mRefLed1V.setValue(true);
                        //LED 2
                        mLedGpio_2R.setValue(true);
                        mRefLed2R.setValue(true);

                        mLedGpio_2O.setValue(false);
                        mRefLed2O.setValue(false);

                        mLedGpio_2V.setValue(false);
                        mRefLed2V.setValue(false);

                        // Step 4. Schedule another event after delay.
                        mHandler.postDelayed(mSequence1, INTERVAL_LED_START);
                    }

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private Runnable mSequence1 = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
            if (mLedGpio_1R == null || mLedGpio_1O == null||
                    mLedGpio_1V == null|| mLedGpio_2R == null||
                    mLedGpio_2O == null|| mLedGpio_2V == null) {
                return;
            }
            try {
                if(stop){
                    mLedGpio_1R.setValue(true);
                    mRefLed2R.setValue(true);
                    mRefLed1R.setValue(true);
                    mLedGpio_2R.setValue(true);
                    mRefLed1V.setValue(false);
                    mLedGpio_1V.setValue(false);
                    mRefLed1O.setValue(false);
                    mLedGpio_1O.setValue(false);
                    mRefLed2O.setValue(false);
                    mLedGpio_2O.setValue(false);
                    mRefLed2V.setValue(false);
                    mLedGpio_2V.setValue(false);
                    // Step 4. Schedule another event after delay.
                    mHandler.postDelayed(mSequence1, INTERVAL_LED_START);

                }else{
                    //LED 1
                    mLedGpio_1R.setValue(false);
                    mRefLed1R.setValue(false);

                    mLedGpio_1O.setValue(true);
                    mRefLed1O.setValue(true);

                    mLedGpio_1V.setValue(false);
                    mRefLed1V.setValue(false);
                    // Step 4. Schedule another event after delay.
                    mHandler.postDelayed(mSequence2, INTERVAL_LED_START_TEMP);
                }



            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private Runnable mSequence2 = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
            if (mLedGpio_1R == null || mLedGpio_1O == null||
                    mLedGpio_1V == null|| mLedGpio_2R == null||
                    mLedGpio_2O == null|| mLedGpio_2V == null) {
                return;
            }
            try {
                if(stop){
                    mLedGpio_1R.setValue(true);
                    mRefLed2R.setValue(true);
                    mRefLed1R.setValue(true);
                    mLedGpio_2R.setValue(true);
                    mRefLed1V.setValue(false);
                    mLedGpio_1V.setValue(false);
                    mRefLed1O.setValue(false);
                    mLedGpio_1O.setValue(false);
                    mRefLed2O.setValue(false);
                    mLedGpio_2O.setValue(false);
                    mRefLed2V.setValue(false);
                    mLedGpio_2V.setValue(false);
                    // Step 4. Schedule another event after delay.
                    mHandler.postDelayed(mSequence1, INTERVAL_LED_START);

                }else {
                    //LED 1
                    mLedGpio_1R.setValue(true);
                    mRefLed1R.setValue(true);

                    mLedGpio_1O.setValue(false);
                    mRefLed1O.setValue(false);

                    mLedGpio_1V.setValue(false);
                    mRefLed1V.setValue(false);
                    //LED 2
                    mLedGpio_2R.setValue(false);
                    mRefLed2R.setValue(false);

                    mLedGpio_2O.setValue(false);
                    mRefLed2O.setValue(false);

                    mLedGpio_2V.setValue(true);
                    mRefLed2V.setValue(true);
                    // Step 4. Schedule another event after delay.
                    mHandler.postDelayed(mSequence3, INTERVAL_LED_START);
                }

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private Runnable mSequence3 = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
            if (mLedGpio_1R == null || mLedGpio_1O == null||
                    mLedGpio_1V == null|| mLedGpio_2R == null||
                    mLedGpio_2O == null|| mLedGpio_2V == null) {
                return;
            }
            try {
                    if(stop){
                        mLedGpio_1R.setValue(true);
                        mRefLed2R.setValue(true);
                        mRefLed1R.setValue(true);
                        mLedGpio_2R.setValue(true);
                        mRefLed1V.setValue(false);
                        mLedGpio_1V.setValue(false);
                        mRefLed1O.setValue(false);
                        mLedGpio_1O.setValue(false);
                        mRefLed2O.setValue(false);
                        mLedGpio_2O.setValue(false);
                        mRefLed2V.setValue(false);
                        mLedGpio_2V.setValue(false);
                        // Step 4. Schedule another event after delay.
                        mHandler.postDelayed(mSequence1, INTERVAL_LED_START);
                    }else {
                        //LED 2
                        mLedGpio_2R.setValue(false);
                        mRefLed2R.setValue(false);

                        mLedGpio_2O.setValue(true);
                        mRefLed2O.setValue(true);

                        mLedGpio_2V.setValue(false);
                        mRefLed2V.setValue(false);
                        // Step 4. Schedule another event after delay.
                        mHandler.postDelayed(mFirewayRunnable, INTERVAL_LED_START_TEMP);
                    }


            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };





}
