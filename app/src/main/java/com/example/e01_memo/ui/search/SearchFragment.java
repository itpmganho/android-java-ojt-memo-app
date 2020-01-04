package com.example.e01_memo.ui.search;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e01_memo.MyApplication;
import com.example.e01_memo.R;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.ui.BaseFragment;
import com.example.e01_memo.ui.main.MainFragment;
import com.example.e01_memo.usecase.MemoDataInteractorImpl;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment implements SearchContract.SearchView {

    private static final String ARGS_SEARCH_FRAGMENT_THEME_ORDINAL = "args_search_fragment_theme_ordinal";

    private SearchActionListener actionListener;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private SearchResultListAdapter adapter;
    private SearchContract.SearchPresenter presenter;
    private SharedPreferencesUtil.ThemeSetting themeSetting;

    public interface SearchActionListener {
        void actionMoveToEdit(MemoItem item);
        void actionDeleteBarGone();
        void actionChangeSelectMemoCount(int selectCount);
        void actionShowDeleteBar();
        void actionHideDeleteBar();
        void actionShowDeletedToast();
    }

    @Override
    public void setPresenter(SearchContract.SearchPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            actionListener = (SearchActionListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public SearchFragment() {}

    public static SearchFragment newInstance(int themeOrdinal) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_SEARCH_FRAGMENT_THEME_ORDINAL, themeOrdinal);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int ordinal = getArguments().getInt(ARGS_SEARCH_FRAGMENT_THEME_ORDINAL);
        themeSetting = SharedPreferencesUtil.ThemeSetting.values()[ordinal];
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyText = view.findViewById(R.id.emptyText);

        adapter = new SearchResultListAdapter(new ArrayList<MemoItem>());
        adapter.setHasStableIds(true);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayout.VERTICAL);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), manager.getOrientation()));

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

        public RecyclerItemTouchHelper(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return true;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                final View foregroundView = ((MemoViewHolder)viewHolder).viewForeground;

                getDefaultUIUtil().onSelected(foregroundView);
            }
        }

        @Override
        public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((MemoViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((MemoViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((MemoViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            SearchFragment.this.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            MemoItem item = adapter.memoItems.get(viewHolder.getAdapterPosition());
            if (isDeleteMode() || item.isFavorite()) return 0;
            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }
    }

    private void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        presenter.onSwipeDeleteMemo(adapter.memoItems.get(position));
    }

    @Override
    public void displayMemoData(List<MemoItem> itemList) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        adapter.memoItems = itemList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showDeletedToast() {
        if (actionListener != null) actionListener.actionShowDeletedToast();
    }

    @Override
    public List<MemoItem> getSelectMemoData() {
        if (adapter != null) {
            return adapter.selectMemoItems;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isDeleteMode() {
        if (adapter != null) {
            return adapter.deleteMode;
        }
        return false;
    }

    @Override
    public void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearDeleteBar() {
        if (adapter != null) adapter.resetDeleteBar();
    }

    @Override
    public void moveToEdit(MemoItem item) {
        if (actionListener != null) actionListener.actionMoveToEdit(item);
    }

    private class SearchResultListAdapter extends RecyclerView.Adapter<MemoViewHolder> {

        private List<MemoItem> memoItems;
        private List<MemoItem> selectMemoItems = new ArrayList<>();
        private boolean deleteMode = false;

        public void resetDeleteBar() {
            deleteMode = false;
            selectMemoItems = new ArrayList<>();
            notifyDataSetChanged();
            if (actionListener != null) actionListener.actionDeleteBarGone();
        }

        public SearchResultListAdapter(List<MemoItem> items) { memoItems = items; }

        @NonNull
        @Override
        public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_memo_item, viewGroup, false);
            return new MemoViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull MemoViewHolder memoViewHolder, final int position) {
            MemoItem item = memoItems.get(position);
            String title = "";
            if (item.getMemo().contains("\n")) {
                String[] mojis = item.getMemo().split("\n");
                title = mojis.length == 0 ? item.getMemo() : mojis[0] + "…";
            } else {
                title = item.getMemo();
            }
            memoViewHolder.memoTitleText.setText(title);
            String dateStr = MemoDataInteractorImpl.dateToString(item.getCreateDate(), MemoDataInteractorImpl.DATE_FORMAT_STR);
            memoViewHolder.createDateText.setText(dateStr);

            if (item.isFavorite()) {
                memoViewHolder.favoriteImage.setImageResource(R.mipmap.ic_favorite_on);
            } else {
                memoViewHolder.favoriteImage.setImageResource(R.mipmap.ic_favorite_off);
            }

            if (selectMemoItems.contains(item)) {
                int shapeId = getActivity().getResources().getIdentifier(themeSetting.getSelectDeleteItemShapeName(), "drawable", getActivity().getPackageName());
                memoViewHolder.viewForeground.setBackgroundResource(shapeId);
            } else {
                memoViewHolder.viewForeground.setBackgroundColor(ContextCompat.getColor(getActivity(), themeSetting.getBackgroundColorResId()));
            }
            if (selectMemoItems.size() != 0) actionListener.actionChangeSelectMemoCount(selectMemoItems.size());

            memoViewHolder.favoriteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!deleteMode) {
                        MemoItem memoItem = memoItems.get(position);
                        memoItem.setFavorite(!memoItem.isFavorite());
                        presenter.onChangeFavorite(memoItem);
                    }
                }
            });

            memoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemoItem memoItem = memoItems.get(position);
                    if (!deleteMode) {
                        if (presenter != null) {
                            presenter.onButton(SearchContract.SearchButton.ListItem, memoItem);
                        }
                    } else {
                        deleteModeTap(memoItem);
                    }
                }
            });

            memoViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MemoItem memoItem = memoItems.get(position);
                    if (!memoItem.isFavorite()) {
                        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                        deleteModeTap(memoItem);
                        return true;
                    }
                    return false;
                }
            });
        }

        private void deleteModeTap(MemoItem memoItem) {
            if (selectMemoItems.contains(memoItem)) {
                selectMemoItems.remove(memoItem);
            } else {
                memoItem.setDeleteFlag(true);
                selectMemoItems.add(memoItem);
            }
            if (deleteMode && selectMemoItems.size() == 0 && actionListener != null) {
                // 解除
                actionListener.actionHideDeleteBar();
                deleteMode = false;
            }
            else if (!deleteMode && selectMemoItems.size() == 1 && actionListener != null){
                // 削除モード
                actionListener.actionShowDeleteBar();
                deleteMode = true;
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return memoItems.size();
        }

        @Override
        public long getItemId(int position) {
            return memoItems.get(position).getId();
        }
    }

    private class MemoViewHolder extends RecyclerView.ViewHolder{
        TextView memoTitleText, createDateText;
        ImageView favoriteImage;
        LinearLayout viewBackground, viewForeground;

        public MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            memoTitleText = itemView.findViewById(R.id.memoTitleText);
            createDateText = itemView.findViewById(R.id.createDateText);
            favoriteImage = itemView.findViewById(R.id.favoriteImg);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()), memoTitleText, createDateText);
        }
    }
}
