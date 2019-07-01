package com.example.z52song.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private TextView tipsTextView;
    private SeekBar percentSeekBar;
    private Button calButton;

    private Switch taxSwitch;
    private ToggleButton detailToggle;
    private Button confirmButton;
    private TextView resultTextView;

    private RadioGroup rGroup;
    private RadioButton radioButton;
    private String curType;
    private double rateCtoU = 0.759;

    private double rateCtoR = 5.215;
    private double taxRate = 0.13;
    private AlertDialog.Builder confirmDialog;


    public void showChangeToast(String content){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, content, duration);
        toast.show();
    }


    public double docaltips(double subtotal, double tiprate, double taxrate, boolean withtax){
        if(withtax){
            double reswh = subtotal * (1 + taxrate) + subtotal * tiprate;
            return reswh;
        } else {
            double reswo = subtotal * (1 + tiprate);
            return reswo;
        }
    }


    public double changeCurrency(String targetCur, double amount){
        if(targetCur == "USD") {
            return amount * rateCtoU;
        } else if(targetCur == "RMB"){
            return amount * rateCtoR;
        } else {
            return amount;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //连接各部件名字
        nameEditText = (EditText) findViewById(R.id.subtotalPT);
        tipsTextView = (TextView) findViewById(R.id.tipTV);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSB);
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);

        calButton = (Button) findViewById(R.id.calBU);
        taxSwitch = (Switch) findViewById(R.id.taxSW);
        detailToggle = (ToggleButton) findViewById(R.id.detailTG);
        confirmButton = (Button) findViewById(R.id.confirmBU);

        resultTextView = (TextView) findViewById(R.id.resTV);

        tipsTextView.setText("Tips rate: " + percentSeekBar.getProgress() + "%");
        percentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tipsTextView.setText("Tips rate: " + percentSeekBar.getProgress() + "%");
                Log.d("seekbar change", "to " + percentSeekBar.getProgress() / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Log.d("seekbar start", "to " + percentSeekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.d("seekbar stop", "to " + percentSeekBar.getProgress());
            }
        });
        curType = "CAD";

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = (RadioButton) findViewById(i);
                switch (radioButton.getId()){
                    case R.id.cadRB: {
                        curType = "CAD";
                        Log.d("curency is: ", curType);
                        break;
                    }
                    case R.id.usdRB: {
                        curType = "USD";
                        Log.d("curency is: ", curType);
                        break;
                    }
                    case R.id.rmbRB: {
                        curType = "RMB";
                        Log.d("curency is: ", curType);
                        break;
                    }
                }
            }
        });

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double subtotaldou = Double.parseDouble(nameEditText.getText().toString());
                double tipsRate = percentSeekBar.getProgress() / 100.0;
                boolean taxBool = taxSwitch.isChecked();

                StringBuilder strTO = new StringBuilder();
                strTO.append("1. subtotal: ");
                strTO.append(subtotaldou + " CAD\n");
                strTO.append("2. tips rate: " + (tipsRate*100.0) + "% tips\n");
                if(taxBool){
                    strTO.append("3. with " + (taxRate*100.0) +"% tax\n");
                } else {
                    strTO.append("3. WITHOUT tax\n");
                }
                strTO.append("4. in " + curType);
                showChangeToast(strTO.toString());

                double overallTotal = docaltips(subtotaldou,tipsRate,taxRate,taxBool);
                double displayTotal = Math.ceil(changeCurrency(curType,overallTotal) * 100.0) / 100.0;

                StringBuilder strRB = new StringBuilder();
                strRB.append("Your total is: \n");
                if(curType == "USD") {
                    strRB.append(displayTotal + " in USD");
                    resultTextView.setText(strRB.toString());
                } else if(curType == "RMB") {
                    strRB.append(displayTotal + " in RMB");
                    resultTextView.setText(strRB.toString());
                } else {
                    strRB.append(displayTotal + " in CAD");
                    resultTextView.setText(strRB.toString());
                }
            }
        });

        detailToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Log.d("toggle", "detail");
                    resultTextView.setText("Your total is:\nDETAIL FORM");
                } else {
                    Log.d("toggle", "total");
                    resultTextView.setText("Your total is:\nOVERALL FORM");
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create the dialog
                confirmDialog = new AlertDialog.Builder(MainActivity.this);

                //setup everything
                confirmDialog.setCancelable(true);
                confirmDialog.setTitle("Confirmation");
                confirmDialog.setMessage("are you sure about the amount?");
                confirmDialog.setPositiveButton("Pay now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("confirmation", "go to payment activity");
                        showChangeToast("Confirmed, go to payment activity");
                    }
                });
                confirmDialog.setNegativeButton("Double check", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("confirmation", "need to double check");
                        showChangeToast("Come back for double check");
                        dialogInterface.cancel();
                    }
                });
                confirmDialog.show();
            }
        });
    }
}
