package com.example.e01_memo.ui.search;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e01_memo.R;
import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.ui.edit.EditActivity;
import com.example.e01_memo.ui.main.MainActivity;
import com.example.e01_memo.ui.setting.SettingFragment;
import com.example.e01_memo.usecase.MemoDataInteractorImpl;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class SearchActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, SearchFragment.SearchActionListener {

    private static final int MOVETO_EDIT_REQUEST_CODE = 1002;

    private SearchPresenterImpl presenter;
    private EditText searchText;
    private Toolbar toolbar;
    private LinearLayout deleteBar;
    private ImageView deleteModeClose;
    private TextView selectMemoCount;
    private ImageView deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(this);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SearchFragment fragment = SearchFragment.newInstance(themeSetting.ordinal());
        presenter = new SearchPresenterImpl(fragment, new MemoDataInteractorImpl(new MemoSQLiteOpenHelper(this)));

        deleteBar = findViewById(R.id.deleteBar);
        selectMemoCount = findViewById(R.id.selectMemoCount);
        deleteModeClose = findViewById(R.id.deleteModeClose);
        deleteButton = findViewById(R.id.deleteButton);

        changeTextFont(SharedPreferencesUtil.getSelectFont(this), selectMemoCount);

        deleteModeClose.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        searchText = findViewById(R.id.searchText);
        searchText.addTextChangedListener(this);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    if (presenter != null) presenter.onSearchItem(textView.getText().toString().trim());
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOVETO_EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeTextFont(SharedPreferencesUtil.FontSetting fontSetting, TextView... textViews) {
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
        initTextFont(fontSetting, textViews);
    }

    private void initTextFont(SharedPreferencesUtil.FontSetting fontSetting, TextView... textViews) {
        Typeface typeface = ResourcesCompat.getFont(this, fontSetting.getFontTypeResId());
        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
            case R.id.action_delete_search:
                deleteSearchText();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void deleteSearchText() {
        searchText.setText("");
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // nothing to do
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (presenter != null) presenter.onTextChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // nothing to do
    }

    @Override
    public void actionShowDeleteBar() {
        deleteBar.setVisibility(View.VISIBLE);
        TypedValue typedValue = new TypedValue();
        float actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        PropertyValuesHolder moveDownHolder = PropertyValuesHolder.ofFloat("translationY", actionBarHeight, 0f);
        ObjectAnimator navBarShowAnim = ObjectAnimator.ofPropertyValuesHolder(deleteBar, moveDownHolder);
        navBarShowAnim.start();
    }

    @Override
    public void actionHideDeleteBar() {
        deleteBar.setVisibility(View.VISIBLE);
        TypedValue typedValue = new TypedValue();
        float actionBarHeigth = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeigth = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        PropertyValuesHolder moveDownHolder = PropertyValuesHolder.ofFloat("translationY", 0f, actionBarHeigth);
        ObjectAnimator navBarShowAnim = ObjectAnimator.ofPropertyValuesHolder(deleteBar, moveDownHolder);
        navBarShowAnim.start();
    }

    @Override
    public void actionShowDeletedToast() {
        Toast.makeText(this, R.string.in_to_garbage_box, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void actionMoveToEdit(MemoItem item) {
        Intent intent = EditActivity.createIntent(this, item.getId(), item.getMemo(), item.isFavorite());
        startActivityForResult(intent, MOVETO_EDIT_REQUEST_CODE);
    }

    @Override
    public void actionDeleteBarGone() {
        deleteBar.setVisibility(View.GONE);
    }

    @Override
    public void actionChangeSelectMemoCount(int selectCount) {
        selectMemoCount.setText(getString(R.string.select_memo_count, selectCount));
    }

    @Override
    public void onClick(View view) {
        if (presenter != null) {
            if (view.getId() == R.id.deleteModeClose) {
                presenter.onTapDeleteBar(SearchContract.SearchButton.DeleteModeClose);
            } else if (view.getId() == R.id.deleteButton) {
                presenter.onTapDeleteBar(SearchContract.SearchButton.DeleteButton);
            }
        }
    }
}
