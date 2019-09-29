# StateAdapter

用包装器模式给RecyclerView添加一个有加载状态的Adapter(loading,empty,error,retry)

## 默认

默认状态图片来源网络AC娘表情包，如需使用最好自定义，嘻嘻...

|  Loading  | Empty | Error | Retry |
|  :--:  | :--:  |  :--:  |  :--:  |
| ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_loading.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_empty.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_error.png) | ![](https://raw.githubusercontent.com/simplepeng/ImageRepo/master/preview_retry.png) |


## 依赖

```groovy
implementation 'me.simple:state-adapter:1.0.1'
```


## 使用

### 基础使用

```java
stateAdapter = StateAdapter.wrap(realAdapter);
recyclerView.setAdapter(stateAdapter);

//可用方法
stateAdapter.showLoading();
stateAdapter.showEmpty();
stateAdapter.showError();
stateAdapter.showRetry();
stateAdapter.showContent();
stateAdapter.setOnItemViewClickListener(int viewId, View.OnClickListener listener)
```

### 自定义视图

```java
public class CustomStateView implements IStateView {
   
}

...
  
stateAdapter = StateAdapter.wrap(realAdapter, new CustomStateView());
recyclerView.setAdapter(stateAdapter);
```

