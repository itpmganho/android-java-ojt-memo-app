package com.example.e01_memo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.e01_memo.R;

public class SharedPreferencesUtil {

    private static final String APP_MEMORY = "app_memory";
    // 選択中のフォント
    private static final String SELECT_FONT = "select_font";
    // 選択中の文字サイズ
    private static final String SELECT_MOJI_SIZE = "select_moji_size";
    // 選択中の並び順
    private static final String SELECT_SORT = "select_sort";
    // 選択中のテーマ
    private static final String SELECT_THEME = "select_theme";

    private static final int DEFAULT_FONT_NUM = 0;
    private static final int DEFAULT_MOJI_SIZE_NUM = 1;
    private static final int DEFAULT_SORT_NUM = 0;
    private static final int DEFAULT_THEME = 0;

    public enum FontSetting {
        SystemFont(R.font.roboto_medium), FontNum1(R.font.nagomi_gokuboso), FontNum2(R.font.roboto_light_italic), FontNum3(R.font.roboto_bold);
        int fontTypeResId;
        FontSetting(int fontType) {
            fontTypeResId = fontType;
        }
        public int getFontTypeResId() {
            return fontTypeResId;
        }
    }

    public enum MojiSizeSetting {
        MojiSmall(R.string.small, 8), MojiDefault(R.string.defaultStr, 16), MojiBig(R.string.big, 20);
        int mojiSize;
        int mojiNameResId;
        MojiSizeSetting(int nameResId, int size) {
            mojiNameResId = nameResId;
            mojiSize = size;
        }
        public int getMojiSize() {
            return mojiSize;
        }
        public int getMojiNameResId() {
            return mojiNameResId;
        }
    }

    public enum SortSetting {
        SortNew, SortOld
    }

    public enum ThemeSetting {
        Default("MemoTheme.Default", R.color.colorStatusBar1, R.color.colorHeaderBar1, R.color.colorThemeBg1, R.color.colorFabBg1, R.color.colorEditBottomBar1),
        First("MemoTheme.First", R.color.colorStatusBar2, R.color.colorHeaderBar2, R.color.colorThemeBg2, R.color.colorFabBg2, R.color.colorEditBottomBar2),
        Second("MemoTheme.Second", R.color.colorStatusBar3, R.color.colorHeaderBar3, R.color.colorThemeBg3, R.color.colorFabBg3, R.color.colorEditBottomBar3),
        Third("MemoTheme.Third", R.color.colorStatusBar4, R.color.colorHeaderBar4, R.color.colorThemeBg4, R.color.colorFabBg4, R.color.colorEditBottomBar4),
        Fourth("MemoTheme.Fourth", R.color.colorStatusBar5, R.color.colorHeaderBar5, R.color.colorThemeBg5, R.color.colorFabBg5, R.color.colorEditBottomBar5),
        Fifth("MemoTheme.Fifth", R.color.colorStatusBar6, R.color.colorHeaderBar6, R.color.colorThemeBg6, R.color.colorFabBg6, R.color.colorEditBottomBar6),
        Sixth("MemoTheme.Sixth", R.color.colorStatusBar7, R.color.colorHeaderBar7, R.color.colorThemeBg7, R.color.colorFabBg7, R.color.colorEditBottomBar7),
        Seventh("MemoTheme.Seventh", R.color.colorStatusBar8, R.color.colorHeaderBar8, R.color.colorThemeBg8, R.color.colorFabBg8, R.color.colorEditBottomBar8);

        private String themeName;
        private String dialogName;
        private int statusColorResId;
        private int headerColorResId;
        private int backgroundColorResId;
        private int fabColorResId;
        private int editBottomColorResId;
        private String selectDeleteItemShapeName;

        ThemeSetting(String name, int statusColor, int headerColor, int backgroundColor, int fabColor, int editBottomColor) {
            themeName = name;
            dialogName = "DeleteAlertDialogTheme." + name();
            selectDeleteItemShapeName = "shape_select_delete_item_" + name().toLowerCase();
            statusColorResId = statusColor;
            headerColorResId = headerColor;
            backgroundColorResId = backgroundColor;
            fabColorResId = fabColor;
            editBottomColorResId = editBottomColor;
        }

