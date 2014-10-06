package org.objectstyle.wolips.eomodeler.core.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class EOFSQLGeneratorFactory implements IEOSQLGeneratorFactory {
	public IEOSQLGenerator sqlGenerator(EOModel model, List<String> entityNames, EODatabaseConfig databaseConfig, ClassLoader eomodelClassLoader, boolean runInEntityModeler) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		WOUtils.setWOSystemProperties();
		String className = "org.objectstyle.wolips.eomodeler.core.sql.EOFSQLGenerator53";
		Class sqlGeneratorClass = Class.forName(className, true, eomodelClassLoader);

		List<URL> modelURLs = new LinkedList();
		// AK: I hope this does the right thing... we add all other models
		// before the one in question
		Iterator modelsIter = model.getModelGroup().getModels().iterator();
		while (modelsIter.hasNext()) {
			EOModel otherModel = (EOModel) modelsIter.next();
			if (otherModel != model) {
				URL otherModelURL = otherModel.getModelURL();
				modelURLs.add(WOUtils.trimModelURLs(otherModelURL));
			}
		}
		modelURLs.add(WOUtils.trimModelURLs(model.getModelURL()));

		Constructor sqlGeneratorConstructor = sqlGeneratorClass.getConstructor(new Class[] { String.class, List.class, List.class, Map.class, boolean.class });
		if (databaseConfig == null) {
			throw new IllegalStateException("Database config is not defined");
		}
		Object sqlGeneratorButICantCastItBecauseItCrossesClassLoaders = sqlGeneratorConstructor.newInstance(new Object[] { model.getName(), modelURLs, entityNames, databaseConfig.toMap().getBackingMap(), Boolean.valueOf(runInEntityModeler) });
		IEOSQLGenerator sqlGenerator = new ReflectionSQLGenerator(sqlGeneratorButICantCastItBecauseItCrossesClassLoaders);
		return sqlGenerator;
	}

	protected static class ReflectionSQLGenerator implements IEOSQLGenerator {
		private Object _eofSQLGenerator;

		public ReflectionSQLGenerator(Object eofSQLGenerator) {
			_eofSQLGenerator = eofSQLGenerator;
		}

		public void executeSQL(String sql) throws SQLException {
			try {
				_eofSQLGenerator.getClass().getMethod("executeSQL", String.class).invoke(_eofSQLGenerator, sql);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to generate SQL.", e);
			}
		}

		public String generateSchemaCreationScript(Map flagsMap) {
			try {
				return (String) _eofSQLGenerator.getClass().getMethod("generateSchemaCreationScript", Map.class).invoke(_eofSQLGenerator, flagsMap);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to generate SQL.", e);
			}
		}
	}
}
