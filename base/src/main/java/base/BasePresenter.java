package base;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by John on 2016/8/1.
 */
public abstract class BasePresenter<M, V> {
    private boolean isAlive;
    public CompositeSubscription mSubscriptions;
    public M mModel;
    public V mView;
    //  public RxManager mRxManager = new RxManager();

    public void setVM(V v, M m) {
        this.mView = v;
        this.mModel = m;
        isAlive = true;
        mSubscriptions = new CompositeSubscription();
        this.onStart();
    }

    public abstract void onStart();

    protected   void subscribe() {

    }

    protected void unsubscribe() {
        mSubscriptions.unsubscribe();
    }

    public void onDestroy() {
        //mRxManager.clear();
        isAlive = false;
        mView = null;
        mModel = null;
    }

    public boolean isAlive() {
        return mView != null;
    }


}
