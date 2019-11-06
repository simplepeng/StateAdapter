# StateAdapter

用包装器模式给RecyclerView添加一个有加载状态的Adapter(loading,empty,error,retry)

## 默认

默认状态图片来源网络AC娘表情包，如需使用最好自定义，嘻嘻...

|  Loading  | Empty | Error | Retry |
|  :--:  | :--:  |  :--:  |  :--:  |
| ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_loading.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_empty.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_error.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_retry.png) |


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

## 版本迭代

* v1.0.3：分离状态布局的写法，去耦合
* v1.0.2：默认不`show-loading`
* v1.0.1：fix type state bug
* v1.0.0：初次提交