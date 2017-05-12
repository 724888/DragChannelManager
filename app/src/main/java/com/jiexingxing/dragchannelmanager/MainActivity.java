package com.jiexingxing.dragchannelmanager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2017/2/16.
 */
public class MainActivity extends Activity {
    private RecyclerView minerecycleView;
    private List<ItemBean> mineData = new ArrayList<ItemBean>();
    private List<ItemBean> otherData = new ArrayList<ItemBean>();
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;
    private ItemTouchHelper.Callback othercallback;
    private ItemTouchHelper othertouchHelper;
    private MyAdapter otherAdapter;
    private RecyclerView other_recycleView;
    private MyAdapter mineAdapter;
    private TextView edit;
    boolean isediting = false;
    private int screenWidth;
    private int recycleview_width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        recycleview_width = (screenWidth - DataTools.dip2px(MainActivity.this, 60)) / 3;
        initData();
        initView();

    }

    private void initView() {


        minerecycleView = (RecyclerView) findViewById(R.id.mine_recycleView);
        edit = (TextView) findViewById(R.id.edit);
        mineAdapter = new MyAdapter(mineData, 0);
        minerecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        minerecycleView.setAdapter(mineAdapter);
        minerecycleView.setLayoutManager(new GridLayoutManager(this, 3));
        callback = new SimpleItemTouchHelperCallback(mineAdapter);
        //用Callback构造ItemtouchHelper
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(minerecycleView);

        other_recycleView = (RecyclerView) findViewById(R.id.other_recycleView);
        otherAdapter = new MyAdapter(otherData, 1);
        other_recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        other_recycleView.setAdapter(otherAdapter);
        other_recycleView.setLayoutManager(new GridLayoutManager(this, 3));
        othercallback = new SimpleItemTouchHelperCallback(otherAdapter);
        //用Callback构造ItemtouchHelper
        othertouchHelper = new ItemTouchHelper(othercallback);
        othertouchHelper.attachToRecyclerView(other_recycleView);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isediting) {
                    edit.setText("编辑");
                    for (int i = 0; i < mineData.size(); i++) {
                        mineData.get(i).setSelect(false);
                    }
                } else {
                    edit.setText("完成");
                    for (int i = 0; i < mineData.size(); i++) {
                        mineData.get(i).setSelect(true);
                    }
                }
                isediting = !isediting;
                mineAdapter.notifyDataSetChanged();

            }
        });


        minerecycleView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, RecyclerView.ViewHolder childViewViewHolder) {

            }

            @Override
            public void onLongClick(View view, int posotion, RecyclerView.ViewHolder childViewViewHolder) {
                if (posotion != 0 && posotion != 1) {
//                    for (int i = 0; i < mineData.size(); i++) {
//                        mineData.get(i).setSelect(true);
//                    }
//                    isediting = true;
//                    edit.setText("完成");
//                    mineAdapter.allSelect();
                    touchHelper.startDrag(childViewViewHolder);
                }

            }
        }));


        other_recycleView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position, RecyclerView.ViewHolder childViewViewHolder) {

                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.channel_tv);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ItemBean channel = otherAdapter.getItem(position);
                    if (isediting) {
                        channel.setSelect(true);
                    } else {
                        channel.setSelect(false);
                    }
                    mineAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                RecyclerView.LayoutManager mine_layoutManager = minerecycleView.getLayoutManager();
                                if (mine_layoutManager instanceof LinearLayoutManager) {
                                    LinearLayoutManager linearManager = (LinearLayoutManager) mine_layoutManager;
                                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                                    minerecycleView.getChildAt(lastItemPosition).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation);
                                    otherAdapter.deleteItem(position);
                                }
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
            }

            @Override
            public void onLongClick(View view, int posotion, RecyclerView.ViewHolder childViewViewHolder) {

            }
        }));


    }


    private void initData() {
        for (int i = 0; i < 15; i++) {
            mineData.add(new ItemBean("我的" + i, false));
        }
        for (int i = 15; i < 30; i++) {
            otherData.add(new ItemBean("其他" + i, false));
        }
    }

    //
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

        private List<ItemBean> mData;
        private int type;

        //    自己  长按出×    按  ×删除     别人的只有  一个点击删除的操作
