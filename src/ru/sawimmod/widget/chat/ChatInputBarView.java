package ru.sawimmod.widget.chat;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import ru.sawimmod.R;
import ru.sawimmod.SawimResources;
import ru.sawimmod.Scheme;
import ru.sawimmod.widget.IcsLinearLayout;
import ru.sawimmod.widget.Util;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 14.11.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class ChatInputBarView extends IcsLinearLayout {

    public ChatInputBarView(Context context, ImageButton menuButton, ImageButton smileButton, EditText messageEditor, ImageButton sendButton) {
        super(context);
        int padding = Util.dipToPixels(context, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setOrientation(HORIZONTAL);
        setPadding(padding, padding, padding, padding);
        setDividerPadding(padding);
        setLayoutParams(layoutParams);

        LinearLayout.LayoutParams menuButtonLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addViewInLayout(menuButton, 0, menuButtonLP);

        LinearLayout.LayoutParams smileButtonLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addViewInLayout(smileButton, 1, smileButtonLP);

        LinearLayout.LayoutParams messageEditorLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageEditorLP.gravity = Gravity.CENTER | Gravity.LEFT;
        messageEditorLP.weight = (float) 0.87;
        addViewInLayout(messageEditor, 2, messageEditorLP);

        LinearLayout.LayoutParams sendButtonLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addViewInLayout(sendButton, 3, sendButtonLP);
    }

    public void setImageButtons(ImageButton menuButton, ImageButton smileButton, ImageButton sendButton) {
        menuButton.setImageDrawable(SawimResources.ic_menu);
        smileButton.setImageResource(Scheme.isBlack() ? R.drawable.ic_emoji_dark : R.drawable.ic_emoji_light);
        sendButton.setImageResource(Scheme.isBlack() ? R.drawable.ic_send_dark : R.drawable.ic_send_light);
        setDividerDrawable(SawimResources.listDivider);
    }
}
