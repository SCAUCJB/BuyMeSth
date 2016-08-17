package base;

/**
 * Created by John on 2016/8/1.
 */
public abstract class BasePresenter <M, V> {
    private boolean isAlive;
    public M mModel;
    public V mView;
  //  public RxManager mRxManager = new RxManager();

    public void setVM(V v, M m) {
        this.mView = v;
        this.mModel = m;
        isAlive=true;
        this.onStart();
    }

    public abstract void onStart();

    public void onDestroy() {
        //mRxManager.clear();
        isAlive=false;
        mView=null;
        mModel=null;
    }

    public boolean isAlive() {
        return mView!=null;
    }



}
