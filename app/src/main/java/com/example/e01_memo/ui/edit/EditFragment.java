package com.example.e01_memo.ui.edit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e01_memo.R;
import com.example.e01_memo.ui.BaseFragment;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;

public class EditFragment extends BaseFragment implements EditContract.EditView, TextWatcher {

    private static final String KEY_ARGS_MEMO_ID = "key_args_memo_id";
    private static final String KEY_ARGS_MEMO_FAVORITE = "key_args_memo_favorite";
    private static final String SCREENSHOT_DIR= "CuteNotepad";

    private EditContract.EditPresenter presenter;
    private EditText editText;
    private TextView textView;
    private ImageView favoriteImg;
    private TextView mojiText;
    private InputMethodManager inputMethodManager;
    private EditActionListener editActionListener;
    private int selectId = -1;
    private boolean selectFavorite = false;

    public interface EditActionListener {
        void actionFinishPage(EditContract.FinishStatus status);
        void actionDisplayMenu(boolean show);
        void actionCopyToClipboard(String textStr);
        void actionRequestWritePermission();
        void actionSaveAsImgComplete(File file);
        void showRestoreDialog(DialogInterface.OnClickListener listener);
    }

    public EditFragment() {}

    public static EditFragment newInstance(int id, boolean favorite) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ARGS_MEMO_ID, id);
        args.putBoolean(KEY_ARGS_MEMO_FAVORITE, favorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            editActionListener = (EditActionListener)context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectId = getArguments().getInt(KEY_ARGS_MEMO_ID);
        selectFavorite = getArguments().getBoolean(KEY_ARGS_MEMO_FAVORITE);
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.textView);
        editText = view.findViewById(R.id.editText);
        favoriteImg = view.findViewById(R.id.favoriteImg);
        mojiText = view.findViewById(R.id.mojiText);
        int resId = SharedPreferencesUtil.getSelectTheme(getActivity()).getBackgroundColorResId();
        textView.setBackgroundResource(resId);
        editText.setBackgroundResource(resId);
        SharedPreferencesUtil.MojiSizeSetting mojiSizeSetting = SharedPreferencesUtil.getSelectMojiSize(getActivity());
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mojiSizeSetting.getMojiSize());

        initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()), editText, mojiText);

        String memoStr = presenter.getMemoStr();
        editText.setText(memoStr);
        String moji = memoStr.replaceAll("\n", "");
        mojiText.setText(String.valueOf(moji.length()));
        if (selectFavorite) {
            favoriteImg.setImageResource(R.mipmap.ic_favorite_on);
        } else {
            favoriteImg.setImageResource(R.mipmap.ic_favorite_off);
        }
        // お気に入りボタンタップ処理
        favoriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectFavorite) {
                    favoriteImg.setImageResource(R.mipmap.ic_favorite_off);
                    selectFavorite = false;
                } else {
                    favoriteImg.setImageResource(R.mipmap.ic_favorite_on);
                    selectFavorite = true;
                }
            }
        });
        editText.addTextChangedListener(this);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onResume() {
        if (selectId == -1)  {
            showSoftKeyboard();
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        hideSoftKeyboard();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(EditContract.EditPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public int getMemoId() {
        return this.selectId;
    }

    @Override
    public String readMemoText() {
        return editText.getText().toString();
    }

    @Override
    public void finishPage(EditContract.FinishStatus status) {
        hideSoftKeyboard();
        if (editActionListener != null) {
            editActionListener.actionFinishPage(status);
        }
    }

    @Override
    public boolean getFavorite() {
        return this.selectFavorite;
    }

    @Override
    public void shareText() {
        String memoStr = editText.getText().toString().trim();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, memoStr);
        startActivity(sharingIntent);
    }

    @Override
    public void showMenu() {
        if (editActionListener != null) editActionListener.actionDisplayMenu(true);
    }

    @Override
    public void restoreMemo(String memoStr) {
        if (editActionListener != null) editActionListener.showRestoreDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (presenter != null) editText.setText(presenter.getMemoStr());
            }
        });
    }

    @Override
    public void copyToClipboard() {
        if (editActionListener != null) editActionListener.actionCopyToClipboard(editText.getText().toString());
    }

    /*****************************************************
     * 画像として保存処理
     ****************************************************/
    @Override
    public void saveAsImage() {
        // パーミッションチェック
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveCapture();
            }
            else {
                if (editActionListener != null) editActionListener.actionRequestWritePermission();
            }
        } else {
            saveCapture();
        }
    }

    private Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);

        // Viewのキャッシュを取得
        Bitmap cache = view.getDrawingCache();
        Bitmap screenShot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        return screenShot;
    }

    private void saveCapture() {
        String fileName = "screenshot_" + System.currentTimeMillis() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + "/" + SCREENSHOT_DIR + "/" + fileName);
        file.getParentFile().mkdirs();
        // キャプチャを撮る
        textView.setText(editText.getText().toString());
        Bitmap capture = getViewCapture(textView);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            // 画像のフォーマットと画質を出力先を指定して保存
            capture.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            if (editActionListener != null) editActionListener.actionSaveAsImgComplete(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showSoftKeyboard() {
        editText.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /***************************************************
    * TextWatcher メソッド
     **************************************************/
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // nothing to do
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // FIXME: 入力された文字列に合わせて文字数を算出し表示する処理の実装。
        mojiText.setText("0");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // nothing to do
    }
}
