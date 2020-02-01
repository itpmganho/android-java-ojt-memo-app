package com.example.e01_memo.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e01_memo.MyApplication;
import com.example.e01_memo.R;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.ui.BaseFragment;
import com.example.e01_memo.usecase.MemoDataInteractorImpl;
import com.example.e01_memo.util.Constant;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment implements MainContract.MainView {

    private static final String ARGS_MAIN_FRAGMENT_THEME_ORDINAL = "args_main_fragment_theme_ordinal";

    private MainContract.MainPresenter presenter;
    private MainActionListener actionListener;
    private RecyclerView recyclerView;
    private TextView emptyText;
    private MemoListAdapter adapter;
    private View rootView;
    private SharedPreferencesUtil.ThemeSetting themeSetting;

    public interface MainActionListener {
        void actionOpenDrawer(boolean open);
        void actionSelectNavItem(int navigationItemNum);
        void actionMoveToEdit(MemoItem item);
        void actionShowDeleteBar();
        void actionHideDeleteBar();
        void actionDeleteBarGone();
        void actionChangeSelectMemoCount(int selectCount);
        void actionShowDeletedToast();
        void actionShowRestoreToast();
        void actionShowDeleteMemoDetail(MemoItem item);
    }

    public MainFragment() {}

    public static MainFragment newInstance(int themeOrdinal) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_MAIN_FRAGMENT_THEME_ORDINAL, themeOrdinal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            actionListener = (MainActionListener)context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int ordinal = getArguments().getInt(ARGS_MAIN_FRAGMENT_THEME_ORDINAL);
        themeSetting = SharedPreferencesUtil.ThemeSetting.values()[ordinal];
        rootView = view.findViewById(R.id.rootView);
        rootView.setBackgroundColor(ContextCompat.getColor(getActivity(), themeSetting.getBackgroundColorResId()));

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyText = view.findViewById(R.id.emptyText);

        initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()), emptyText);

        adapter = new MemoListAdapter(new ArrayList<MemoItem>());
        adapter.setHasStableIds(true);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

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
            MainFragment.this.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
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
        if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
            presenter.onSwipeRestoreMemo(adapter.memoItems.get(position));
        } else {
            presenter.onSwipeDeleteMemo(adapter.memoItems.get(position));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.resetDeleteBar();
    }

    @Override
    public void setPresenter(MainContract.MainPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void openDrawer(boolean open) {
        if (actionListener != null) actionListener.actionOpenDrawer(open);
    }

    @Override
    public void selectNavigationItem(int navigationItemNum) {
        if (actionListener != null) actionListener.actionSelectNavItem(navigationItemNum);
    }

    @Override
    public void displayMemoData(List<MemoItem> itemList) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        adapter.memoItems = itemList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void moveToEdit(MemoItem item) {
        if (actionListener != null) actionListener.actionMoveToEdit(item);
    }

    @Override
    public void clearDeleteBar() {
        if (adapter != null) {
            adapter.resetDeleteBar();
        }
    }

    @Override
    public void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
        if (MyApplication.selectNavigationItem != Constant.NavigationItem.DELETE) {
            emptyText.setText(R.string.not_delete_memo);
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
    public void showDeletedToast() {
        if (actionListener != null) actionListener.actionShowDeletedToast();
    }

    @Override
    public void showRestoreToast() {
        if (actionListener != null) actionListener.actionShowRestoreToast();
    }

    @Override
    public void showDeleteMemoDetail(MemoItem item) {
        if (actionListener != null) actionListener.actionShowDeleteMemoDetail(item);
    }

    @Override
    public SharedPreferencesUtil.SortSetting getSelectSort() {
        return SharedPreferencesUtil.getSelectSort(getActivity());
    }

    @Override
    public List<MemoItem> getSelectMemoData() {
        if (adapter != null) {
            return adapter.selectMemoItems;
        } else {
            return new ArrayList<>();
        }
    }

    private class MemoListAdapter extends RecyclerView.Adapter<MemoViewHolder> {

        private static final int DISPLAY_OTHER = 0;
        private static final int DISPLAY_DELETE = 1;
        private List<MemoItem> memoItems;
        private List<MemoItem> selectMemoItems = new ArrayList<>();
        private boolean deleteMode = false;

        public void resetDeleteBar() {
            deleteMode = false;
            selectMemoItems = new ArrayList<>();
            notifyDataSetChanged();
            if (actionListener != null) actionListener.actionDeleteBarGone();
        }

        public MemoListAdapter(List<MemoItem> items) {
            memoItems = items;
        }

        @NonNull
        @Override
        public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View inflate;
            if (viewType == DISPLAY_DELETE) {
                inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_delete_item, viewGroup, false);
            } else {
                inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_memo_item, viewGroup, false);
            }
            return new MemoViewHolder(inflate);
        }

        @Override
        public int getItemViewType(int position) {
            if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                return DISPLAY_DELETE;
            } else {
                return DISPLAY_OTHER;
            }
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

            if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                memoViewHolder.favoriteImage.setImageResource(R.mipmap.ic_delete);
            } else {
                if (item.isFavorite()) {
                    memoViewHolder.favoriteImage.setImageResource(R.mipmap.ic_favorite_on);
                } else {
                    memoViewHolder.favoriteImage.setImageResource(R.mipmap.ic_favorite_off);
                }
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
                    if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                        int dialogStyleId = SharedPreferencesUtil.getSelectDialogStyleId(getActivity());
                        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), dialogStyleId)
                                .setMessage(R.string.delete_dialog_message)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MemoItem memoItem = memoItems.get(position);
                                        if (presenter != null) presenter.onDeleteMemo(memoItem);
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

                                initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()),
                                        message, positive, negative);
                            }
                        });
                        dialog.show();
                    } else {
                        if (!deleteMode) {
                            MemoItem memoItem = memoItems.get(position);
                            memoItem.setFavorite(!memoItem.isFavorite());
                            presenter.onChangeFavorite(memoItem);
                        }
                    }
                }
            });

            memoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemoItem memoItem = memoItems.get(position);
                    if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                        if (!deleteMode) {
                            if (presenter != null) {
                                presenter.onButton(MainContract.MainButton.DeleteItem, memoItem);
                            }
                        } else {
                            deleteModeTap(memoItem);
                        }
                    } else {
                        if (!deleteMode) {
                            if (presenter != null) {
                                presenter.onButton(MainContract.MainButton.ListItem, memoItem);
                            }
                        } else {
                            if (!memoItem.isFavorite()) deleteModeTap(memoItem);
                        }
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
                boolean flag = MyApplication.selectNavigationItem != Constant.NavigationItem.DELETE;
                memoItem.setDeleteFlag(flag);
                selectMemoItems.add(memoItem);
            }
            if (deleteMode && selectMemoItems.size() == 0 && actionListener != null) {
                // 解除
                actionListener.actionHideDeleteBar();
                deleteMode = false;
            }
            else if (!deleteMode && selectMemoItems.size() == 1 && actionListener != null) {
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
            viewBackground = itemView.findViewById(R.id.viewBackground);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()), memoTitleText, createDateText);
        }
    }
}
