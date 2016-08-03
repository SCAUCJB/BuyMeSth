package adpater.listener;
import android.support.v7.widget.RecyclerView;
/**
 * Created by John on 2016/8/3.
 */

public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);
    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);
    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
