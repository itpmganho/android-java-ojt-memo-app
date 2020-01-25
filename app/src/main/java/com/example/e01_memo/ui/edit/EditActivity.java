package com.example.e01_memo.ui.edit;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e01_memo.R;
import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.usecase.MemoDataInteractorImpl;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.io.File;

public class EditActivity extends AppCompatActivity implements EditFragment.EditActionListener, View.OnClickListener {

    private static final int REQUEST_CODE = 1000;
    private static final int POPUP_TIME_OUT = 3000;
    private static final String KEY_MEMO_ID = "key_memo_id";
    private static final String KEY_MEMO_TEXT = "key_memo_text";
    private static final String KEY_MEMO_FAVORITE = "key_memo_favorite";

    private EditPresenterImpl presenter;
    private Toolbar toolbar;
    private Toast copiedToast;
    private AlertDialog restoreDialog;
    private PopupWindow popupMenu, popupComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_edit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        changeTextFont(SharedPreferencesUtil.getSelectFont(this));

        // データ取得
        int id = getIntent().getIntExtra(KEY_MEMO_ID, -1);
        boolean favorite = getIntent().getBooleanExtra(KEY_MEMO_FAVORITE, false);
        String memoStr = "";
        if (id != -1) {
            memoStr = getIntent().getStringExtra(KEY_MEMO_TEXT);
            getSupportActionBar().setTitle(memoStr);
        } else {
            getSupportActionBar().setTitle(getString(R.string.new_memo));
        }

        EditFragment fragment = EditFragment.newInstance(id, favorite);

        presenter = new EditPresenterImpl(fragment, memoStr, favorite, new MemoDataInteractorImpl(new MemoSQLiteOpenHelper(this)));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }

    @Override
    protected void onPause() {
        if (popupComplete != null) {
            popupComplete.dismiss();
            popupComplete = null;
        }
        super.onPause();
    }

    private void changeTextFont(SharedPreferencesUtil.FontSetting fontSetting) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    Typeface typeface = ResourcesCompat.getFont(this, fontSetting.getFontTypeResId());
                    tv.setTypeface(typeface);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                if (presenter != null) presenter.onTapShare();
                break;
            case R.id.action_menu:
                if (presenter != null) presenter.onTapMenu();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    public static Intent createIntent(Context context, int id, String memo, boolean favorite) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(KEY_MEMO_ID, id);
        intent.putExtra(KEY_MEMO_TEXT, memo);
        intent.putExtra(KEY_MEMO_FAVORITE, favorite);
        return intent;
    }

    @Override
    public void actionFinishPage(EditContract.FinishStatus status) {
        switch (status) {
            case OK:
                setResult(RESULT_OK);
                finish();
                break;
            case CANCEL:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void actionDisplayMenu(boolean show) {
        if (popupMenu != null) {
            popupMenu.dismiss();
            popupMenu = null;
        }
        View inputView = LayoutInflater.from(this).inflate(R.layout.layout_popup_menu, null);
        View copyButton = inputView.findViewById(R.id.copyButton);
        View restoreButton = inputView.findViewById(R.id.restoreButton);
        View saveAsImg = inputView.findViewById(R.id.saveAsImgButton);
        copyButton.setOnClickListener(this);
        restoreButton.setOnClickListener(this);
        saveAsImg.setOnClickListener(this);
        popupMenu = new PopupWindow(inputView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -4, getResources().getDisplayMetrics());

        popupMenu.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        popupMenu.setFocusable(true);

        popupMenu.setOutsideTouchable(true);

        popupMenu.update();

        popupMenu.showAsDropDown(toolbar, 0, size, Gravity.RIGHT);
    }

    @Override
    public void actionCopyToClipboard(String textStr) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) return;
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", textStr));

        // TOAST表示
        if (copiedToast != null) {
            copiedToast.cancel();
            copiedToast = null;
        }

        copiedToast = Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT);
        copiedToast.show();
    }

    @Override
    public void actionRequestWritePermission() {
        requestWritePermission();
    }

    @Override
    public void actionSaveAsImgComplete(final File file) {
        if (popupComplete != null) {
            popupComplete.dismiss();
            popupComplete = null;
        }
        View inputView = LayoutInflater.from(this).inflate(R.layout.layout_popup_complete, null);
        View openButton = inputView.findViewById(R.id.openButton);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupComplete != null) {
                    popupComplete.dismiss();
                    popupComplete = null;
                }
                Intent intentGallery = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                intentGallery.setDataAndType(uri, "image/*");
                intentGallery.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intentGallery);
            }
        });
        CardView cardView = inputView.findViewById(R.id.cardView);

        int colorResId = SharedPreferencesUtil.getSelectTheme(this).getFabColorResId();
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, colorResId));

        popupComplete = new PopupWindow(inputView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, getResources().getDisplayMetrics());

        popupComplete.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupComplete.setFocusable(true);

        popupComplete.setOutsideTouchable(true);

        popupComplete.setAnimationStyle(R.style.popup_window_animation);

        popupComplete.update();

        View view = findViewById(R.id.mainContainer);
        popupComplete.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, size);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (popupComplete != null) {
                    popupComplete.dismiss();
                    popupComplete = null;
                }
            }
        }, POPUP_TIME_OUT);
    }

    private void requestWritePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (presenter != null) presenter.onTapMenuItem(EditContract.PopupButton.SaveAsImgButton);
            } else {
                Toast toast = Toast.makeText(this, R.string.access_to_storage_message, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void showRestoreDialog(DialogInterface.OnClickListener listener) {
        if (restoreDialog != null) {
            restoreDialog.dismiss();
            restoreDialog = null;
        }
        restoreDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.restore_dialog_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, listener)
                .create();
        restoreDialog.show();
    }

    @Override
    public View getCurrentFocusView() {
        return getCurrentFocus();
    }

    @Override
    public void onBackPressed() {
        if (presenter != null) presenter.onUpdateMemo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (presenter == null) return;
        if (popupMenu != null) popupMenu.dismiss();
        if (view.getId() == R.id.copyButton) {
            presenter.onTapMenuItem(EditContract.PopupButton.CopyButton);
        }
        else if (view.getId() == R.id.restoreButton) {
            presenter.onTapMenuItem(EditContract.PopupButton.RestoreButton);
        }
        else if (view.getId() == R.id.saveAsImgButton) {
            presenter.onTapMenuItem(EditContract.PopupButton.SaveAsImgButton);
        }
    }
}
