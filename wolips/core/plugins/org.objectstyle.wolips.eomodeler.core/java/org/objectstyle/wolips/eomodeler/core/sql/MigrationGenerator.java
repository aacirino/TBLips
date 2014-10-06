package org.objectstyle.wolips.eomodeler.core.sql;

import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.objectstyle.wolips.eomodeler.core.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityForest;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.thirdparty.velocity.WOLipsVelocityUtils;
import org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader;

public class MigrationGenerator {
	public static String generate(EOModel model, List<EOEntity> entities) throws Exception {
		List<EOEntity> generateEntities;
		if (entities == null || entities.isEmpty()) {
			generateEntities = new LinkedList<EOEntity>();
			for (EOEntity entity : model.getEntities()) {
				if (!entity.isPrototype()) {
					generateEntities.add(entity);
				}
			}
		} else {
			generateEntities = entities;
		}
		EOEntityForest entityForest = new EOEntityForest();
		entityForest.add(generateEntities);
		generateEntities = entityForest.breadthFirst();

		VelocityEngine velocityEngine = WOLipsVelocityUtils.createVelocityEngine("EOGenerator", Activator.getDefault().getBundle(), null, null, true, ResourceLoader.class);
		VelocityContext context = new VelocityContext();

		List<String> entityMigrations = new LinkedList<String>();
		for (EOEntity entity : generateEntities) {
			context.put("entity", entity);
			String entityMigration = WOLipsVelocityUtils.writeTemplateToString(velocityEngine, context, "EntityMigration0.java");
			entityMigrations.add(entityMigration);
		}

		List<String> foreignKeyMigrations = new LinkedList<String>();
		for (EOEntity entity : generateEntities) {
			context.put("entity", entity);
			String foreignKeyMigration = WOLipsVelocityUtils.writeTemplateToString(velocityEngine, context, "ForeignKeyMigration0.java");
			foreignKeyMigrations.add(foreignKeyMigration);
		}

		context.remove("entity");
		context.put("model", model);
		context.put("entityMigrations", entityMigrations);
		context.put("foreignKeyMigrations", foreignKeyMigrations);
		String modelMigration = WOLipsVelocityUtils.writeTemplateToString(velocityEngine, context, "Migration0.java");
		return modelMigration;
	}
}
