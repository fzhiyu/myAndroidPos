package com.cuse.myandroidpos.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.cuse.myandroidpos.R;
import com.cuse.myandroidpos.utils.SunmiPrintHelper;

/**
 * Example of printing text
 */
public class TextActivity extends BaseActivity {
    //Font usage variables
    private String testFont;
    private boolean isBold, isUnderLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        setMyTitle(R.string.text_title);
        setBack();

        testFont = null;
        isBold = true;
        isUnderLine = true;
    }

    public void onClick(View view) {
        String content = "mEditText.getText().toString()";

//        float size = Integer.parseInt(mTextView2.getText().toString());
        float size = 24;

        SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine, testFont);
        SunmiPrintHelper.getInstance().feedPaper();
    }
}
