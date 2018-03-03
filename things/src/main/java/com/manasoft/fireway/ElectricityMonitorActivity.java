package com.manasoft.fireway;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ElectricityMonitorActivity extends Activity{
    public static final String FIREBASE_LOGS="logs";
    private static final String TAG = "ElectricityMonitor";
    public static final String FIREBASE_ONLINE_ENDPOINT ="/online";
    public  static final String FIREBASE_INFO_CONNECTED=".info/connected";
    private DatabaseReference firebaseDatabase;
    private ElectricityLog electricityLog;
    private TextView affiche ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_monitor);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        final String key = firebaseDatabase.child(FIREBASE_LOGS).push().getKey();
        electricityLog = new ElectricityLog();
        final DatabaseReference onlineRef = firebaseDatabase.child(FIREBASE_INFO_CONNECTED);
        final DatabaseReference currentUserRef = firebaseDatabase.child(FIREBASE_ONLINE_ENDPOINT);
        affiche = findViewById(R.id.affiche);
        onlineRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"DatabaseError:"+dataSnapshot);
                affiche.setText(dataSnapshot.toString());
                if(dataSnapshot.getValue(Boolean.class)){
                    electricityLog.setTimestampOn(ServerValue.TIMESTAMP);
                    final DatabaseReference currentLogDbRef = firebaseDatabase.child(FIREBASE_LOGS).child(key);
                    currentLogDbRef.setValue(electricityLog);


                    currentUserRef.setValue(true);
                    currentUserRef.onDisconnect().setValue(false);
                    electricityLog.setTimestampOff(ServerValue.TIMESTAMP);
                    currentLogDbRef.onDisconnect().setValue(electricityLog);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"DatabaseError:"+databaseError);
            }
        });
    }
}
