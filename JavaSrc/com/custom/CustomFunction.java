package  com.custom; 
  
import static com.tibco.be.model.functions.FunctionDomain.ACTION;
import static com.tibco.be.model.functions.FunctionDomain.BUI;

import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;

import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.QueryIndexType;

import com.tibco.cep.kernel.model.entity.Entity;
import com.tibco.be.model.functions.BEFunction;
import com.tibco.be.model.functions.BEMapper;
import com.tibco.be.model.functions.BEPackage;
import com.tibco.be.model.functions.FunctionParamDescriptor;
import com.tibco.cep.ignite.cache.IgniteCacheProvider;
import com.tibco.cep.ignite.dao.IgniteEntityDao;
import com.tibco.cep.ignite.store.IgniteStoreProvider;

import com.tibco.datagrid.Connection;
import com.tibco.datagrid.DataGrid;
import com.tibco.datagrid.DataGridException;
import com.tibco.datagrid.Session;
import com.tibco.datagrid.Statement;
import com.tibco.datagrid.ResultSet;
import com.tibco.datagrid.Row;
import com.tibco.datagrid.RowIterator;
import com.tibco.datagrid.Column;
import com.tibco.datagrid.ColumnType;
import com.tibco.datagrid.TibDateTime;

@BEPackage(
		catalog = "CustomFunction",//Add a catalog name here
		category = "CustomFunction", //Add a category name here
		synopsis = "") //Add a synopsis here
public class CustomFunction{

	@BEFunction(
			name = "createIndex",
			signature = "void createIndex (conceptUri, cacheName)",
			params = {
					@FunctionParamDescriptor(name = "conceptUri", type = "String", desc = "" /*Add Description here*/),
					@FunctionParamDescriptor(name = "cacheName", type = "String", desc = "" /*Add Description here*/)
			},
			freturn = @FunctionParamDescriptor(name = "", type = "void", desc = "" /*Add Description here*/),
			version = "1.0", /*Add Version here*/
			see = "",
			mapper = @BEMapper(),
			description = "" /*Add Description here*/,
			cautions = "none",
			fndomain = {ACTION, BUI},
			example = ""
			)
	public static void createIndex(String conceptUri, String cacheName) {
		String fieldName = "_extId";
		IgniteCacheProvider cacheProvider = IgniteStoreProvider.INSTANCE.getCacheProvider();
		IgniteEntityDao igniteEntityDao = (IgniteEntityDao) cacheProvider.getEntityDao("be.gen" + conceptUri.replace('/', '.'));
		//igniteEntityDao.setIndex("_extId", true, cacheName);

		QueryIndex queryIndex = new QueryIndex(fieldName);
		queryIndex.setName("Index_" + cacheName + "_" + fieldName);
		queryIndex.setIndexType(QueryIndexType.SORTED);

		Collection<QueryIndex> queryIndexs = new HashSet<QueryIndex>();
		queryIndexs.add(queryIndex);

		QueryEntity queryEntity = new QueryEntity();
	   
//		Collection<QueryEntity> existingQueryEntities = cacheConfiguration.getQueryEntities();
//		if (null != existingQueryEntities && !existingQueryEntities.isEmpty()) {
//			queryEntity = existingQueryEntities.iterator().next();
//			queryEntity.getIndexes().addAll(queryIndexs);
//		} else {
			queryEntity.setIndexes(queryIndexs);
//		}
//		queryEntity.setValueType(BinaryObject.class.getName());
//		queryEntity.setKeyType(Long.class.getName());

//		Set<BinaryObject> keys = new HashSet<>();
//		binaryCache.query(new ScanQuery<>(null)).forEach(entry -> keys.add((BinaryObject)entry.getKey()));
//		Map<BinaryObject, BinaryObject> map = binaryCache.getAll(keys);
//		binaryCache.destroy();
//		binaryCache = getIgnite().getOrCreateCache(cacheConfiguration);
//		binaryCache.putAll(map);
	}

	@BEFunction(
			name = "setExtId",
			signature = "void setExtId (obj, extId)",
			params = {
					@FunctionParamDescriptor(name = "obj", type = "Entity", desc = "Concept or Event instance to reset extId"),
					@FunctionParamDescriptor(name = "extId", type = "String", desc = "new extId of the entity")
			},
			freturn = @FunctionParamDescriptor(name = "", type = "void", desc = ""),
			version = "1.0",
			see = "",
			mapper = @BEMapper(),
			description = "Set extId of a specified concept or event",
			cautions = "none",
			fndomain = {ACTION, BUI},
			example = ""
			)
	public static void setExtId(Entity obj, String extId) {
		obj.getId().setExtId(extId);
	}
   
