package protocol;

import android.view.ContextMenu;
import android.view.Menu;
import protocol.xmpp.XmppServiceContact;
import ru.sawimmod.Clipboard;
import ru.sawimmod.R;
import ru.sawimmod.SawimApplication;
import ru.sawimmod.Scheme;
import ru.sawimmod.activities.BaseActivity;
import ru.sawimmod.comm.JLocale;
import ru.sawimmod.comm.StringConvertor;
import ru.sawimmod.comm.Util;
import ru.sawimmod.icons.Icon;
import ru.sawimmod.models.list.VirtualList;
import ru.sawimmod.models.list.VirtualListItem;
import ru.sawimmod.models.list.VirtualListModel;
import ru.sawimmod.modules.DebugLog;

import java.util.List;

public final class StatusView {

    private Protocol protocol;
    private Contact contact;
    private String clientVersion;
    private String userRole;
    private VirtualListModel model;
    private VirtualList list;

    public static final int INFO_MENU_COPY = 1;
    public static final int INFO_MENU_COPY_ALL = 2;

    public StatusView() {
    }

    public void addClient() {
        if ((ClientInfo.CLI_NONE != contact.clientIndex)
                && (null != protocol.clientInfo)) {
            addPlain(protocol.clientInfo.getIcon(contact.clientIndex),
                    (protocol.clientInfo.getName(contact.clientIndex)
                            + " " + contact.version).trim());
        }
        addPlain(null, clientVersion);
        addPlain(null, userRole);
    }

    public void setClientVersion(String version) {
        clientVersion = version;
    }

    public void setUserRole(String role) {
        userRole = role;
    }

    public void addTime() {
        if (!contact.isSingleUserContact()) {
            return;
        }
        if (contact instanceof XmppServiceContact) {
            return;
        }
        long signonTime = contact.chaingingStatusTime;
        if (0 < signonTime) {
            long now = SawimApplication.getCurrentGmtTime();
            boolean today = (now - 24 * 60 * 60) < signonTime;
            if (contact.isOnline()) {
                addInfo(R.string.li_signon_time, Util.getLocalDateString(signonTime, today));
                addInfo(R.string.li_online_time, Util.longitudeToString(now - signonTime));
            } else {
                addInfo(R.string.li_signoff_time, Util.getLocalDateString(signonTime, today));
            }
        }
    }

    public void addPlain(Icon img, String str) {
        if (!StringConvertor.isEmpty(str)) {
            VirtualListItem line = model.createNewParser(true);
            if (null != img) {
                line.addImage(img.getImage());
            }
            line.addDescription(str, Scheme.THEME_TEXT, Scheme.FONT_STYLE_PLAIN);
            model.addPar(line);
            list.updateModel();
        }
    }

    public void addStatusText(String text) {
        if (!StringConvertor.isEmpty(text)) {
            VirtualListItem line = model.createNewParser(true);
            line.addDescription(text, Scheme.THEME_PARAM_VALUE, Scheme.FONT_STYLE_PLAIN);
            model.addPar(line);
            list.updateModel();
        }
    }

    public void addInfo(String key, String value) {
        model.addParam(key, value);
        list.updateModel();
    }

    public void addInfo(int key, String value) {
        model.addParam(key, value);
        list.updateModel();
    }

    public void addContactStatus() {
        byte status = contact.getStatusIndex();
        StatusInfo info = protocol.getStatusInfo();
        addStatus(info.getIcon(status), info.getName(status));
    }

    public void addXStatus() {
        XStatusInfo info = protocol.getXStatusInfo();
        int x = contact.getXStatusIndex();
        addStatus(info.getIcon(x), JLocale.getString(info.getName(x)));
    }

    public void addStatus(Icon icon, String name) {
        addPlain(icon, name);
    }

    public void init(Protocol p, Contact c) {
        list = VirtualList.getInstance();
        model = new VirtualListModel();
        list.setModel(model);
        list.setOnBuildContextMenu(new VirtualList.OnBuildContextMenu() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, int listItem) {
                menu.add(Menu.FIRST, INFO_MENU_COPY, 2, R.string.copy_text);
                menu.add(Menu.FIRST, INFO_MENU_COPY_ALL, 2, R.string.copy_all_text);
            }

            @Override
            public void onContextItemSelected(BaseActivity activity, int listItem, int itemMenuId) {
                switch (itemMenuId) {
                    case INFO_MENU_COPY:
                        VirtualListItem item = list.getModel().elements.get(listItem);
                        Clipboard.setClipBoardText(activity, ((item.getLabel() == null) ? "" : item.getLabel() + "\n") + item.getDescStr());
                        break;

                    case INFO_MENU_COPY_ALL:
                        StringBuilder s = new StringBuilder();
                        List<VirtualListItem> listItems = list.getModel().elements;
                        for (int i = 0; i < listItems.size(); ++i) {
                            CharSequence label = listItems.get(i).getLabel();
                            CharSequence descStr = listItems.get(i).getDescStr();
                            if (label != null)
                                s.append(label).append("\n");
                            if (descStr != null)
                                s.append(descStr).append("\n");
                        }
                        Clipboard.setClipBoardText(activity, s.toString());
                        break;
                }
            }
        });
        contact = c;
        protocol = p;
        clientVersion = null;
        userRole = null;
    }

    public void initUI() {
        model.clear();
        list.setCaption(contact.getName());
        addInfo(protocol.getUserIdName(), contact.getUserId());
    }

    public Contact getContact() {
        return contact;
    }

    public void showIt() {
        list.show();
    }
}