package ru.sawimmod.forms;

import protocol.Protocol;
import ru.sawimmod.R;
import ru.sawimmod.activities.BaseActivity;
import ru.sawimmod.models.form.FormListener;
import ru.sawimmod.models.form.Forms;
import ru.sawimmod.modules.DebugLog;
import ru.sawimmod.modules.search.UserInfo;

public class EditInfo implements FormListener {

    private static final int _NickNameItem = 1000;
    private static final int _FirstNameItem = 1001;
    private static final int _LastNameItem = 1002;
    private static final int _EmailItem = 1003;
    private static final int _BdayItem = 1004;
    private static final int _CellPhoneItem = 1005;
    private static final int _AddrItem = 1006;
    private static final int _CityItem = 1007;
    private static final int _StateItem = 1008;
    private static final int _SexItem = 1009;
    private static final int _HomePageItem = 1010;
    private static final int _WorkCompanyItem = 1011;
    private static final int _WorkDepartmentItem = 1012;
    private static final int _WorkPositionItem = 1013;
    private static final int _WorkPhoneItem = 1014;
    private static final int _AboutItem = 1015;
    private static final int _ContactJID = 1016;
    private static final int _ExtAddr = 1017;
    private static final int _PostalCode = 1018;
    private static final int _Country = 1019;
    private Forms form;
    private Protocol protocol;
    private UserInfo userInfo;

    public EditInfo(Protocol p, UserInfo info) {
        protocol = p;
        this.userInfo = info;
    }

    public EditInfo init() {
        final boolean isXmpp = (protocol instanceof protocol.xmpp.Xmpp);
        form = new Forms(R.string.editform, this, false);

        form.addTextField(_NickNameItem, R.string.nick, userInfo.nick);
        form.addTextField(_FirstNameItem, R.string.firstname, userInfo.firstName);
        form.addTextField(_LastNameItem, R.string.lastname, userInfo.lastName);
        int[] sexItem = {-1, R.string.female, R.string.male};
        form.addSelector(_SexItem, R.string.gender, sexItem, userInfo.gender);

        form.addTextField(_BdayItem, R.string.birth_day, userInfo.birthDay);

        if (isXmpp) {
            form.addTextField(_EmailItem, R.string.email, userInfo.email);
            form.addTextField(_CellPhoneItem, R.string.cell_phone, userInfo.cellPhone);
            form.addTextField(_ContactJID, R.string.contact_jid, userInfo.contactjid);
        }

        form.addTextField(_HomePageItem, R.string.home_page, userInfo.homePage);

        form.addHeader(R.string.home_info);

        if (isXmpp) {
            form.addTextField(_AddrItem, R.string.addr, userInfo.homeAddress);
            form.addTextField(_ExtAddr, R.string.ext_add, userInfo.homeExtended);
        }

        form.addTextField(_CityItem, R.string.city, userInfo.homeCity);
        form.addTextField(_StateItem, R.string.state, userInfo.homeState);

        if (isXmpp) {
            form.addTextField(_PostalCode, R.string.postcode, userInfo.homePostal);
            form.addTextField(_Country, R.string.country, userInfo.homeCountry);
        }

        form.addHeader(R.string.work_info);
        form.addTextField(_WorkCompanyItem, R.string.title, userInfo.workCompany);
        form.addTextField(_WorkDepartmentItem, R.string.depart, userInfo.workDepartment);
        form.addTextField(_WorkPositionItem, R.string.position, userInfo.workPosition);

        if (isXmpp) {
            form.addTextField(_WorkPhoneItem, R.string.phone, userInfo.workPhone);
            form.addTextField(_AboutItem, R.string.notes, userInfo.about);
        }

        return this;
    }

    public void show(BaseActivity activity) {
        form.show(activity);
    }

    private void destroy() {
        protocol = null;
        form = null;
        userInfo = null;
    }

    @Override
    public void formAction(Forms form, boolean apply) {
        if (!apply) {
            form.back();
            destroy();

        } else {
            boolean isXmpp = (protocol instanceof protocol.xmpp.Xmpp);
            userInfo.nick = form.getTextFieldValue(_NickNameItem);
            userInfo.birthDay = form.getTextFieldValue(_BdayItem);
            if (isXmpp) {
                userInfo.email = form.getTextFieldValue(_EmailItem);
                userInfo.cellPhone = form.getTextFieldValue(_CellPhoneItem);
                userInfo.contactjid = form.getTextFieldValue(_ContactJID);
            }

            userInfo.firstName = form.getTextFieldValue(_FirstNameItem);
            userInfo.lastName = form.getTextFieldValue(_LastNameItem);
            if (!isXmpp) {
                userInfo.gender = (byte) form.getSelectorValue(_SexItem);
            } else {
                userInfo.xmpp_gender = form.getSelectorString(_SexItem);
            }
            userInfo.homePage = form.getTextFieldValue(_HomePageItem);

            if (isXmpp) {
                userInfo.homeAddress = form.getTextFieldValue(_AddrItem);
                userInfo.homeExtended = form.getTextFieldValue(_ExtAddr);
                userInfo.homePostal = form.getTextFieldValue(_PostalCode);
                userInfo.homeCountry = form.getTextFieldValue(_Country);
            }

            userInfo.homeCity = form.getTextFieldValue(_CityItem);
            userInfo.homeState = form.getTextFieldValue(_StateItem);

            userInfo.workCompany = form.getTextFieldValue(_WorkCompanyItem);
            userInfo.workDepartment = form.getTextFieldValue(_WorkDepartmentItem);
            userInfo.workPosition = form.getTextFieldValue(_WorkPositionItem);

            if (isXmpp) {
                userInfo.workPhone = form.getTextFieldValue(_WorkPhoneItem);
                userInfo.about = form.getTextFieldValue(_AboutItem);
            }

            userInfo.updateProfileView();
            protocol.saveUserInfo(userInfo);
            form.back();
            destroy();
        }
    }
}