//    别人的只有点击添加操作

        public MyAdapter(List<ItemBean> list, int type) {
            this.mData = list;
            this.type = type;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list, parent, false);
            MyViewHolder vh = new MyViewHolder(root);
            //为Item设置点击事件

            return vh;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.channel_tv.setText(mData.get(position).getText());
            if (type == 1) {
                holder.delete_tv.setVisibility(View.GONE);
            } else if (type == 0) {
                if (position == 0 || position == 1) {
                    holder.delete_tv.setVisibility(View.GONE);
                } else if (mData.get(position).isSelect()) {
                    holder.delete_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.delete_tv.setVisibility(View.GONE);
                }
            }
            ViewGroup.LayoutParams params = holder.channel_rl.getLayoutParams();
            params.width = recycleview_width;
            holder.channel_rl.setLayoutParams(params);

            holder.delete_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type == 0) {
                        ItemBean positionItemBean = mineAdapter.getItem(position);
                        positionItemBean.setSelect(false);
                        mineAdapter.deleteItem(position);
                        otherAdapter.addItem(positionItemBean);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            //注意:这里最少有一个,因为有多了一个添加按钮
            return null == mData ? 0 : mData.size();
        }


        public ItemBean getItem(int position) {
            return mData.get(position);
        }

        public void addItem(ItemBean channel) {
            mData.add(channel);
            notifyDataSetChanged();
        }

        public void allSelect() {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setSelect(true);
            }
            notifyDataSetChanged();
        }

        public void addItem(int position, ItemBean channel) {
            mData.add(position, channel);
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            mData.remove(position);
            notifyDataSetChanged();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView channel_tv;
            private TextView delete_tv;
            private RelativeLayout channel_rl;

            public MyViewHolder(View itemView) {
                super(itemView);
                channel_tv = (TextView) itemView.findViewById(R.id.channel_tv);
                delete_tv = (TextView) itemView.findViewById(R.id.delete_tv);
                channel_rl = (RelativeLayout) itemView.findViewById(R.id.channel_rl);
            }
        }

        //        移动处理
        @Override
        public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (type == 0) {
                int fromPosition = source.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
//                Toast.makeText(MainActivity.this, "fromPosition>>" + fromPosition + ">>toPosition>>" + toPosition, Toast.LENGTH_LONG).show();
                if (toPosition == 0 || toPosition == 1) {

                } else {
                    if (fromPosition < mData.size() && toPosition < mData.size()) {
                        //交换数据位置
//                        Collections.swap(mData, fromPosition, toPosition);
                        ItemBean itemBean = mData.get(fromPosition);
                        mData.remove(fromPosition);
                        mData.add(toPosition, itemBean);
                        //刷新位置交换
                        notifyItemMoved(fromPosition, toPosition);
                    }
                }
                //移动过程中移除view的放大效果
                onItemClear(source);
            }
        }

        //        移除
        @Override
        public void onItemDissmiss(RecyclerView.ViewHolder source) {
            if (type == 0) {
                int position = source.getAdapterPosition();
                mData.remove(position); //移除数据
                notifyItemRemoved(position);//刷新数据移除
            }
        }

        //        放大
        @Override
        public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
            if (type == 0) {
                //当拖拽选中时放大选中的view
                int position = viewHolder.getAdapterPosition();
                viewHolder.itemView.setScaleX(1.2f);
                viewHolder.itemView.setScaleY(1.2f);
            }
        }

        //   恢复
        @Override
        public void onItemClear(RecyclerView.ViewHolder viewHolder) {
            //拖拽结束后恢复view的状态
            if (type == 0) {
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
            }
        }

    }

    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation
    ) {

        int[] initLocation = new int[2];
        moveView.getLocationInWindow(initLocation);
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            private boolean isMove;

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                isMove = false;
            }
        });
    }

    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }


}
