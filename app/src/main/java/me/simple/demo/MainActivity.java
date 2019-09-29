package me.simple.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.simple.state_adapter.StateAdapter;
import me.simple.state_adapter.StateViewHolder;

public class MainActivity extends AppCompatActivity {

    private StateAdapter stateAdapter;
    private RealAdapter realAdapter;
    private RecyclerView recyclerView;

    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        realAdapter = new RealAdapter();
        stateAdapter = StateAdapter.wrap(realAdapter).setOnRetryItemClickListener(new StateAdapter.OnRetryItemClickListener() {
            @Override
            public void onClick(StateViewHolder holder, int position) {
                contentClick(findViewById(R.id.btn_content));
            }
        });

//        stateAdapter = StateAdapter.wrap(realAdapter, new CustomStateView());

        recyclerView.setAdapter(stateAdapter);

        getData();
    }

    private void getData() {
        emptyClick(findViewById(R.id.btn_empty));
    }

    public void emptyClick(View view) {
        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showEmpty();
            }
        }, 2000);
    }

    public void errorClick(View view) {
        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showError();
            }
        }, 2000);
    }

    public void retryClick(View view) {
        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showRetry();
            }
        }, 2000);
    }

    public void contentClick(View view) {
        items.clear();
        realAdapter.notifyDataSetChanged();

        stateAdapter.showLoading();

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    items.add(String.valueOf(i));
                    stateAdapter.showContent();
                }
            }
        }, 2000);

    }

    public void addMoreClick(View view) {
        items.add("-----------------------------");
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    items.add(String.valueOf(i));
                }
                realAdapter.notifyItemRangeInserted(items.size() - 11, 11);
            }
        }, 2000);
    }

    class RealAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tvItem.setText(items.get(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvItem;

        public VH(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
        }
    }
}
