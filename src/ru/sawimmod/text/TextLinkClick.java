package ru.sawimmod.text;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import protocol.Protocol;
import protocol.xmpp.Jid;
import ru.sawimmod.Clipboard;
import ru.sawimmod.R;
import ru.sawimmod.activities.BaseActivity;
import ru.sawimmod.comm.Util;
import ru.sawimmod.modules.DebugLog;
import ru.sawimmod.view.PictureView;
import ru.sawimmod.view.menu.JuickMenu;
import ru.sawimmod.view.tasks.HtmlTask;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.11.13
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class TextLinkClick implements TextLinkClickListener {

    private Protocol currentProtocol;
    private String currentContact;

    public TextLinkClick(Protocol currentProtocol, String currentContact) {
        this.currentProtocol = currentProtocol;
        this.currentContact = currentContact;
    }

    @Override
    public void onTextLinkClick(View textView, String clickedString, boolean isLongTap) {
        if (clickedString.length() == 0) return;
        final BaseActivity activity = BaseActivity.getCurrentActivity();
        boolean isJuick = clickedString.startsWith("@") || clickedString.startsWith("#");
        if (isJuick) {
            new JuickMenu(activity, currentProtocol, currentContact, clickedString).show();
            return;
        }
        if (isLongTap || Jid.isJID(clickedString)) {
            CharSequence[] items = new CharSequence[2];
            items[0] = activity.getString(R.string.copy);
            items[1] = activity.getString(R.string.add_contact);
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setTitle(R.string.url_menu);
            final String finalClickedString = clickedString;
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Clipboard.setClipBoardText(activity, finalClickedString);
                            break;
                        case 1:
                            currentProtocol.getSearchForm().show(activity,
                                    Util.getUrlWithoutProtocol(finalClickedString), true);
                            break;
                    }
                }
            });
            try {
                builder.create().show();
            } catch (Exception e) {
                // WindowManager$BadTokenException will be caught and the app would not display
                DebugLog.panic("onTextLinkClick", e);
            }
        } else {
            if (!Util.isUrl(clickedString))
                clickedString = "http://" + clickedString;
            if (clickedString.toLowerCase().startsWith(HtmlTask.PIK4U)
                    || (clickedString.toLowerCase().startsWith("https://db.tt/"))
                    || Util.isImageFile(clickedString)) {
                PictureView pictureView = new PictureView();
                pictureView.setLink(clickedString);
                FragmentTransaction transaction = (activity).getSupportFragmentManager().beginTransaction();
                transaction.add(pictureView, PictureView.TAG);
                transaction.commitAllowingStateLoss();
            } else {
                Uri uri = Uri.parse(clickedString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());
                activity.startActivity(intent);
            }
        }
    }
}
