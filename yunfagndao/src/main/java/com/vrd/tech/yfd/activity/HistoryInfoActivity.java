package com.vrd.tech.yfd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.AlertInfo;
import com.vrd.tech.yfd.entity.WiHistoryInfo;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.vrd.tech.yfd.view.DividerItemDecoration;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HistoryInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.mXRecyclerView)
    XRecyclerView mXRecyclerView;
    RecyclerView.Adapter<?> adapter;
    String objId;
    String token;

    @Bind(R.id.message_title)
    TextView messageTitle;
    @Bind(R.id.tvDeleteAllMsg)
    TextView tvDeleteAllMsg;
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();
    private int messageType = 0; //消息类型 0为历史消息 1为警报消息
    AlertDialog dialog;
    boolean isSendingRequest = false;
    private MobileApi mobileApi;
    final String fields = "content,rcv_time,cust_id,obj_id";
    private String theLastNotiId;
    WiHistoryInfo wiHistoryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_info);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        messageType = intent.getIntExtra("messageType", 0);
        objId = intent.getStringExtra("objId");
        token = intent.getStringExtra("access_token");
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        //初始化XRecyclerView(使用上和RecyclerView基本一样)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                loadMoreDate();
            }
        });
        messageTitle.setText(getString(R.string.title_history_info));
        adapter = new SimpleStringAdapter();

        mXRecyclerView.setAdapter(adapter);
        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        //第一次加载数据
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void loadMoreDate() {
        if (objId == null) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
        } else {
            Subscription sub;
            sub = getHistoryMessagesByLastId(theLastNotiId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LoadHistoryMessageSubscriber());
            mCompositeSubscription.add(sub);
        }

    }

    @OnClick(R.id.tvDeleteAllMsg)
    public void onClick() {
        //deleteAllAlertMsg();
    }

    /**
     * 加载更多历史消息回调
     */
    class LoadHistoryMessageSubscriber extends Subscriber<WiHistoryInfo> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mXRecyclerView.loadMoreComplete();
            MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
        }

        @Override
        public void onNext(WiHistoryInfo historyMessage) {
            mXRecyclerView.loadMoreComplete();
            if (historyMessage.getData().size() != 0) {
                theLastNotiId = historyMessage.getData().get(historyMessage.getData().size() - 1).getNoti_id();
                wiHistoryInfo.getData().addAll(historyMessage.getData());
            } else {
                MyUtil.showToast(getApplicationContext(), getString(R.string.no_more_data));
            }
        }
    }


    private void refresh() {
        if (objId == null) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            Subscription sub;
            sub = getHistoryMessagesByLastId("").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new HistoryMessageSubscriber());
            mCompositeSubscription.add(sub);
        }
    }

    /**
     * 刷新历史消息回调
     */
    class HistoryMessageSubscriber extends Subscriber<WiHistoryInfo> {
        @Override
        public void onCompleted() {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
        }

        @Override
        public void onNext(WiHistoryInfo historyMessage) {
            if (wiHistoryInfo == null) {
                if (historyMessage.getTotal() == 0) {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.no_history_info));
                } else {
                    wiHistoryInfo = historyMessage;
                    theLastNotiId = wiHistoryInfo.getData().get(wiHistoryInfo.getData().size() - 1).getNoti_id();
                    adapter.notifyDataSetChanged();
                }
            } else {
                if (wiHistoryInfo.getTotal() < historyMessage.getTotal()) {
                    //有新数据
                    for (int i = historyMessage.getTotal() - wiHistoryInfo.getTotal(); i > 0; i--) {
                        wiHistoryInfo.getData().add(0, historyMessage.getData().get(i - 1));
                    }
                    wiHistoryInfo.setTotal(historyMessage.getTotal());
                } else {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.no_new_data));
                }
            }
        }
    }


    private Observable<WiHistoryInfo> getHistoryMessagesByLastId(String lastId) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("obj_id", objId);
        options.put("msg_type", "0");
        options.put("limit", "15");
        options.put("sorts", "-noti_id");
        options.put("page", "noti_id");
        if (!TextUtils.isEmpty(lastId)) {
            options.put("min_id", lastId);
        }
        options.put("access_token", token);
        return mobileApi.getHistoryInfo(APIUtil.createOptions(MobileApi.Method_Notification, options, fields));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        mobileApi = null;
    }

    /**
     * 历史消息数据适配器
     */
    class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(HistoryInfoActivity.this).inflate(R.layout.list_item_historyinfo, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (holder == null) {
                return;
            }
            holder.tv_msg_content.setText(wiHistoryInfo.getData().get(position).getContent());
            String date = MyUtil.changeTime(wiHistoryInfo.getData().get(position).getRcv_time(), 0);
            holder.tv_msg_date.setText(date.substring(5, 16));
        }

        @Override
        public int getItemCount() {
            if (wiHistoryInfo == null) {
                return 0;
            } else {
                return wiHistoryInfo.getData().size();
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_msg_date;
            TextView tv_msg_content;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_msg_date = (TextView) itemView.findViewById(R.id.tv_msg_date);
                tv_msg_content = (TextView) itemView.findViewById(R.id.tv_msg_content);
            }
        }
    }

//    private void showWaitingDialog() {
//        dialog = new AlertDialog.Builder(HistoryInfoActivity.this).create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//        dialog.getWindow().setContentView(R.layout.waiting_content_view);
//        dialog.getWindow().setGravity(Gravity.CENTER);
//        BookLoading loading = (BookLoading) dialog.getWindow().findViewById(R.id.bookLoading);
//        loading.start();
//    }
//
//    private void dismissWaitingDialog() {
//        if (dialog != null) {
//            BookLoading loading = (BookLoading) dialog.getWindow().findViewById(R.id.bookLoading);
//            loading.stop();
//            dialog.dismiss();
//            dialog = null;
//        }
//    }
}
