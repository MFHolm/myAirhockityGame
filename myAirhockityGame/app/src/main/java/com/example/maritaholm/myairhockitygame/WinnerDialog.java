package com.example.maritaholm.myairhockitygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by MThomsen on 22-06-2015.
 */
public class WinnerDialog extends DialogFragment {
    public static WinnerDialog newInstance(String winner){
        WinnerDialog wd = new WinnerDialog();
        Bundle info = new Bundle();
        info.putString("winner",winner);
        wd.setArguments(info);
        return wd;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String winner = getArguments().getString("winner");


        Dialog AD = new AlertDialog.Builder(getActivity())
                .setTitle("The winner is " + winner + "!")
                .setPositiveButton("Return to main menu!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getActivity().finish();
                            }
                        }

                )

                .setNegativeButton("Play again!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                startActivity(intent);
                            }
                        }
                )
                .create();
        AD.setCanceledOnTouchOutside(false);
        return AD;
    }}

