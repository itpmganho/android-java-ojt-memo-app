package com.example.e01_memo.ui.main.views;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e01_memo.R;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class DeleteDetailDialogFragment extends DialogFragment {

    private static final String KEY_MEMO_TEXT = "key_memo_text";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_delete_detail_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(getActivity());
        SharedPreferencesUtil.MojiSizeSetting mojiSizeSetting = SharedPreferencesUtil.getSelectMojiSize(getActivity());
        View dialogBg = dialog.findViewById(R.id.dialogBackground);
        dialogBg.setBackgroundColor(ContextCompat.getColor(getActivity(), themeSetting.getBackgroundColorResId()));
        ImageView closeBtn = dialog.findViewById(R.id.closeButton);
        TextView memoText = dialog.findViewById(R.id.memoText);
        memoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mojiSizeSetting.getMojiSize());
        TextView mojiText = dialog.findViewById(R.id.mojiText);

        SharedPreferencesUtil.FontSetting fontSetting = SharedPreferencesUtil.getSelectFont(getActivity());
        Typeface typeface = ResourcesCompat.getFont(getActivity(), fontSetting.getFontTypeResId());
        memoText.setTypeface(typeface);
        mojiText.setTypeface(typeface);

        Bundle args = getArguments();
        String memoStr = args.getString(KEY_MEMO_TEXT);
        String moji = memoStr.replaceAll("\n", "");

        memoText.setText(memoStr);
        mojiText.setText(String.valueOf(moji.length()));

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public void setMemoItem(MemoItem item) {
        Bundle args = new Bundle();
        args.putString(KEY_MEMO_TEXT, item.getMemo());
        this.setArguments(args);
    }
}
