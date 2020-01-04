package com.example.e01_memo.ui.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e01_memo.R;
import com.example.e01_memo.ui.BaseFragment;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends BaseFragment implements View.OnClickListener, SettingContract.SettingView {
    // フォント設定用
    private int[] fontLayoutIds = {
        R.id.fontSettingNum0, R.id.fontSettingNum1, R.id.fontSettingNum2, R.id.fontSettingNum3
    };
    private int[] selectFontImgs = {
        R.id.selectFontImgNum0, R.id.selectFontImgNum1, R.id.selectFontImgNum2, R.id.selectFontImgNum3
    };

    // 文字サイズ設定用
    private int[] mojiSizeLayoutIds = {
        R.id.mojiSizeSettingNum0, R.id.mojiSizeSettingNum1, R.id.mojiSizeSettingNum2
    };
    private int[] selectMojiSizeImgs = {
        R.id.selectMojiSizeImgNum0, R.id.selectMojiSizeImgNum1, R.id.selectMojiSizeImgNum2
    };

    private TextView mojiSizeTitleText;
    private TextView editTitleText;
    private TextView generalTitleText;
    private TextView fontSetting;
    private TextView theme;
    private TextView aboutApp;
    private TextView mojiSizeText;
    private List<TextView> textViews = new ArrayList<>();
    private LinearLayout mojiSize;
    private SettingContract.SettingPresenter presenter;
    private SettingActionListener actionListener;

    interface SettingActionListener {
        void actionMoveToAboutApp();
        void actionMoveToTheme();
        void actionChangeTextFont();
    }

    public SettingFragment() {}

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            actionListener = (SettingActionListener)context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mojiSizeTitleText = view.findViewById(R.id.mojiSizeTitleText);
        generalTitleText = view.findViewById(R.id.generalTitleText);
        editTitleText = view.findViewById(R.id.editTitleText);
        fontSetting = view.findViewById(R.id.fontSetting);
        theme = view.findViewById(R.id.theme);
        aboutApp = view.findViewById(R.id.aboutApp);
        mojiSize = view.findViewById(R.id.mojiSize);
        mojiSizeText = view.findViewById(R.id.mojiSizeText);

        initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()),
                editTitleText,
                mojiSizeTitleText,
                generalTitleText,
                fontSetting,
                theme,
                aboutApp,
                mojiSizeText
        );

        SharedPreferencesUtil.MojiSizeSetting mojiSizeSetting = SharedPreferencesUtil.getSelectMojiSize(getActivity());
        mojiSizeText.setText(getString(mojiSizeSetting.getMojiNameResId()));

        fontSetting.setOnClickListener(this);
        theme.setOnClickListener(this);
        aboutApp.setOnClickListener(this);
        mojiSize.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (presenter == null) return;
        if (view.getId() == R.id.fontSetting) {
            presenter.onButton(SettingContract.SettingButton.Font);
        }
        else if (view.getId() == R.id.mojiSize) {
            presenter.onButton(SettingContract.SettingButton.MojiSize);
        }
        else if (view.getId() == R.id.theme) {
            presenter.onButton(SettingContract.SettingButton.Theme);
        }
        else if (view.getId() == R.id.aboutApp) {
            presenter.onButton(SettingContract.SettingButton.AboutApp);
        }
    }

    @Override
    public void setPresenter(SettingContract.SettingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showFontDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View inputView = layoutInflater.inflate(R.layout.layout_font_dialog, null);

        SharedPreferencesUtil.FontSetting selectFont = SharedPreferencesUtil.getSelectFont(getActivity());

        for (int i = 0; i < selectFontImgs.length; i++) {
            ImageView selectFontImg = inputView.findViewById(selectFontImgs[i]);
            if (selectFont.ordinal() == i) {
                selectFontImg.setVisibility(View.VISIBLE);
            } else {
                selectFontImg.setVisibility(View.INVISIBLE);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setView(inputView)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                TextView fontTitle = dialog.findViewById(R.id.fontTitleText);
                TextView closeButton = dialog.findViewById(R.id.closeButton);

                initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()), fontTitle, closeButton);
            }
        });

        for (int i = 0; i < fontLayoutIds.length; i++) {
            View fontView = inputView.findViewById(fontLayoutIds[i]);
            fontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < fontLayoutIds.length; i++) {
                        ImageView fontImg = inputView.findViewById(selectFontImgs[i]);
                        if (fontLayoutIds[i] == view.getId()) {
                            fontImg.setVisibility(View.VISIBLE);
                            SharedPreferencesUtil.saveSelectFont(getActivity(), SharedPreferencesUtil.FontSetting.values()[i]);
                        } else {
                            fontImg.setVisibility(View.INVISIBLE);
                        }
                    }
                    if (actionListener != null) actionListener.actionChangeTextFont();
                    initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()),
                            editTitleText,
                            mojiSizeTitleText,
                            generalTitleText,
                            fontSetting,
                            theme,
                            aboutApp,
                            mojiSizeText
                    );
                    dialog.dismiss();
                }
            });
        }

        inputView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showMojiSizeDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View inputView = layoutInflater.inflate(R.layout.layout_moji_size_dialog, null);

        SharedPreferencesUtil.MojiSizeSetting selectMojiSize = SharedPreferencesUtil.getSelectMojiSize(getActivity());

        for (int i = 0; i < selectMojiSizeImgs.length; i++) {
            ImageView selectMojiSizeImg = inputView.findViewById(selectMojiSizeImgs[i]);
            if (selectMojiSize.ordinal() == i) {
                selectMojiSizeImg.setVisibility(View.VISIBLE);
            } else {
                selectMojiSizeImg.setVisibility(View.INVISIBLE);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setView(inputView)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                TextView mojiSizeTitle = dialog.findViewById(R.id.mojiSizeTitle);
                TextView mojiSizeTitleSmall = dialog.findViewById(R.id.mojiSizeTitleSmall);
                TextView mojiSizeTitleDefault = dialog.findViewById(R.id.mojiSizeTitleDefault);
                TextView mojiSizeTitleBig = dialog.findViewById(R.id.mojiSizeTitleBig);
                TextView closeButton = dialog.findViewById(R.id.closeButton);

                initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()),
                        mojiSizeTitle,
                        mojiSizeTitleSmall,
                        mojiSizeTitleDefault,
                        mojiSizeTitleBig,
                        closeButton
                );
            }
        });

        for (int i = 0; i < mojiSizeLayoutIds.length; i++) {
            View mojiSizeView = inputView.findViewById(mojiSizeLayoutIds[i]);
            mojiSizeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < mojiSizeLayoutIds.length; i++) {
                        ImageView mojiSizeImg = inputView.findViewById(selectMojiSizeImgs[i]);
                        if (mojiSizeLayoutIds[i] == view.getId()) {
                            mojiSizeImg.setVisibility(View.VISIBLE);
                            SharedPreferencesUtil.MojiSizeSetting mojiSizeSetting = SharedPreferencesUtil.MojiSizeSetting.values()[i];
                            mojiSizeText.setText(getString(mojiSizeSetting.getMojiNameResId()));
                            SharedPreferencesUtil.saveSelectMojiSize(getActivity(), mojiSizeSetting);
                        } else {
                            mojiSizeImg.setVisibility(View.INVISIBLE);
                        }
                    }
                    dialog.dismiss();
                }
            });
        }

        inputView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void moveToAboutApp() {
        if (actionListener != null) actionListener.actionMoveToAboutApp();
    }

    @Override
    public void moveToTheme() {
        if (actionListener != null) actionListener.actionMoveToTheme();
    }
}
