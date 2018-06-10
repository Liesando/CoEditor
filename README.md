# CoEditor

## Contents

- [What is CoEditor?](#what-is-coeditor)
- [Frameworks & Instruments used](#frameworks--instruments-used)
- [How it works](#how-it-works)
   - [Client](#client)
   - [Server](#server)
- [How to launch the app](#how-to-launch-the-app)
- [Tweaking application parameters](#tweaking-application-parameters)


## What is CoEditor?

**CoEditor** is a service that allows you to create and edit a plain-text document in a concurrent way. This means that there may be a big amount of users working with single document at the same time.
Right now there are several examples of similar services:
* [Google Docs](https://docs.google.com/)
* [Microsoft Office Online](https://products.office.com/ru-ru/office-online/documents-spreadsheets-presentations-office-online)
* [Zoho Docs](https://www.zoho.eu/docs/)

In turn, this service is determined to be one more _collaborative-browser-editor_.

> _**Notice**: this project is a test task and is not supposed to replace services listed above. If you need a reliable stable solution use one of the above-mentioned instead._

## Frameworks & Instruments used

### Front-end

* [Vue.js](https://ru.vuejs.org/v2/guide/index.html)

  Vue.js is a simple (yet powerful) light-weight framework for developing UI. It was chosen due to its simplicity (in contrast to Angular).
* [axios](https://github.com/axios/axios)

  A library that allows ajax-requests and handling responses.
 
* [BootstrapVue](https://bootstrap-vue.js.org/)

* [Froala WYSIWYG Editor](https://www.froala.com/wysiwyg-editor)
  
### Back-end

* Spring Framework

  In fact, Spring is a must-know standard of java-development. 
  It offers several modules that allow you to speed-up your development.
  In this project the following modules are used: Spring Boot, Spring IoC, Spring Security, Spring TestContext.
* Hibernate

  This ORM-framework does all the routine database-stuff work for you, thus reducing the amount of utility code.
* H2 database

  A simple light-weight DBMS - easy to use during development.
  
## How it works

### Data model

We will use the following logical model of database.

![logical model](https://image.ibb.co/i0b8Sd/Model.png)

Thus, we have documents and their versions (optionally labeled with _version name_).
 > You may notice that we store the whole document itself. It is by design, but may change soon.

Model for the document looks like the following:
```java
public class Document {
    private int id;
    private String name;
    
    // getters & setters
}
```

Model for the document version is a bit more complicated, because of some subtle Hibernate requirements:
```java
public class DocumentVersion {
    
    public static class IdTimePk implements Serializable {
        private int documentId;
        private LocalDateTime modificationTime;

        // getters & setters
    }
    
    private IdTimePk primaryKey;
    private String data;
    private String versionLabel;
    private Document document;
    
    // getters & setters
}
```

All the actual document work is performed with `DocumentVersion`s.

### Client

In order to be allowed to work with documents client have to be logged in.

After successful authentication client is able to:
* create a new document or open existing one;

* edit loaded document;

* save current version of document with specified version label;

* load version of document specified by version label.

---

The lifecycle of client-side code is quite straight-forward:
* on initialization ask the server for push and fetch intervals;

* periodically check if there are any modifications to the document;
  
  if they are then push the changes to the server;
* periodically check if there is a newer version of document;

  if it is then fetch it from the server and load into the text area;

* periodically load:
  - list of available documents;
  - list of active users;
  - list of current document versions.
  
### Server

On the server side there are following mappings for handling operations on documents:
* `GET  /rest/docs`
get _all documents_ without contents;

* `POST /rest/docs`
_create new_ document;

* `PUT /rest/docs`
_register new_ document version with current text area's contents;

* `PATCH /rest/docs`
_label_ current document version with _version name_ (the whole document is passed inside request body);

* `GET  /rest/docs/1`
get _contents_ of specified document

* `GET /rest/docs/1/version/version name`
get version of document with id `1` and labelled as `version name`;

* `GET /rest/docs/1/version/all`
get list of all versions of document;

* `GET  /rest/docs/1/lastupdate`
get _the date of the last version_ of the document specified with id;

* `GET /rest/docs/1/activeusers`
get a string that contains current active users' nicknames.

Also there are two mappings that tune client-side periods of pushing and fetching:
* `GET  /rest/config/push_interval`
get push interval;

* `GET  /rest/config/fetch_interval`
get fetch interval.

> Notice: that's prohibited for document version label to be `all` or 
to contain question marks `?`. Reasons for that are the following: the `/rest/docs/1/versions/all`
mapping is already registered for controller's needs and `?` symbol
is treated in some special way by Spring.  

#### Handling active users

Server has special logic for handling with active users of every opened document. 

If user wants to work with document he sends a request to the server that at least contains 
his _authentication data_ (session id) and _document id_.

When server gets a request containing user's data (session id) and document id,
it remembers that there was seen this user's operations on that exact document.
In other words - user's activity.

As one may expect the logic of handling active users is simple:
* if any authenticated request to the document happens - remember user as active user of the document;

* periodically refresh active users list: if some user is idle for too long consider him offline 
and remove from the list.

## How to launch the app

Current dev-version of the app is ready out-of-the-box. All you need is to checkout (or download .zip) this repository's
dev-branch, open it in IntelliJ IDEA (or your favorite IDE) and press the _Launch_ button.

> Actually, you may also need to set up JDK for the project.

> Database workflow is designed to create in your linux home folder db-files starting with _coeditor_.
It was not tested whether these database files are created correctly on Windows or MacOS platform. 
Please, let me know by emailing me at smedelyan@yandex.ru if you are experiencing any issues with that.

## Tweaking application parameters

There is an `application.properties` configuration file ([here](../dev/src/main/resources/application.properties))
that contains some parameters explained below:

* `server.port`  
what port should server start on;

* `coeditor.db.url`  
database connection string;

* `coeditor.rest.push_interval`  
how often (once for every `push_interval` milliseconds) client must attempt to push changes;

* `coeditor.rest.fetch_interval`  
how often (once for every `fetch_interval` milliseconds) client must attempt to fetch changes;

* `coeditor.rest.users_check_delay`  
how often (once for every `USERS_CHECK_DELAY` milliseconds) server must check for non-active users;

* `coeditor.rest.active_user_expire_time`  
how much time (in milliseconds) must pass since the last user's request
to treat him as offline;

* `coeditor.rest.active_user_collapse_size`  
if there are more then this amount of users working with the document, then
server shall return a string like `"Active users: user1, user2, user3, ... and N more"`
on request of currently active users;  
obviously, there are no more than ACTIVE_USER_COLLAPSE_SIZE users' names listed.  