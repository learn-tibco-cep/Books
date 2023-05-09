# Books

## Overview

In this tutorial, we use the [Open Library Data Dumps](https://openlibrary.org/developers/dumps) to demonstrate data persistence in BE applications.  BusinessEvents supports many different types of data cache and persistent stores.  You may select and configure a persistent store at deployment time by providing a CDD file.

This project has configured the following commonly used persistent stores and presented implementation of CRUD (Create, Query, Update, and Delete) operations for single concept or bulk of 1000's concepts.

* [Books.cdd](./Books.cdd) - Apache Ignite data grid with no persistence
* [BooksPG.cdd](./BooksPG.cdd) - Apache Ignite data grid with Postgresql as the backing store
* [BooksAS4.cdd](./BooksAS4.cdd) - Apache Ignite data grid with ActiveSpaces 4.x as the backing store
* [BooksCas.cdd](./BooksCas.cdd) - Apache Ignite data grid with Cassandra 4.1 as the backing store
* [BooksAS4store.cdd](./BooksAS4store.cdd) - ActiveSpaces 4.x as the persistent store with no cache cluster

Detailed description for this tutorial can be found in [Wiki page](https://github.com/learn-tibco-cep/tutorials/wiki/Data-Persistence).

## Components

* [Concepts](./Concepts) - This folder contains concept definitions based on [Open Library](https://openlibrary.org/developers/dumps) JSON schema.
* [Load](./RuleFunctions/Load) - This folder contains implementation for loading Open-Library data from files.
* [Query](./Query) - This folder contains implementation for querying data cache and persistent stores.
* [JavaSrc](./JavaSrc) - This folder contains custom Java functions for supporting query operations.
* [Preprocessors](./RuleFunctions/Preprocessors) - This folder contains implementation of processors for CRUD operation events.
* [Rules](./Rules) - This folder contains implementation of rules for updating application data.
* [Test](./Test) - This folder contains test cases and data files used by functional tests.

## Build and Run Unit Tests

Clone this repository, and import it into BE studio workspace, `$WS`, as an `Existing TIBCO BE Studio Project`.  Build the enterprise archive, e.g., `$WS/Books.ear`.

Following steps use Apache Ignite with no backing store.  Other persistence options are described in the above referenced `Wiki page`.

### Start Ignite cache node

```
$BE_HOME/bin/be-engine --propFile $BE_HOME/bin/be-engine.tra -n cache-0 -u cache -c ${WS}/Books.cdd Books.ear
```

### Start query node (optional)

The query node demonstrates `continuous query` that prints out object counts every time a concept is created.  The verboseness of the print out is configured in the query PU by the property `books.app.bql.callback.window = -1`.

Skip this step if you are not interested in `continuous query`.

```
$BE_HOME/bin/be-engine --propFile $BE_HOME/bin/be-engine.tra -n query -u query -c ${WS}/Books.cdd Books.ear
```

### Start inference node

The inference node contains an inference agent and a query agent, and so all queries are processed by the same node.

To run all unit tests after the inference node starts, you can set the following cluster-level properties in `Books.cdd`:

```
books.app.unitTests = true
tibco.clientVar.FileLoader/project_root = /path/to/project/Books
```
These properties will make the inference engine automatically schedule unit tests that read input data from the project source folder `Books/Test/Data`.

Start the inference node by using the updated `Books.cdd`:

```
$BE_HOME/bin/be-engine --propFile $BE_HOME/bin/be-engine.tra -n default-0 -u default -c ${WS}/Books.cdd Books.ear
```

## Test Cases

In the `Books.cdd`, a system property, `books.app.unitTests = true`, is set to schedule all functional tests when the inference node starts.  The implementation of the following test cases demonstrate a few platform capabilities for managing application data in BusinessEvents.

1. [Load data from file](./Test/T01_LoadTests.rule):
   * Read data file - [onLoadFromFile](./RuleFunctions/Preprocessors/onLoadFromFile.rulefunction)
   * Create a concept from JSON string - [onCreateConcept](./RuleFunctions/Preprocessors/onCreateConcept.rulefunction)
   * Multi-threaded operation using a local event channel - [onLoadFromFile](./RuleFunctions/Preprocessors/onLoadFromFile.rulefunction)
   * Create bulk of concepts in single RTC - [createConcepts](./RuleFunctions/Load/createConcepts.rulefunction)
1. [Create new revisions from query result](./Test/T02_RevisionTests.rule):
   * Create new revisions of a concept by deep copy - [createRevisions](./RuleFunctions/createRevisions.rulefunction)
   * Retrieve primary keys by using native cache query or direct store query - [queryLimitedConceptIds](./Query/queryLimitedConceptIds.rulefunction)
1. [Update concepts by using rules](./Test/T03_UpdateTests.rule):
   * Fetch concept by primary key in preprocessor - [onUpdateConcept](./RuleFunctions/Preprocessors/onUpdateConcept.rulefunction)
   * Update concept in rules - e.g., [UpdateAuthor](./Rules/UpdateAuthor.rule)
1. [Delete concepts](./Test/T04_DeleteTests.rule):
   * Fetch and delete a concept in preprocessor - [onDeleteConcept](./RuleFunctions/Preprocessors/onDeleteConcept.rulefunction)
   * Delete bulk of concepts by using native cache query or direct store query - [deleteRevisions](./Query/deleteRevisions.rulefunction)
1. [Query concepts](./Test/T05_QueryTests.rule):
   * Query concepts by using BQL - [queryConcepts](./Query/queryConcepts.rulefunction)
   * Invoke aggregate query by using preprocessor in query agent - [onAggregateQuery](./Query/Preprocessors/onAggregateQuery.rulefunction)
   * Retrieve column values by using native cache query or direct store query - [queryBooksByAuthor](./Query/queryBooksByAuthor.rulefunction)
1. [Revoke changes of concepts by modified date](./Test/T06_RevokeTests.rule):
   * Retrieve primary keys of modified concepts by using BQL or store query - [queryModifiedConceptIds](./Query/queryModifiedConceptIds.rulefunction)
   * Update bulk of concepts by asserting many concepts in a RTC - [onUpdateBatch](./RuleFunctions/Preprocessors/onUpdateBatch.rulefunction)
1. [Count concepts in cache and/or persistent store](./Test/T07_CountConcepts.rule):
   * Count concepts of a specified type by using native cache query or custom Java function - [countConcepts](./Query/countConcepts.rulefunction)
   * Query limited number of rows of a specified type by using native cache query or custom Java function - [queryTopRows](./Query/queryTopRows.rulefunction)
1. Continuous Query in a query node
   * Set `books.app.bql.continuous.count = true` in a query agent
   * A callback function, [bqlCallback](./Query/bqlCallback) will be invoked when new concepts are created.  The callback will print out new changes in an interval specified by the property `books.app.bql.callback.window`.