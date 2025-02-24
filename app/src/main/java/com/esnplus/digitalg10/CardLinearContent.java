package com.esnplus.digitalg10;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class CardLinearContent extends LinearLayout {

    public CardLinearContent(Context context) {
        super(context);
        init(context,null);
    }
    public CardLinearContent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    public CardLinearContent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    private void init(Context context,@Nullable AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView;
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CardLinearContent);

        String text = typedArray.getString(R.styleable.CardLinearContent_text);
        int imgSrc = typedArray.getResourceId(R.styleable.CardLinearContent_src,-1);
        int orientation = typedArray.getInt(R.styleable.CardLinearContent_orientation, LinearLayout.VERTICAL);
        float textSize = typedArray.getDimension(R.styleable.CardLinearContent_textSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        typedArray.recycle();

        if (orientation ==  LinearLayout.VERTICAL){
            rootView = inflater.inflate(R.layout.card_vertical_content, this, true);
        }else {
            rootView = inflater.inflate(R.layout.card_horizontal_content, this, true);
        }

        TextView tv = rootView.findViewById(R.id.tv);
        ImageView iv = rootView.findViewById(R.id.iv);

        iv.setImageResource(imgSrc);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}