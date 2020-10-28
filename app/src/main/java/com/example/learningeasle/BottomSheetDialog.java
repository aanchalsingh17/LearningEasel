package com.example.learningeasle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {


    private BottomSheetListener bottomSheetListener;
    TextView popularity,areaOfInterests,uploadDate,followers;
    private static BottomSheetDialog bottomSheetDialog=null;
    private int flag;

    private BottomSheetDialog(BottomSheetListener bottomSheetListener){
        this.bottomSheetListener=bottomSheetListener;
    }

    public static BottomSheetDialog getInstance(BottomSheetListener bottomSheetListener){
        if(bottomSheetDialog == null)
            bottomSheetDialog=new BottomSheetDialog(bottomSheetListener);
        return bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        popularity=view.findViewById(R.id.popularity);
        areaOfInterests=view.findViewById(R.id.areaOfInterests);
        uploadDate=view.findViewById(R.id.uploadDate);
        followers=view.findViewById(R.id.followers);

        if(flag == 1 ) {
            popularity.setTextColor(Color.parseColor("#0D141E"));
            popularity.setTypeface(uploadDate.getTypeface(), Typeface.BOLD);
        }
        if(flag == 2){
            areaOfInterests.setTextColor(Color.parseColor("#0D141E"));
            areaOfInterests.setTypeface(areaOfInterests.getTypeface(), Typeface.BOLD);
        }
        if(flag == 3 || flag == 0){
            uploadDate.setTextColor(Color.parseColor("#0D141E"));
            uploadDate.setTypeface(uploadDate.getTypeface(), Typeface.BOLD);
        }
        if(flag == 4){
            followers.setTextColor(Color.parseColor("#0D141E"));
            followers.setTypeface(followers.getTypeface(), Typeface.BOLD);
        }

        popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                bottomSheetListener.onTextSelected("pop");
                dismiss();
            }
        });

        areaOfInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=2;
                bottomSheetListener.onTextSelected("aoi");
                dismiss();
            }
        });

        uploadDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=3;
                bottomSheetListener.onTextSelected("upd");
                dismiss();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=4;
                bottomSheetListener.onTextSelected("fol");
                dismiss();
            }
        });
        return view;
    }

    public interface BottomSheetListener{
        void onTextSelected(String type);
    }


//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        try {
//            System.out.println(context.toString()+ " has implemented");
//            bottomSheetListener = (BottomSheetListener) context;
//            System.out.println(context.toString()+ " has implemented");
//
//        }catch (ClassCastException e){
//            System.out.println(context.toString()+ " must implement");
//        }
//    }
}
