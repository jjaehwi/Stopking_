package com.sample.stopking_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

public class Statistics extends AppCompatActivity implements ToolTipsManager.TipListener, View.OnClickListener {

    ImageButton tooltip;
    ToolTipsManager toolTipsManager;
    TextView textView;
    RelativeLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TabHost tabHost = findViewById(R.id.host); tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon1, null));
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon2, null));
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);

        tooltip = findViewById(R.id.tooltip);
        textView = findViewById(R.id.textView);
        linearLayout = findViewById(R.id.linearlayout);
        // initialize tooltip manager
        toolTipsManager = new ToolTipsManager(this);
        tooltip.setOnClickListener(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolTipsManager.dismissAll();
            }
        });


    }

    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
        // Check condition
        if(byUser) {
            // When user dismiss the tooltip
            // Display toast
            Toast.makeText(getApplicationContext(),"Dismissed",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view){
        // Check condition
                int position = ToolTip.POSITION_ABOVE;
                int align = ToolTip.ALIGN_LEFT;
                displayTooltip(position,align);
    }

    private void displayTooltip(int position, int align) {
        String message = "소주 한 병의 칼로리는 408kcal입니다.";
        toolTipsManager.findAndDismiss(textView);
        if(!message.isEmpty()) {
            ToolTip.Builder builder = new ToolTip.Builder(this, tooltip, linearLayout, message, position);
            builder.setAlign(align);
            builder.setBackgroundColor(Color.BLUE);
            toolTipsManager.show(builder.build());
        }else{
            Toast.makeText(getApplicationContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
        }
    }
}