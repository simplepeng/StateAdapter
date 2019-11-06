package me.simple.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.simple.state_adapter.StateAdapter;
import me.simple.state_adapter.impl.SimpleEmptyView;
import me.simple.state_adapter.impl.SimpleErrorView;
import me.simple.state_adapter.impl.SimpleLoadingView;
import me.simple.state_adapter.impl.SimpleRetryView;

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
        stateAdapter = StateAdapter.wrap(realAdapter)
                .register(new SimpleEmptyView())
                .register(new SimpleErrorView())
                .register(new SimpleRetryView())
                .register(new SimpleLoadingView());

        stateAdapter.setOnItemViewClickListener(R.id.btn_state_retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentClick(v);
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
