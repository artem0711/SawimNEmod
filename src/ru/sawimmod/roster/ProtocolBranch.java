package ru.sawimmod.roster;

import protocol.Contact;
import protocol.Protocol;
import ru.sawimmod.Options;
import ru.sawimmod.comm.Util;

import java.util.Vector;

public class ProtocolBranch extends TreeBranch {

    private Protocol protocol;

    public ProtocolBranch(Protocol p) {
        protocol = p;
        setExpandFlag(false);
    }

    public boolean isProtocol(Protocol p) {
        return protocol == p;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public boolean isEmpty() {
        if (Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE)) {
            Vector contacts = protocol.getContactItems();
            for (int i = contacts.size() - 1; 0 <= i; --i) {
                if (((Contact) contacts.elementAt(i)).isVisibleInContactList()) {
                    return false;
                }
            }
            return true;
        }
        return (0 == protocol.getContactItems().size())
                && (0 == protocol.getGroupItems().size());
    }

    @Override
    public byte getType() {
        return PROTOCOL;
    }

    public String getText() {
        return protocol.getUserId();
    }

    public int getNodeWeight() {
        return 0;
    }

    public void sort() {
        synchronized (protocol.getRosterLockObject()) {
            if (Options.getBoolean(Options.OPTION_USER_GROUPS)) {
                Util.sort(protocol.getGroupItems());
            } else {
                Util.sort(protocol.getContactItems());
            }
        }
    }
}