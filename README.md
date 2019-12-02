# StateAdapter

用装饰器模式给RecyclerView添加一个有加载状态布局的Adapter

* 完美支持原生RecyclerView.Adapter
* 完美支持[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
* 完美支持[MultiType](https://github.com/drakeet/MultiType)

## 默认

默认状态图片来源网络AC娘表情包，如需使用最好自定义，嘻嘻...

|  Loading  | Empty | Error | Retry |
|  :--:  | :--:  |  :--:  |  :--:  |
| ![](https://i.loli.net/2019/11/06/9emTI2Wkj36Opdl.png) | ![](https://i.loli.net/2019/11/06/VJqFStfwnMHN7dy.png) | ![](https://i.loli.net/2019/11/06/ERNpLygI1oM9tPb.png) | ![](https://i.loli.net/2019/11/06/igtEjTVl85Cov2Z.png) |


## 依赖

```groovy
implementation 'me.simple:state-adapter:1.0.3'
```


## 使用

### 基础使用

```java
realAdapter = new RealAdapter();
stateAdapter = StateAdapter.wrap(realAdapter)
                .register(new SimpleEmptyView())
                .register(new SimpleErrorView())
                .register(new SimpleRetryView())
                .register(new SimpleLoadingView());
recyclerView.setAdapter(stateAdapter);

//可用方法
stateAdapter.showLoading();
stateAdapter.showEmpty();
stateAdapter.showError();
stateAdapter.showRetry();
stateAdapter.showContent();//or realAdapter.notifyDataSetChanged

//设置状态布局里控件的点击事件
stateAdapter.setOnItemViewClickListener(int viewId, View.OnClickListener listener)
```

### 自定义视图

```java
//StateEmptyView，StateLoadingView，StateErrorView，StateErrorView
public class SimpleEmptyView extends StateEmptyView {

    @Override
    public int setLayoutRes() {
        return R.layout.simple_empty_view;
    }

    @Override
    public void onCreate(View view) {
        super.onCreate(view);
    }

    @Override
    public void onAttachedToWindow(StateViewHolder viewHolder) {
        super.onAttachedToWindow(viewHolder);
    }

    @Override
    public void onDetachedFromWindow(StateViewHolder viewHolder) {
        super.onDetachedFromWindow(viewHolder);
    }
}
```

## 混淆

```java
-keep me.simple.state_adapter.** { *; }
-keepnames me.simple.state_adapter.** { *; }
-keep class androidx.recyclerview.widget.**{*;}
```

## 版本迭代

* v1.0.3：分离状态布局的写法，去耦合
* v1.0.2：默认不`show-loading`
* v1.0.1：fix type state bug
* v1.0.0：初次提交