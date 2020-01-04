package com.example.e01_memo.ui.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e01_memo.MyApplication;
import com.example.e01_memo.R;
import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.ui.edit.EditActivity;
import com.example.e01_memo.ui.main.views.DeleteDetailDialogFragment;
import com.example.e01_memo.ui.search.SearchActivity;
import com.example.e01_memo.ui.setting.SettingActivity;
import com.example.e01_memo.usecase.MemoDataInteractorImpl;
import com.example.e01_memo.util.Constant;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, MainFragment.MainActionListener {

    private static final int MOVETO_EDIT_REQUEST_CODE = 1001;

    // 並び替え設定用
    private int[] sortLayoutIds = {
            R.id.sortSettingNum0, R.id.sortSettingNum1
    };
    private int[] selectSortImgs = {
            R.id.selectSortImgNum0, R.id.selectSortImgNum1
    };

    private MainPresenterImpl presenter;
    private Toolbar toolbar;
    private LinearLayout navAllMemoView;
    private LinearLayout navFavoriteView;
    private LinearLayout navDeleteView;
    private ImageView navAllMemoImg;
    private ImageView navFavoriteImg;
    private ImageView navDeleteImg;
    private TextView navAllMemoText;
    private TextView navFavoriteText;
    private TextView navDeleteText;
    private DrawerLayout drawer;
    private LinearLayout deleteBar;
    private FloatingActionButton editButton;
    private Menu optionsMenu;
    private ImageView deleteModeClose;
    private TextView selectMemoCount;
    private ImageView deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(this);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.all_memo));

        MainFragment fragment = MainFragment.newInstance(themeSetting.ordinal());
        presenter = new MainPresenterImpl(fragment, new MemoDataInteractorImpl(new MemoSQLiteOpenHelper(this)));

        editButton = (FloatingActionButton) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter != null) presenter.onButton(MainContract.MainButton.EditButton, null);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                presenter.onOpenDrawer();
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.mipmap.ic_menu);

        deleteBar = findViewById(R.id.deleteBar);
        navAllMemoView = findViewById(R.id.navAllMemo);
        navFavoriteView = findViewById(R.id.navFavorite);
        navDeleteView = findViewById(R.id.navDelete);
        navAllMemoImg = findViewById(R.id.allMemoImg);
        navFavoriteImg = findViewById(R.id.favoriteImg);
        navDeleteImg = findViewById(R.id.deleteImg);
        navAllMemoText = findViewById(R.id.allMemoText);
        navFavoriteText = findViewById(R.id.favoriteText);
        navDeleteText = findViewById(R.id.deleteText);
        deleteModeClose = findViewById(R.id.deleteModeClose);
        selectMemoCount = findViewById(R.id.selectMemoCount);
        deleteButton = findViewById(R.id.deleteButton);

        changeTextFont(SharedPreferencesUtil.getSelectFont(this), navAllMemoText, navFavoriteText, navDeleteText, selectMemoCount);

        // 初期選択
        MyApplication.selectNavigationItem = Constant.NavigationItem.ALL_MEMO;
        navAllMemoView.setBackgroundResource(R.drawable.shape_item_selected);
        navAllMemoImg.setImageResource(R.mipmap.ic_nav_report_on);
        navAllMemoText.setTextColor(Color.WHITE);

        navAllMemoView.setOnClickListener(this);
        navFavoriteView.setOnClickListener(this);
        navDeleteView.setOnClickListener(this);

        deleteModeClose.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
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
    protected void onResume() {
        super.onResume();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (presenter.isDeleteMode()) {
                presenter.onBackDeleteMode();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_all_memo, menu);
        return true;
    }

    private void hideOptionsMenu() {
        if (optionsMenu != null) {
            optionsMenu.findItem(R.id.action_search).setVisible(false);
            optionsMenu.findItem(R.id.action_sort).setVisible(false);
            optionsMenu.findItem(R.id.action_settings).setVisible(false);
            optionsMenu.findItem(R.id.action_garbage_box).setVisible(false);
        }
    }

    private void showOptionsMenu() {
        if (optionsMenu != null) {
            optionsMenu.findItem(R.id.action_search).setVisible(true);
            optionsMenu.findItem(R.id.action_sort).setVisible(true);
            optionsMenu.findItem(R.id.action_settings).setVisible(true);
            optionsMenu.findItem(R.id.action_garbage_box).setVisible(false);
        }
    }

    private void showGarbageBoxOptionsMenu() {
        if (optionsMenu != null) {
            optionsMenu.findItem(R.id.action_search).setVisible(false);
            optionsMenu.findItem(R.id.action_sort).setVisible(false);
            optionsMenu.findItem(R.id.action_settings).setVisible(true);
            optionsMenu.findItem(R.id.action_garbage_box).setVisible(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (presenter != null) {
            if (view.getId() == R.id.navAllMemo) {
                presenter.onTapNavItem(MainContract.MainButton.NavAllMemo);
            }
            else if (view.getId() == R.id.navFavorite) {
                presenter.onTapNavItem(MainContract.MainButton.NavFavorite);
            }
            else if (view.getId() == R.id.navDelete) {
                presenter.onTapNavItem(MainContract.MainButton.NavGarbageBox);
            }
            else if (view.getId() == R.id.deleteModeClose) {
                presenter.onTapDeleteBar(MainContract.MainButton.DeleteModeClose);
            }
            else if (view.getId() == R.id.deleteButton) {
                if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                    presenter.onTapDeleteBar(MainContract.MainButton.RestoreButton);
                } else {
                    presenter.onTapDeleteBar(MainContract.MainButton.DeleteButton);
                }
            }
        }
    }

    @Override
    public void actionOpenDrawer(boolean open) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (open) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void actionSelectNavItem(int navigationItemNum) {
        navAllMemoView.setBackground(null);
        navFavoriteView.setBackground(null);
        navDeleteView.setBackground(null);
        navAllMemoImg.setImageResource(R.mipmap.ic_nav_report_off);
        navFavoriteImg.setImageResource(R.mipmap.ic_nav_favorite_off);
        navDeleteImg.setImageResource(R.mipmap.ic_nav_delete_off);
        navAllMemoText.setTextColor(Color.BLACK);
        navFavoriteText.setTextColor(Color.BLACK);
        navDeleteText.setTextColor(Color.BLACK);

        if (navigationItemNum == Constant.NavigationItem.FAVORITE) {
            showOptionsMenu();
            editButton.show();
            navFavoriteView.setBackgroundResource(R.drawable.shape_item_selected);
            navFavoriteImg.setImageResource(R.mipmap.ic_nav_favorite_on);
            navFavoriteText.setTextColor(Color.WHITE);
            deleteButton.setImageResource(R.mipmap.ic_nav_delete_on);
            // タイトル変更
            getSupportActionBar().setTitle(getString(R.string.favorite));
        }
        else if (navigationItemNum == Constant.NavigationItem.DELETE) {
            showGarbageBoxOptionsMenu();
            editButton.hide();
            navDeleteView.setBackgroundResource(R.drawable.shape_item_selected);
            navDeleteImg.setImageResource(R.mipmap.ic_nav_delete_on);
            navDeleteText.setTextColor(Color.WHITE);
            deleteButton.setImageResource(R.mipmap.ic_restore);
            // タイトル変更
            getSupportActionBar().setTitle(getString(R.string.garbage_box));
        }
        else if (navigationItemNum == Constant.NavigationItem.ALL_MEMO){
            showOptionsMenu();
            editButton.show();
            navAllMemoView.setBackgroundResource(R.drawable.shape_item_selected);
            navAllMemoImg.setImageResource(R.mipmap.ic_nav_report_on);
            navAllMemoText.setTextColor(Color.WHITE);
            deleteButton.setImageResource(R.mipmap.ic_nav_delete_on);
            // タイトル変更
            getSupportActionBar().setTitle(getString(R.string.all_memo));
        }
        presenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case R.id.action_settings:
                startActivity(SettingActivity.createIntent(this));
                finish();
                break;
            case R.id.action_sort:
                showSortDialog();
                break;
            case R.id.action_search:
                startActivity(SearchActivity.createIntent(this));
                break;
            case R.id.action_garbage_box:
                showDoEmptyGarbageBox();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    @Override
    public void actionMoveToEdit(MemoItem item) {
        if (item == null) {
            startActivityForResult(new Intent(MainActivity.this, EditActivity.class), MOVETO_EDIT_REQUEST_CODE);
        } else {
            Intent intent = EditActivity.createIntent(this, item.getId(), item.getMemo(), item.isFavorite());
            startActivityForResult(intent, MOVETO_EDIT_REQUEST_CODE);
        }
    }

    @Override
    public void actionShowDeleteBar() {
        hideOptionsMenu();
        deleteBar.setVisibility(View.VISIBLE);
        editButton.hide();
        TypedValue typedValue = new TypedValue();
        float actionBarHeigth = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeigth = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        PropertyValuesHolder moveDownHolder = PropertyValuesHolder.ofFloat("translationY", actionBarHeigth, 0f);
        ObjectAnimator navBarShowAnim = ObjectAnimator.ofPropertyValuesHolder(deleteBar, moveDownHolder);
        navBarShowAnim.start();
    }

    @Override
    public void actionHideDeleteBar() {
        showOptionsMenu();
        deleteBar.setVisibility(View.VISIBLE);
        editButton.show();
        TypedValue typedValue = new TypedValue();
        float actionBarHeigth = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeigth = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        PropertyValuesHolder moveDownHolder = PropertyValuesHolder.ofFloat("translationY",  0f, actionBarHeigth);
        ObjectAnimator navBarShowAnim = ObjectAnimator.ofPropertyValuesHolder(deleteBar, moveDownHolder);
        navBarShowAnim.start();
    }

    @Override
    public void actionDeleteBarGone() {
        deleteBar.setVisibility(View.GONE);
        if (MyApplication.selectNavigationItem != Constant.NavigationItem.DELETE) {
            editButton.show();
            showOptionsMenu();
        } else {
            editButton.hide();
            showGarbageBoxOptionsMenu();
        }
    }

    @Override
    public void actionChangeSelectMemoCount(int selectCount) {
        selectMemoCount.setText(getString(R.string.select_memo_count, selectCount));
    }

    @Override
    public void actionShowDeletedToast() {
        Toast.makeText(this, R.string.in_to_garbage_box, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void actionShowRestoreToast() {
        Toast.makeText(this, R.string.in_to_allmemo_box, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void actionShowDeleteMemoDetail(MemoItem item) {
        DeleteDetailDialogFragment deleteDetailDialogFragment = new DeleteDetailDialogFragment();
        deleteDetailDialogFragment.setMemoItem(item);
        deleteDetailDialogFragment.show(getSupportFragmentManager(), DeleteDetailDialogFragment.class.getSimpleName());
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

    private void showSortDialog() {

        final View inputView = LayoutInflater.from(this).inflate(R.layout.layout_sort_dialog, null);

        SharedPreferencesUtil.SortSetting selectSort = SharedPreferencesUtil.getSelectSort(this);

        for (int i = 0; i < selectSortImgs.length; i++) {
            ImageView selectSortImg = inputView.findViewById(selectSortImgs[i]);
            if (selectSort.ordinal() == i) {
                selectSortImg.setVisibility(View.VISIBLE);
            } else {
                selectSortImg.setVisibility(View.INVISIBLE);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setView(inputView)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(typedValue.data);

                TextView sortTitle = dialog.findViewById(R.id.sortTitleText);
                TextView sortNewTitle = dialog.findViewById(R.id.sortNewTitleText);
                TextView sortOldTitle = dialog.findViewById(R.id.sortOldTitleText);
                TextView closeButton = dialog.findViewById(R.id.closeButton);

                initTextFont(SharedPreferencesUtil.getSelectFont(MainActivity.this),
                        sortTitle, sortNewTitle, sortOldTitle, closeButton);
            }
        });

        for (int i = 0; i < sortLayoutIds.length; i++) {
            View mojiSizeView = inputView.findViewById(sortLayoutIds[i]);
            mojiSizeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < sortLayoutIds.length; i++) {
                        ImageView sortImg = inputView.findViewById(selectSortImgs[i]);
                        if (sortLayoutIds[i] == view.getId()) {
                            sortImg.setVisibility(View.VISIBLE);
                            SharedPreferencesUtil.saveSelectSort(MainActivity.this, SharedPreferencesUtil.SortSetting.values()[i]);
                        } else {
                            sortImg.setVisibility(View.INVISIBLE);
                        }
                        presenter.start();
                        dialog.dismiss();
                    }
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

    private void showDoEmptyGarbageBox() {
        int dialogStyleId = SharedPreferencesUtil.getSelectDialogStyleId(this);
        final AlertDialog dialog = new AlertDialog.Builder(this, dialogStyleId)
                .setMessage(R.string.do_empty_garbage_box)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (presenter != null) presenter.garbageRemoveAll();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                TextView message = dialog.findViewById(android.R.id.message);
                TextView positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                TextView negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                initTextFont(SharedPreferencesUtil.getSelectFont(MainActivity.this), message, positive, negative);
            }
        });
        dialog.show();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