	@BEFunction(
			name = "connectToAS",
			signature = "Object connectToAS(url, gridName, user, password)",
			params = {
					@FunctionParamDescriptor(name = "url", type = "String", desc = "ActiveSpaces connection URL, e.g., http://localhost:8080"),
					@FunctionParamDescriptor(name = "gridName", type = "String", desc = "ActiveSpaces grid name, e.g., _default"),
					@FunctionParamDescriptor(name = "user", type = "String", desc = "ActiveSpaces user name (optional), e.g., admin"),
					@FunctionParamDescriptor(name = "password", type = "String", desc = "ActiveSpaces user password (optional)")
			},
			freturn = @FunctionParamDescriptor(name = "", type = "Object", desc = "Session object of the AS connection"),
			version = "1.0",
			see = "",
			mapper = @BEMapper(),
			description = "Connect to ActiveSpaces data grid, and return the session object of the connection",
			cautions = "throws Exception if it fails with DataGridException",
			fndomain = {ACTION, BUI},
			example = ""
			)
	public static Object connectToAS(String url, String gridName, String user, String password) {
		try {
			Properties props = new Properties();
			if (user != null && password != null) {
				props.setProperty(Connection.TIBDG_CONNECTION_PROPERTY_STRING_USERNAME, user);
				props.setProperty(Connection.TIBDG_CONNECTION_PROPERTY_STRING_USERPASSWORD, password);
			}
			Connection connection = DataGrid.connect(url, gridName, props);
			return connection.createSession(props);
		} catch (DataGridException ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException(ex);
		}
	}

	@BEFunction(
			name = "executeUpdate",
			signature = "long executeUpdate(session, sql, timeout)",
			params = {
					@FunctionParamDescriptor(name = "session", type = "Object", desc = "Session of an ActiveSpaces connection, which is returned by the connectToAS()"),
					@FunctionParamDescriptor(name = "sql", type = "String", desc = "SQL statement for Update or delete"),
					@FunctionParamDescriptor(name = "timeout", type = "long", desc = "max wait time in seconds")
			},
			freturn = @FunctionParamDescriptor(name = "", type = "long", desc = "Number of records impacted by the SQL statement"),
			version = "1.0",
			see = "",
			mapper = @BEMapper(),
			description = "Execute a SQL update or delete on an ActiveSpaces data grid",
			cautions = "throws Exception if the execution fails with DataGridException",
			fndomain = {ACTION, BUI},
			example = ""
			)
	public static long executeUpdate(Object session, String sql, long timeout) {
		Statement statement = null;
		long rows = 0;
		try {
			Session sess = (Session) session;
			Properties props = new Properties();
			if (timeout > 0) {
				props.setProperty(Statement.TIBDG_STATEMENT_PROPERTY_DOUBLE_UPDATE_TIMEOUT, String.valueOf(timeout));
			}
			statement = sess.createStatement(sql, props);
			rows = statement.executeUpdate(props);
		} catch (DataGridException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return rows;
	}
	
	@BEFunction(
			name = "executeQuery",
			signature = "Object executeQuery(session, sql, timeout)",
			params = {
					@FunctionParamDescriptor(name = "session", type = "Object", desc = "Session of an ActiveSpaces connection, which is returned by the connectToAS()"),
					@FunctionParamDescriptor(name = "sql", type = "String", desc = "SQL select statement"),
					@FunctionParamDescriptor(name = "timeout", type = "long", desc = "max wait time in seconds")
			},
			freturn = @FunctionParamDescriptor(name = "", type = "Object", desc = "List of string array containing column values returned by the SQL statement"),
			version = "1.0",
			see = "",
			mapper = @BEMapper(),
			description = "Execute a SQL select on an ActiveSpaces data grid",
			cautions = "throws Exception if the execution fails with DataGridException",
			fndomain = {ACTION, BUI},
			example = ""
			)
	public static Object executeQuery(Object session, String sql, long timeout) {
		Statement statement = null;
		ResultSet resultset = null;
		ArrayList<String[]> result = new ArrayList<String[]>();
		try {
			Session sess = (Session) session;
			Properties props = new Properties();
			if (timeout > 0) {
				props.setProperty(Statement.TIBDG_STATEMENT_PROPERTY_DOUBLE_FETCH_TIMEOUT, String.valueOf(timeout));
			}
			statement = sess.createStatement(sql, props);
			int cols = statement.getResultSetMetadata().getColumnCount();
			resultset = statement.executeQuery(props);
			for (Row row : resultset) {
				RowIterator iter = row.iterator();
				int i = 0;
				String[] data = new String[cols];
				while (iter.hasNext()) {
					String value = "";
					Column col = iter.next();
					switch(col.getColumnType()) {
						case STRING:
							value = row.getString(col.getName());
							break;
						case LONG:
							long l = row.getLong(col.getName());
							value = String.valueOf(l);
							break;
						case DOUBLE:
							double f = row.getDouble(col.getName());
							value = String.valueOf(f);
							break;
						case DATETIME:
							TibDateTime d = row.getDateTime(col.getName());
							if (d != null) {
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								value = df.format(d.toDate());
							}
							break;
						case OPAQUE:
							byte[] q = row.getOpaque(col.getName());
							value = new String(q);
							break;
						default:
							break;
					}
					data[i] = value;
					i++;
					if (i >= cols) {
						break;
					}
				}
				try {
					row.destroy();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
				result.add(data);
			}
		} catch (DataGridException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (resultset != null) {
				try {
					resultset.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return result;
	}
}