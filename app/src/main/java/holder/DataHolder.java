package holder;

/**
 * Created by Hau on 2017/11/3.
 */
import android.content.Context;

/**
 * 数据控制器
 * ----------
 * 将ItemData和ViewHolder进行绑定，并设置监听
 */
public interface DataHolder<T> {

    void bind(Context context, SuperViewHolder holder, T item, int position);
}
