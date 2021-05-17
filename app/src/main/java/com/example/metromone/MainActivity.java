package com.example.metromone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ImageButton start;
    EditText editText;
    MediaPlayer high_woodblock,low_woodblock,high_bongo,low_bongo,claves,drumsticks;
    Handler handler;
    Timer myTimer,anotherTimer1,anotherTimer2,anotherTimer3,anotherTimer4,anotherTimer5,anotherTimer6;
    int count=0;
    Spinner spinner;
    NumberPicker numberPicker;
    LinearLayout linearLayout;
    SharedPreferences LastSelect;
    SharedPreferences.Editor editor;
    ImageView dot1,dot2,dot3,dot4,dot5,dot6;
    int ele1=1,ele2=0,ele3=0,ele4=0,ele5=0,ele6=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LastSelect=getSharedPreferences("LastSetting", Context.MODE_PRIVATE);
        editor=LastSelect.edit();

        final int LastClickSpinner=LastSelect.getInt("LastClickSpinner",0);

        start=findViewById(R.id.start);
        editText=findViewById(R.id.etText);
        spinner=findViewById(R.id.spinner_choice);
        numberPicker=findViewById(R.id.number_picker);
        linearLayout=findViewById(R.id.linearLayout);
        dot1=findViewById(R.id.dot1);
        dot2=findViewById(R.id.dot2);
        dot3=findViewById(R.id.dot3);
        dot4=findViewById(R.id.dot4);
        dot5=findViewById(R.id.dot5);
        dot6=findViewById(R.id.dot6);
        handler = new Handler();

        high_woodblock=MediaPlayer.create(MainActivity.this,R.raw.high_woodblock);
        low_woodblock=MediaPlayer.create(this,R.raw.low_woodblock);
        high_bongo=MediaPlayer.create(this,R.raw.high_bongo);
        low_bongo=MediaPlayer.create(this,R.raw.low_bongo);
        claves=MediaPlayer.create(this,R.raw.claves);
        drumsticks=MediaPlayer.create(this,R.raw.drumsticks);
        linearLayout.setVisibility(View.INVISIBLE);

        ArrayAdapter adapter=ArrayAdapter.createFromResource(this,R.array.choice,android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
        spinner.setSelection(LastClickSpinner);
        spinner.setOnItemSelectedListener(this);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the beat
                if(count==0) {
                    if ((!editText.getText().toString().trim().isEmpty())&&
                            Integer.parseInt(editText.getText().toString().trim()) >= 1 &&
                            Integer.parseInt(editText.getText().toString().trim()) <= 300) {
                        spinner.setEnabled(false);
                        editText.setEnabled(false);
                        numberPicker.setEnabled(false);
                        count=1;
                        linearLayout.setVisibility(View.VISIBLE);
                        musicPlay();
                        //number picker selection
                        numberPickerSelection();
                    } else {
                        Toast.makeText(MainActivity.this, "please enter a number within 1 to 300", Toast.LENGTH_SHORT).show();
                    }
                }
                //stop the beat
                else if(count==1){
                    count=0;
                    spinner.setEnabled(true);
                    editText.setEnabled(true);
                    numberPicker.setEnabled(true);
                    linearLayout.setVisibility(View.INVISIBLE);
                    start.setBackground(getResources().getDrawable(R.drawable.play));
                    myTimer.cancel();
                    anotherTimer1.cancel();
                    anotherTimer2.cancel();
                    anotherTimer3.cancel();
                    anotherTimer4.cancel();
                    anotherTimer5.cancel();
                    anotherTimer6.cancel();

                    musicPause();
                }
            }
        });


        String[] choices={"4","5","6","1","2","3"};
        numberPicker.setDisplayedValues(choices);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(5);

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position==0){
            ele1=1;
            ele2=0;ele3=0;ele4=0;ele5=0;ele6=0;

        }
        else if(position==1){
            ele2=1;
            ele1=0;ele3=0;ele4=0;ele5=0;ele6=0;

        }
        else if(position==2){
            ele3=1;
            ele1=0;ele2=0;ele4=0;ele5=0;ele6=0;
        }
        else if(position==3){
            ele4=1;
            ele2=0;ele3=0;ele1=0;ele5=0;ele6=0;
        }
        else if(position==4){
            ele5=1;
            ele2=0;ele3=0;ele4=0;ele1=0;ele6=0;
        }
        else if(position==5){
            ele6=1;
            ele2=0;ele3=0;ele4=0;ele5=0;ele1=0;
        }
        editor.putInt("LastClickSpinner",position).commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    //Spinner codes
    void musicPlay(){
        if(ele1==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            high_woodblock.start();
                        }
                    });

                }
            },0,  period);
        }
        else if(ele2==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            low_woodblock.start();
                        }
                    });

                }
            },0,  period);
        }
        else if(ele3==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            high_bongo.start();
                        }
                    });

                }
            },0,  period);
        }
        else if(ele4==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            low_bongo.start();
                        }
                    });

                }
            },0,  period);
        }
        else if(ele5==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            claves.start();
                        }
                    });

                }
            },0,  period);
        }
        else if(ele6==1){
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicPause();
                            drumsticks.start();
                        }
                    });

                }
            },0,  period);
        }

    }
    void musicPause(){
        if(ele1==1) {
//            high_woodblock.pause();
            if(low_woodblock.isPlaying()) {
                low_woodblock.pause();
            }
            if(high_bongo.isPlaying()) {
                high_bongo.pause();
            }
            if(low_bongo.isPlaying()) {
                low_bongo.pause();
            }
            if(claves.isPlaying()) {
                claves.pause();
            }
            if(drumsticks.isPlaying()) {
                drumsticks.pause();
            }

        }
        else if(ele2==1){
            if(high_woodblock.isPlaying()) {
                high_woodblock.pause();
            }
//            low_woodblock.pause();
            if(high_bongo.isPlaying()) {
                high_bongo.pause();
            }
            if(low_bongo.isPlaying()) {
                low_bongo.pause();
            }
            if(claves.isPlaying()) {
                claves.pause();
            }
            if(drumsticks.isPlaying()) {
                drumsticks.pause();
            }

        }
        else if(ele3==1){
            if(high_woodblock.isPlaying()) {
                high_woodblock.pause();
            }
            if(low_woodblock.isPlaying()) {
                low_woodblock.pause();
            }
//            high_bongo.pause();
            if(low_bongo.isPlaying()) {
                low_bongo.pause();
            }
            if(claves.isPlaying()) {
                claves.pause();
            }
            if(drumsticks.isPlaying()) {
                drumsticks.pause();
            }
        }
        else if(ele4==1){
            if(high_woodblock.isPlaying()) {
                high_woodblock.pause();
            }
            if(low_woodblock.isPlaying()) {
                low_woodblock.pause();
            }
            if(high_bongo.isPlaying()) {
                high_bongo.pause();
            }
//            low_bongo.pause();
            if(claves.isPlaying()) {
                claves.pause();
            }
            if(drumsticks.isPlaying()) {
                drumsticks.pause();
            }
        }
        else if(ele5==1){
            if(high_woodblock.isPlaying()) {
                high_woodblock.pause();
            }
            if(low_woodblock.isPlaying()) {
                low_woodblock.pause();
            }
            if(high_bongo.isPlaying()) {
                high_bongo.pause();
            }
            if(low_bongo.isPlaying()) {
                low_bongo.pause();
            }
//            claves.pause();
            if(drumsticks.isPlaying()) {
                drumsticks.pause();
            }
        }
        else if(ele6==1){
            if(high_woodblock.isPlaying()) {
                high_woodblock.pause();
            }
            if(low_woodblock.isPlaying()) {
                low_woodblock.pause();
            }
            if(high_bongo.isPlaying()) {
                high_bongo.pause();
            }
            if(low_bongo.isPlaying()) {
                low_bongo.pause();
            }
            if(claves.isPlaying()) {
                claves.pause();
            }
//            drumsticks.pause();
        }
    }

    //numberPicker select
    private void numberPickerSelection() {
        int choice=numberPicker.getValue();
        anotherTimer1=new Timer();
        anotherTimer2=new Timer();
        anotherTimer3=new Timer();
        anotherTimer4=new Timer();
        anotherTimer5=new Timer();
        anotherTimer6=new Timer();
        if(choice==0){
            dot1.setVisibility(View.VISIBLE);
            dot3.setVisibility(View.VISIBLE);
            dot4.setVisibility(View.VISIBLE);
            dot6.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.INVISIBLE);
            dot5.setVisibility(View.INVISIBLE);
            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer2.cancel();
            anotherTimer5.cancel();
            anotherTimer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot1.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },0,  4*period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot3.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period,  4*period);
            anotherTimer4.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot4.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },2*period,  4*period);
            anotherTimer6.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot6.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot1.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },3*period,  4*period);

        }
        else if(choice==1){
            dot1.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.VISIBLE);
            dot3.setVisibility(View.VISIBLE);
            dot4.setVisibility(View.VISIBLE);
            dot6.setVisibility(View.VISIBLE);
            dot5.setVisibility(View.INVISIBLE);

            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer5.cancel();
            anotherTimer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot1.setImageResource(R.drawable.big_dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },0,  5*period);
            anotherTimer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot2.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period,  5*period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot3.setImageResource(R.drawable.big_dot);

                            dot1.setImageResource(R.drawable.dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },2*period,  5*period);
            anotherTimer4.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot4.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);

                        }
                    });

                }
            },3*period,  5*period);
            anotherTimer6.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot6.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);

                        }
                    });

                }
            },4*period,  5*period);

        }
        else if(choice==2){
            dot1.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.VISIBLE);
            dot3.setVisibility(View.VISIBLE);
            dot4.setVisibility(View.VISIBLE);
            dot5.setVisibility(View.VISIBLE);
            dot6.setVisibility(View.VISIBLE);


            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot1.setImageResource(R.drawable.big_dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },0,  6*period);
            anotherTimer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot2.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period,  6*period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot3.setImageResource(R.drawable.big_dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },2*period,  6*period);
            anotherTimer4.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot4.setImageResource(R.drawable.big_dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },3*period,  6*period);
            anotherTimer5.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot5.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot6.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },4*period,  6*period);

            anotherTimer6.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot6.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot2.setImageResource(R.drawable.dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot4.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },5*period,  6*period);

        }
        else if(choice==3){
            dot2.setVisibility(View.VISIBLE);
            dot1.setVisibility(View.INVISIBLE);
            dot3.setVisibility(View.INVISIBLE);
            dot4.setVisibility(View.INVISIBLE);
            dot5.setVisibility(View.INVISIBLE);
            dot6.setVisibility(View.INVISIBLE);

            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer1.cancel();
            anotherTimer4.cancel();
            anotherTimer5.cancel();
            anotherTimer6.cancel();
            anotherTimer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot2.setImageResource(R.drawable.big_dot);
                        }
                    });

                }
            },0,  period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot2.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period/2,  period);

        }
        else if(choice==4){
            dot1.setVisibility(View.VISIBLE);
            dot3.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.INVISIBLE);
            dot4.setVisibility(View.INVISIBLE);
            dot5.setVisibility(View.INVISIBLE);
            dot6.setVisibility(View.INVISIBLE);

            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer2.cancel();
            anotherTimer4.cancel();
            anotherTimer5.cancel();
            anotherTimer6.cancel();
            anotherTimer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot1.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },0,  2*period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot3.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period,  2*period);


        }
        else if(choice==5){
            dot1.setVisibility(View.VISIBLE);
            dot3.setVisibility(View.VISIBLE);
            dot5.setVisibility(View.VISIBLE);
            dot2.setVisibility(View.INVISIBLE);
            dot4.setVisibility(View.INVISIBLE);
            dot6.setVisibility(View.INVISIBLE);

            long period = 60000 / Long.parseLong(editText.getText().toString().trim());
            anotherTimer2.cancel();
            anotherTimer4.cancel();
            anotherTimer6.cancel();
            anotherTimer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot1.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },0,  3*period);
            anotherTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot3.setImageResource(R.drawable.big_dot);
                            dot1.setImageResource(R.drawable.dot);
                            dot5.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },period,  3*period);
            anotherTimer5.schedule(new TimerTask() {
                @Override
                public void run() {
                    //execute something here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dot5.setImageResource(R.drawable.big_dot);
                            dot3.setImageResource(R.drawable.dot);
                            dot1.setImageResource(R.drawable.dot);
                        }
                    });

                }
            },2*period,  3*period);

        }
    }


}