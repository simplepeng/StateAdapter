package me.simple.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import me.simple.state_adapter.StateAdapter;

public class MainActivity extends AppCompatActivity {

    private StateAdapter stateAdapter;
    private RecyclerView recyclerView;

    //    private RealAdapter realAdapter;
//    private BrvahAdapter brvahAdapter;

    private Items multiTypeItems = new Items();
    private MultiTypeAdapter multiTypeAdapter = new MultiTypeAdapter(multiTypeItems);

    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

//        realAdapter = new RealAdapter();
//        brvahAdapter = new BrvahAdapter(items);
        multiTypeAdapter.register(String.class, new ItemViewBinder());

        stateAdapter = StateAdapter.newBuilder()
                .registerEmpty(R.layout.adapter_empty_view)
                .registerLoading(R.layout.adapter_loading_view)
                .registerError(R.layout.adapter_error_view)
                .registerRetry(R.layout.adapter_retry_view)
                .registerCustom("login", R.layout.layout_login)
                .wrap(multiTypeAdapter);

//        stateAdapter.setOnItemViewClickListener(R.id.btn_state_retry, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                contentClick(v);
//            }
//        });

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
//        realAdapter.notifyDataSetChanged();
//        brvahAdapter.notifyDataSetChanged();
        multiTypeAdapter.notifyDataSetChanged();

        stateAdapter.showLoading();

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    items.add(String.valueOf(i));
                    multiTypeItems.add(String.valueOf(i));
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
                    multiTypeItems.add(String.valueOf(i));
                }
//                realAdapter.notifyItemRangeInserted(items.size() - 11, 11);
//                brvahAdapter.notifyItemRangeInserted(items.size() - 11, 11);
                multiTypeAdapter.notifyItemRangeInserted(items.size() - 11, 11);

            }
        }, 2000);
    }

    public void customClick(View view) {
        stateAdapter.showCustom("login");
    }

    class RealAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout, parent, false));
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


    static class BrvahAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public BrvahAdapter(@Nullable List<String> data) {
            super(R.layout.item_layout, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_item, item);
        }
    }

    class ItemViewBinder extends me.drakeet.multitype.ItemViewBinder<String, VH> {

        @NonNull
        @Override
        protected VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            return new VH(inflater.inflate(R.layout.item_layout, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull VH holder, @NonNull String item) {
            holder.tvItem.setText(item);
        }
    }
}
