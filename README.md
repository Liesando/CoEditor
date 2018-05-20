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
> _This section is a short overview of used frameworks and instruments with mentioned reasons of choice._

### Front-end

* [Vue.js](https://ru.vuejs.org/v2/guide/index.html)

  Vue.js is a simple (yet powerful) light-weight framework for developing UI. It was chosen due to its simplicity (in contrast to Angular).
* [axios](https://github.com/axios/axios)

  A library that allows ajax-requests and handling responses.
  
### Back-end

* Spring Framework

  In fact, Spring is a must-know standard of java-development. It offers several modules that allow you to speed-up your development.
* Pure JDBC
  
  Actually _we do not need_ anything more powerful than plain JDBC (Hibernate or Spring Data, for example) since there are no complex entities with lots of relations between them. Thus, it's much easier to implement a basic DB access service and use it everywhere we need an access to the database. 
  > _Though, this choice may change in some time._
* H2 database

  A simple light-weight DBMS - easy to use during development.
