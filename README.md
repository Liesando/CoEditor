# CoEditor

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
 
* Bootstrap
  
### Back-end

* Spring Framework

  In fact, Spring is a must-know standard of java-development. It offers several modules that allow you to speed-up your development.
* Pure JDBC
  
  Actually _we do not need_ anything more powerful than plain JDBC (Hibernate or Spring Data, for example) since there are no complex entities with lots of relations between them. Thus, it's much easier to implement a basic DB access service and use it everywhere we need an access to the database. 
  > _Though, this choice may change in some time._
* H2 database

  A simple light-weight DBMS - easy to use during development.
  
## How it works?

### Data model

We will use the following logical model of database.

![logical model](https://image.ibb.co/dgP8ko/Model.png)

Thus, we have files and their versions (optionally labeled with _version name_).
 > You may notice that we store the whole document itself. It is by design, but may change soon.

Model for the document looks like the following:
```java
public class Document {
    private int id;
    private String name;
    private String data;
    private LocalDateTime lastModification;
    private String versionLabel;
    
    // getters & setters
}
```

### Client

Client can create a new document or open an existing one. When document is loaded into the text area client is able to edit it as he wants.

The lifecycle of client-side code is quite straight-forward:
* on initialization ask the server for push and fetch intervals
* periodically check if there are any modifications to the document
  
  if they are then push the changes to the server
* periodically check if there is a newer version of document

  if it is then fetch it from the server and load into the text area
* periodically load lists of available documents and active users
  
### Server

On the server side there are following mappings for handling operations on documents:
* `GET  /rest/docs`
get _all documents_ without contents
* `POST /rest/docs/new`
_create a new_ document that is passed inside of request body
* `GET  /rest/docs/1`
get _contents_ of specified document
* `POST /rest/docs/update`
register _new version_ of document (passed inside of request body)
* `GET  /rest/docs/1/lastupdate`
get _date of the last version_ of the document specified with id

Also there are two mappings that tune client-side periods of pushing and fetching:
* `GET  /rest/push_interval`
get push interval
* `GET  /rest/fetch_interval`
get fetch interval

And the last one mapping provides the list (actually, simple string) of users that are currently working with the same document:
* `GET  /rest/docs/1/activeusers`
get users that are currently working with the document specified with id

#### Handling active users

Server has special logic for handling with active users of every opened document. 

If user wants to work with document he sends server a request with at least his authentication data (session id) and document id.

When server gets a request containing user's data (session id) and document id,
it adds a mapping for that document, indicating that there was seen this user's operations on this document,
in other words - user's activity.

This is implemented as simple `HashMap`, that maps `Integer` (document id) to another `HashMap`, 
that in turn maps username to last seen date. It may sound a bit complicated, but when it comes to code, it becomes more obvious:

```java
public class MainRestController {
    
    // some fields and constants
    
    // documentId -> {username, lastSeenDate}
    private HashMap<Integer, HashMap<String, Date>> activeUsers;
    
    // other logics
}
```

As one may expect the logic of handling active users is simple:
* if any authenticated request to the document happened - register user as active user of the document
* periodically refresh active users list: if some user were idle for too long consider him as offline 
and remove from the list

That's all! 