        public String getThemeName() {
            return this.themeName;
        }
        public String getDialogName() { return this.dialogName; }
        public int getStatusColorResId() { return this.statusColorResId; }
        public int getHeaderColorResId() { return this.headerColorResId; }
        public int getBackgroundColorResId() { return this.backgroundColorResId; }
        public int getFabColorResId() { return this.fabColorResId; }
        public int getEditBottomColorResId() { return this.editBottomColorResId; }
        public String getSelectDeleteItemShapeName() { return this.selectDeleteItemShapeName; }
    }

    public static android.content.SharedPreferences getSheardPreference(Context context) {
        return context.getSharedPreferences(APP_MEMORY, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getSheardPreference(context).edit();
    }

    public static String getString(Context context, String key) {
        return getSheardPreference(context).getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
        getEditor(context).putString(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getSheardPreference(context).getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        getEditor(context).putInt(key, value).apply();
    }

    public static FontSetting getSelectFont(Context context) {
        int selectFontNum = getSheardPreference(context).getInt(SELECT_FONT, DEFAULT_FONT_NUM);
        if (selectFontNum == FontSetting.FontNum1.ordinal()) {
            return FontSetting.FontNum1;
        }
        else if (selectFontNum == FontSetting.FontNum2.ordinal()) {
            return FontSetting.FontNum2;
        }
        else if (selectFontNum == FontSetting.FontNum3.ordinal()) {
            return FontSetting.FontNum3;
        }
        return FontSetting.SystemFont;
    }

    public static void saveSelectFont(Context context, FontSetting fontSetting) {
        getEditor(context).putInt(SELECT_FONT, fontSetting.ordinal()).apply();
    }

    public static MojiSizeSetting getSelectMojiSize(Context context) {
        int selectMojiSizeNum = getSheardPreference(context).getInt(SELECT_MOJI_SIZE, DEFAULT_MOJI_SIZE_NUM);
        if (selectMojiSizeNum == MojiSizeSetting.MojiSmall.ordinal()) {
            return MojiSizeSetting.MojiSmall;
        }
        else if (selectMojiSizeNum == MojiSizeSetting.MojiBig.ordinal()) {
            return MojiSizeSetting.MojiBig;
        }
        return MojiSizeSetting.MojiDefault;
    }

    public static void saveSelectMojiSize(Context context, MojiSizeSetting mojiSizeSetting) {
        getEditor(context).putInt(SELECT_MOJI_SIZE, mojiSizeSetting.ordinal()).apply();
    }

    public static SortSetting getSelectSort(Context context) {
        int selectSortNum = getSheardPreference(context).getInt(SELECT_SORT, DEFAULT_SORT_NUM);
        if (selectSortNum == SortSetting.SortNew.ordinal()) {
            return SortSetting.SortNew;
        } else {
            return SortSetting.SortOld;
        }
    }

    public static void saveSelectSort(Context context, SortSetting sortSetting) {
        getEditor(context).putInt(SELECT_SORT, sortSetting.ordinal()).apply();
    }

    public static ThemeSetting getSelectTheme(Context context) {
        int selectThemeNum = getSheardPreference(context).getInt(SELECT_THEME, DEFAULT_THEME);
        return ThemeSetting.values()[selectThemeNum];
    }

    public static void saveSelectTheme(Context context, ThemeSetting themeSetting) {
        getEditor(context).putInt(SELECT_THEME, themeSetting.ordinal()).commit();
    }

    public static int getSelectStyleId(Context context) {
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(context);
        int styleId = context.getResources().getIdentifier(themeSetting.getThemeName(), "style", context.getPackageName());
        return styleId;
    }

    public static int getSelectDialogStyleId(Context context) {
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(context);
        int styleId = context.getResources().getIdentifier(themeSetting.getDialogName(), "style", context.getPackageName());
        return styleId;
    }
}
