package com.hasgeek.funnel.data;

import com.hasgeek.funnel.model.Attendee;
import com.hasgeek.funnel.model.ContactExchangeContact;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ContactExchangeController {


    public static RealmResults<Attendee> getAttendeesBySpaceId_Hot(Realm realm, String spaceId) {
        if (spaceId.equals("117"))
            spaceId = "116";
        return realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .findAll();
    }

    public static List<Attendee> getAttendeesBySpaceId_Cold(Realm realm, String spaceId) {
        RealmResults<Attendee> attendeeRealmResults = getAttendeesBySpaceId_Hot(realm, spaceId);

        if (attendeeRealmResults.size() > 0)
            return realm.copyFromRealm(attendeeRealmResults);

        return new ArrayList<>();
    }


    public static Attendee getAttendeeBySpaceIdAndPuk_Hot(Realm realm, String spaceId, String puk) {
        if (spaceId.equals("117"))
            spaceId = "116";
        return realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .equalTo("puk", puk)
                .findFirst();
    }

    public static Attendee getAttendeeBySpaceIdAndPuk_Cold(Realm realm, String spaceId, String puk) {
        Attendee attendee = getAttendeeBySpaceIdAndPuk_Hot(realm, spaceId, puk);

        if (attendee != null)
            return realm.copyFromRealm(attendee);

        return null;
    }

    public static void deleteAndSaveAttendeesBySpaceId(Realm realm, String spaceId, List<Attendee> attendeeList) {
        if (spaceId.equals("117"))
            spaceId = "116";
        realm.beginTransaction();
        realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .findAll()
                .deleteAllFromRealm();
        realm.copyToRealmOrUpdate(attendeeList);
        realm.commitTransaction();
    }

    public static void deleteAttendeesBySpaceId(Realm realm, String spaceId) {
        if (spaceId.equals("117"))
            spaceId = "116";
        realm.beginTransaction();
        realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void saveContactExchangeContacts(Realm realm, List<ContactExchangeContact> contactExchangeContacts) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contactExchangeContacts);
        realm.commitTransaction();
    }

    public static RealmResults<ContactExchangeContact> getContactExchangeContactsBySpaceId_Hot(Realm realm, String spaceId) {
        if (spaceId.equals("117"))
            spaceId = "116";
        return realm.where(ContactExchangeContact.class)
                .equalTo("space.id", spaceId)
                .findAll();
    }

    public static List<ContactExchangeContact> getContactExchangeContactsBySpaceId_Cold(Realm realm, String spaceId) {

        RealmResults<ContactExchangeContact> contactExchangeContactRealmResults = getContactExchangeContactsBySpaceId_Hot(realm, spaceId);

        if (contactExchangeContactRealmResults.size() > 0)
            return realm.copyFromRealm(contactExchangeContactRealmResults);

        return new ArrayList<>();
    }

    public static void addContactExchangeContact(Realm realm, ContactExchangeContact contactExchangeContact) {
        realm.beginTransaction();
        contactExchangeContact.setSynced(false);
        realm.copyToRealmOrUpdate(contactExchangeContact);
        realm.commitTransaction();
    }


    public static ContactExchangeContact getContactExchangeFromAttendee(Attendee attendee) {
        ContactExchangeContact contactExchangeContact = new ContactExchangeContact();

        contactExchangeContact.setId(attendee.getId());
        contactExchangeContact.setCompany(attendee.getCompany());
        contactExchangeContact.setFullname(attendee.getFullname());
        contactExchangeContact.setJobTitle(attendee.getJobTitle());
        contactExchangeContact.setPuk(attendee.getPuk());
        contactExchangeContact.setSpace(attendee.getSpace());

        return contactExchangeContact;
    }

    public static List<ContactExchangeContact> getUnsyncedContactExchangeContactsBySpaceId_Cold(Realm realm, String spaceId) {
        RealmResults<ContactExchangeContact> contactExchangeContactRealmResults = getContactExchangeContactsBySpaceId_Hot(realm, spaceId).where().equalTo("synced", false).findAll();

        if (contactExchangeContactRealmResults.size() != 0)
            return realm.copyFromRealm(contactExchangeContactRealmResults);

        return new ArrayList<>();
    }


    public static ContactExchangeContact getContactExchangeContactFromPukAndKeyAndSpaceId_Hot(Realm realm, String puk, String key, String spaceId) {
        if (spaceId.equals("117"))
            spaceId = "116";
        Attendee a = realm.where(Attendee.class)
                .equalTo("space.id", spaceId)
                .equalTo("puk", puk)
                .findFirst();

        if (a == null)
            return null;

        ContactExchangeContact contactExchangeContact = getContactExchangeFromAttendee(a);
        contactExchangeContact.setKey(key);
        return contactExchangeContact;
    }

    public static void updateContactExchangeContact(Realm realm, ContactExchangeContact contactExchangeContact) {
        realm.beginTransaction();
        realm.insertOrUpdate(contactExchangeContact);
        realm.commitTransaction();
    }


    public static void deleteContactExchangeContact(Realm realm, ContactExchangeContact contactExchangeContact) {
        realm.beginTransaction();
        try {
            realm.copyToRealm(contactExchangeContact).deleteFromRealm();
        } catch (Exception e) {
            try {
                contactExchangeContact.deleteFromRealm();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        realm.commitTransaction();
    }


    public static String getContactExchangeContactsAsCSVStringFromSpaceId(Realm realm, String spaceId) {
        if (spaceId.equals("117"))
            spaceId = "116";
        RealmResults<ContactExchangeContact> contactExchangeContactRealmResults = realm.where(ContactExchangeContact.class)
                .equalTo("space.id", spaceId)
                .equalTo("synced", true)
                .findAll();

        StringBuilder data = new StringBuilder("fullname,company,jobtitle,email,phone,twitter\n");

        for (ContactExchangeContact contact: contactExchangeContactRealmResults) {
            data.append(contact.getFullname());
            data.append(",");
            data.append(contact.getCompany());
            data.append(",");
            data.append(contact.getJobTitle());
            data.append(",");
            data.append(contact.getEmail());
            data.append(",");
            data.append(contact.getPhone());
            data.append(",");
            data.append(contact.getTwitter());
            data.append("\n");
        }

        return data.toString();
    }



}
