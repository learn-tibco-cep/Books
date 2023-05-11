/******************************************************************************
    Description: This file defines all necessary tables needed to manage Cassandra
    persistence of BusinessEvents® working memory objects
 *****************************************************************************/
USE perf;

DROP TABLE IF EXISTS ClassToTable;
CREATE TABLE IF NOT EXISTS ClassToTable(fieldName TEXT, className TEXT, tableName TEXT,  PRIMARY KEY (className));

DROP TABLE IF EXISTS CacheIds;
CREATE TABLE IF NOT EXISTS CacheIds(cacheIdGeneratorName TEXT, nextCacheId BIGINT,  PRIMARY KEY (cacheIdGeneratorName));

DROP TABLE IF EXISTS BEAliases;
CREATE TABLE IF NOT EXISTS BEAliases(beName TEXT, alias TEXT,  PRIMARY KEY (beName));

DROP TABLE IF EXISTS ClassRegistry;
CREATE TABLE IF NOT EXISTS ClassRegistry(classname TEXT, typeid INT,  PRIMARY KEY (classname));

DROP TABLE IF EXISTS StateMachineTimeout;
CREATE TABLE IF NOT EXISTS StateMachineTimeout(fired BIGINT, uid uuid, currenttime BIGINT, state_ TEXT, propertyName TEXT, nexttime BIGINT, time_created_ FROZEN<TUPLE<TIMESTAMP, TEXT>>, id_ BIGINT, extid TEXT, closure TEXT, ttl BIGINT,  PRIMARY KEY ( uid ));

DROP TABLE IF EXISTS WorkItemTable;
CREATE TABLE IF NOT EXISTS WorkItemTable(workStatus BIGINT, scheduledTime BIGINT, work BLOB, workKey TEXT, workQueue TEXT,  PRIMARY KEY (workKey,workQueue));

DROP TABLE IF EXISTS ObjectTable;
CREATE TABLE IF NOT EXISTS ObjectTable(isDeleted INT, id_ BIGINT, globalid BIGINT, extid TEXT, className TEXT, SITEID BIGINT,  PRIMARY KEY (globalid));

DROP TABLE IF EXISTS ProcessLoopState;
CREATE TABLE IF NOT EXISTS ProcessLoopState(loopKey TEXT, maxCounter INT, jobKey TEXT, taskName TEXT, counter INT, isComplete INT,  PRIMARY KEY (loopKey));

DROP TABLE IF EXISTS ProcessMergeState;
CREATE TABLE IF NOT EXISTS ProcessMergeState(uid uuid, isError INT, processId BIGINT, tokenCount INT, expectedTokenCount INT, processTime BIGINT, mergeKey TEXT, isComplete INT, transitionName TEXT,  PRIMARY KEY ( uid ));

DROP INDEX IF EXISTS i_WorkItems_workQueue;
CREATE INDEX IF NOT EXISTS i_WorkItems_workQueue on WorkItemTable(workQueue);

DROP INDEX IF EXISTS i_WorkItems_scheduledTime;
CREATE INDEX IF NOT EXISTS i_WorkItems_scheduledTime on WorkItemTable(scheduledTime);

DROP INDEX IF EXISTS i_ObjectTable_extId;
CREATE INDEX IF NOT EXISTS i_ObjectTable_extId on ObjectTable(extid);

DROP INDEX IF EXISTS i_ObjectTable_id;
CREATE INDEX IF NOT EXISTS i_ObjectTable_id on ObjectTable(id_);

DROP INDEX IF EXISTS i_ObjectTable_isDeleted;
CREATE INDEX IF NOT EXISTS i_ObjectTable_isDeleted on ObjectTable(isDeleted);

DROP INDEX IF EXISTS i_ProcessMergeState;
CREATE INDEX IF NOT EXISTS i_ProcessMergeState on ProcessMergeState(mergeKey);
