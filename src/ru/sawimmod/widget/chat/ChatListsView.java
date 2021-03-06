package ru.sawimmod.widget.chat;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import ru.sawimmod.SawimResources;
import ru.sawimmod.widget.IcsLinearLayout;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 14.11.13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
public class ChatListsView extends IcsLinearLayout {

    public ChatListsView(Context context, boolean isTablet, View chatListView, View nickList) {
        super(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        setOrientation(HORIZONTAL);
        layoutParams.weight = 2;
        setLayoutParams(layoutParams);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (isTablet)
            lp.weight = 1;
        addViewInLayout(chatListView, 0, lp);

        if (isTablet) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 3;
            addViewInLayout(nickList, 1, lp);
        }
    }

    public void update() {
        setDividerDrawable(SawimResources.listDivider);
    }
}
