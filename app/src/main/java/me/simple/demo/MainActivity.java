package me.simple.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.simple.state_adapter.StateAdapter;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        realAdapter = new RealAdapter();
        stateAdapter = StateAdapter.wrap(realAdapter);
        recyclerView.setAdapter(stateAdapter);

        getData();
    }

    private void getData() {
        emptyClick(findViewById(R.id.btn_empty));
    }

    private void loadMore() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                items.add("load more");
                for (int i = 0; i < 20; i++) {
                    items.add(String.valueOf(i));
                    realAdapter.notifyItemRangeInserted(items.size() - 20, 20);
                }
            }
        }, 2000);

    }

    public void emptyClick(View view) {
        items.clear();
        realAdapter.notifyDataSetChanged();

        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showEmpty();
            }
        }, 2000);

    }

    public void errorClick(View view) {
//        items.clear();
//        realAdapter.notifyDataSetChanged();

        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showError();
            }
        }, 2000);
    }

    public void retryClick(View view) {
//        items.clear();
//        realAdapter.notifyDataSetChanged();

        stateAdapter.showLoading();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                stateAdapter.showRetry();
            }
        }, 2000);
    }

    public void contentClick(View view) {
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
