package ru.sawimmod.modules;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import protocol.Contact;
import protocol.Protocol;
import protocol.xmpp.XmppContact;
import ru.sawimmod.Options;
import ru.sawimmod.R;
import ru.sawimmod.activities.BaseActivity;
import ru.sawimmod.chat.message.Message;
import ru.sawimmod.comm.JLocale;
import ru.sawimmod.comm.Util;
import ru.sawimmod.io.Storage;
import ru.sawimmod.models.form.FormListener;
import ru.sawimmod.models.form.Forms;
import ru.sawimmod.models.list.VirtualList;
import ru.sawimmod.models.list.VirtualListModel;

import java.util.Vector;

public final class Answerer implements FormListener {
    private Vector dictionary = new Vector();
    private VirtualList list;
    private VirtualListModel model = new VirtualListModel();
    Forms form;

    private int selItem = 0;
    private static final int MENU_EDIT = 0;
    private static final int MENU_ADD = 1;
    private static final int MENU_CLEAR = 2;
    private static final int MENU_DELETE = 3;
    private static final int MENU_ON_OFF = 4;

    private static final int FORM_EDIT_QUESTION = 0;
    private static final int FORM_EDIT_ANSWER = 1;

    private Answerer() {
    }

    private static final Answerer instance = new Answerer();

    public static Answerer getInstance() {
        return instance;
    }

    public void activate() {
        list = VirtualList.getInstance();
        list.setCaption(JLocale.getString(R.string.answerer));
        refreshList();
        list.setOnBuildContextMenu(new VirtualList.OnBuildContextMenu() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, int listItem) {
                if (dictionary.size() > 0) {
                    menu.add(Menu.FIRST, MENU_EDIT, 2, JLocale.getString(R.string.edit));
                    menu.add(Menu.FIRST, MENU_DELETE, 2, JLocale.getString(R.string.delete));
                }
            }

            @Override
            public void onContextItemSelected(BaseActivity activity, int listItem, int itemMenuId) {
                switch (itemMenuId) {
                    case MENU_EDIT:
                        form = new Forms(R.string.answerer_dictionary, Answerer.this, false);
                        selItem = listItem;
                        form.clearForm();
                        form.addTextField(FORM_EDIT_QUESTION, JLocale.getString(R.string.answerer_question), getItemQuestion(listItem));
                        form.addTextField(FORM_EDIT_ANSWER, JLocale.getString(R.string.answerer_answer), getItemAnswer(listItem));
                        form.show(activity);
                        break;

                    case MENU_DELETE:
                        dictionary.removeElementAt(listItem);
                        save();
                        refreshList();
                        break;
                }
            }
        });
        list.setBuildOptionsMenu(new VirtualList.OnBuildOptionsMenu() {
            @Override
            public void onCreateOptionsMenu(Menu menu) {
                menu.add(Menu.FIRST, MENU_ADD, 2, JLocale.getString(R.string.add_new));
                menu.add(Menu.FIRST, MENU_CLEAR, 2, JLocale.getString(R.string.delete_all));
                if (Options.getBoolean(Options.OPTION_ANSWERER)) {
                    menu.add(Menu.FIRST, MENU_ON_OFF, 2, JLocale.getString(R.string.answerer_off));
                } else {
                    menu.add(Menu.FIRST, MENU_ON_OFF, 2, JLocale.getString(R.string.answerer_on));
                }
            }

            @Override
            public void onOptionsItemSelected(BaseActivity activity, MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_ADD:
                        form = new Forms(R.string.answerer_dictionary, Answerer.this, false);
                        dictionary.addElement(" = ");
                        selItem = dictionary.size() - 1;
                        form.clearForm();
                        form.addTextField(FORM_EDIT_QUESTION, JLocale.getString(R.string.answerer_question), "");
                        form.addTextField(FORM_EDIT_ANSWER, JLocale.getString(R.string.answerer_answer), "");
                        form.show(activity);
                        break;

                    case MENU_CLEAR:
                        popupAction();
                        Toast.makeText(activity, "All removed", Toast.LENGTH_SHORT).show();
                        break;

                    case MENU_ON_OFF:
                        Options.setBoolean(Options.OPTION_ANSWERER, !Options.getBoolean(Options.OPTION_ANSWERER));
                        Options.safeSave();
                        refreshList();
                        break;
                }
            }
        });
        list.show();
    }

    private void refreshList() {
        model.clear();
        int count = dictionary.size();
        for (int i = 0; i < count; ++i) {
            model.addItem((String) dictionary.elementAt(i), false);
        }
        list.setModel(model);
        list.updateModel();
    }

    public void load() {
        Storage storage = new Storage("module-answerer");
        try {
            storage.open();
            dictionary = storage.loadListOfString();
        } catch (Exception e) {
            dictionary = new Vector();
            DebugLog.panic("answerer dictionary load", e);
        }
        storage.close();
        DebugLog.println("answerer load: " + dictionary.size() + " items");
        if (0 == dictionary.size()) {
            dictionary.addElement("Hello=Hi. :-)");
            dictionary.addElement("Hi=H1 :-).");
            save();
        }
    }

    private void save() {
        Storage.delete("module-answerer");
        Storage storage = new Storage("module-answerer");
        try {
            if (0 == dictionary.size()) {
                return;
            }
            storage.open();
            storage.saveListOfString(dictionary);
        } catch (Exception e) {
            DebugLog.panic("answerer dictionary save", e);
        }
        storage.close();
    }

    public void checkMessage(Protocol protocol, Contact contact, Message message) {
        String getMyName = contact.getMyName();
        String msg = message.getText();
        String msgOfConf = "";
        if (getMyName != null && ru.sawimmod.chat.Chat.isHighlight(msg, getMyName)) {
            msgOfConf = msg.substring(getMyName.length() + 2);
        }
        for (int i = 0; i < dictionary.size(); ++i) {
            String getItemQuestion = getItemQuestion(i);
            String getItemAnswer = getItemAnswer(i);

            if (contact.isConference()) {
                if (msgOfConf.equalsIgnoreCase(getItemQuestion)) {
                    XmppContact toContact = (XmppContact) contact;
                    protocol.sendMessage(toContact, message.getName() + ru.sawimmod.chat.Chat.ADDRESS + getItemAnswer, true);
                }
            } else {
                if (msg.equalsIgnoreCase(getItemQuestion)) {
                    protocol.sendMessage(contact, getItemAnswer, true);
                }
            }
        }
    }

    private String getItemQuestion(int index) {
        return Util.explode((String) dictionary.elementAt(index), '=')[0];
    }

    private String getItemAnswer(int index) {
        return Util.explode((String) dictionary.elementAt(index), '=')[1];
    }

    public void popupAction() {
        dictionary.removeAllElements();
        save();
        list.back();
    }

    public void formAction(Forms gform, boolean apply) {
        if (form == gform) {
            if (apply) {
                String item = form.getTextFieldValue(FORM_EDIT_QUESTION) + "=" + form.getTextFieldValue(FORM_EDIT_ANSWER);
                dictionary.setElementAt(item, selItem);
                save();
                form.back();
                list.updateModel();
            } else {
                form.back();
            }
        }
    }
}
