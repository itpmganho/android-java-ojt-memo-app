package com.example.e01_memo.ui.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.e01_memo.R;
import com.example.e01_memo.util.DisplayUtil;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ThemeFragment extends Fragment implements ThemeContract.ThemeView {

    private int[] statusBarColor = {
            R.color.colorStatusBar1, R.color.colorStatusBar2, R.color.colorStatusBar3, R.color.colorStatusBar4,
            R.color.colorStatusBar5, R.color.colorStatusBar6, R.color.colorStatusBar7, R.color.colorStatusBar8
    };
    private int[] headerBarColor = {
            R.color.colorHeaderBar1, R.color.colorHeaderBar2, R.color.colorHeaderBar3, R.color.colorHeaderBar4,
            R.color.colorHeaderBar5, R.color.colorHeaderBar6, R.color.colorHeaderBar7, R.color.colorHeaderBar8
    };
    private int[] themeBgColor = {
            R.color.colorThemeBg1, R.color.colorThemeBg2, R.color.colorThemeBg3, R.color.colorThemeBg4,
            R.color.colorThemeBg5, R.color.colorThemeBg6, R.color.colorThemeBg7, R.color.colorThemeBg8
    };
    private int[] fabBgColor = {
            R.color.colorFabBg1, R.color.colorFabBg2, R.color.colorFabBg3, R.color.colorFabBg4,
            R.color.colorFabBg5, R.color.colorFabBg6, R.color.colorFabBg7, R.color.colorFabBg8
    };

    private static final String ARGS_THEME_FRAGMENT_THEME_ORDINAL = "args_theme_fragment_theme_ordinal";

    private ThemeContract.ThemePresenter presenter;
    private RecyclerView recyclerView;
    private ThemeListAdapter adapter;
    private ThemeActionListener actionListener;
    private View rootView;

    public interface ThemeActionListener {
        void actionChangeTheme(View rootView, SharedPreferencesUtil.ThemeSetting themeSetting);
    }

    public ThemeFragment() {}

    public static ThemeFragment newInstance(int ordinal) {
        ThemeFragment fragment = new ThemeFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_THEME_FRAGMENT_THEME_ORDINAL, ordinal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            actionListener = (ThemeActionListener)context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int ordinal = getArguments().getInt(ARGS_THEME_FRAGMENT_THEME_ORDINAL);
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.ThemeSetting.values()[ordinal];
        rootView = view.findViewById(R.id.rootView);
        rootView.setBackgroundColor(ContextCompat.getColor(getActivity(), themeSetting.getBackgroundColorResId()));

        Point displaySize = DisplayUtil.getDisplaySize(getActivity());
        int maxWidth = displaySize.x / 2;
        int viewSize = maxWidth - (48 * 2);

        recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new ThemeListAdapter(viewSize, themeSetting);
        adapter.setHasStableIds(true);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new ItemOffsetDecoration(48));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(ThemeContract.ThemePresenter presenter) {
        this.presenter = presenter;
    }

    private class ThemeListAdapter extends RecyclerView.Adapter<ThemeViewHolder> {

        private List<Boolean> selectFlagList = new ArrayList<>();
        private int viewSize;

        public ThemeListAdapter(int size, SharedPreferencesUtil.ThemeSetting setting) {
            viewSize = size;
            selectFlagList.clear();
            for (int i = 0; i < statusBarColor.length; i++) {
                selectFlagList.add(setting.ordinal() == i);
            }
        }

        @NonNull
        @Override
        public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_theme_item, viewGroup, false);
            setBaseSize(inflate);
            return new ThemeViewHolder(inflate);
        }

        private void setBaseSize(View inflate) {
            CardView cardView = inflate.findViewById(R.id.cardView);
            View statusBar = inflate.findViewById(R.id.statusBar);
            View headerBar = inflate.findViewById(R.id.headerBar);
            View fab = inflate.findViewById(R.id.fab);

            cardView.getLayoutParams().height = viewSize;
            int statusBarSize = viewSize / 3 / 3;
            statusBar.getLayoutParams().height = statusBarSize;
            headerBar.getLayoutParams().height = statusBarSize * 2;
            fab.getLayoutParams().height = statusBarSize * 2;
            fab.getLayoutParams().width = statusBarSize * 2;
        }

        @Override
        public void onBindViewHolder(@NonNull ThemeViewHolder themeViewHolder, final int position) {
            viewStatusChange(themeViewHolder, position);
            themeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!selectFlagList.get(position)) {
                        for (int i = 0; i < selectFlagList.size(); i++) {
                            selectFlagList.set(i, false);
                        }
                        selectFlagList.set(position, true);
                        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.ThemeSetting.values()[position];
                        SharedPreferencesUtil.saveSelectTheme(getActivity(), themeSetting);

                        if (actionListener != null && rootView != null) {
                            actionListener.actionChangeTheme(rootView, themeSetting);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }

        private void viewStatusChange(ThemeViewHolder viewHolder, int position) {
            viewHolder.statusBar.setBackgroundResource(statusBarColor[position]);
            viewHolder.headerBar.setBackgroundResource(headerBarColor[position]);
            viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), themeBgColor[position]));
            viewHolder.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), fabBgColor[position])));
            viewHolder.checkImg.setColorFilter(ContextCompat.getColor(getActivity(), headerBarColor[position]), PorterDuff.Mode.SRC_ATOP);
            viewHolder.shadow.setVisibility(selectFlagList.get(position) ? View.VISIBLE : View.GONE);
            viewHolder.checkImg.setVisibility(selectFlagList.get(position) ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return statusBarColor.length;
        }
    }

    private class ThemeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        View statusBar, headerBar, fab, shadow;
        ImageView checkImg;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            statusBar = itemView.findViewById(R.id.statusBar);
            headerBar = itemView.findViewById(R.id.headerBar);
            fab = itemView.findViewById(R.id.fab);
            checkImg = itemView.findViewById(R.id.checkImg);
            shadow = itemView.findViewById(R.id.shadow);
        }
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int itemOffset;

        public ItemOffsetDecoration(int offset) {
            itemOffset = offset;
        }

        public ItemOffsetDecoration(Context context,int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
        }
    }
}
