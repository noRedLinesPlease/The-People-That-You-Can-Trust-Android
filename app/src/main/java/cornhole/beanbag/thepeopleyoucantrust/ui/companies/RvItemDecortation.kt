package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RvItemDecoration(space: Int) : RecyclerView.ItemDecoration() {
    private val halfSpace: Int
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.paddingLeft != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace)
            parent.clipToPadding = false
        }
        outRect.top = halfSpace
        outRect.bottom = halfSpace
        outRect.left = halfSpace
        outRect.right = halfSpace
    }

    init {
        halfSpace = space / 2
    }
}