package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.objectstyle.wolips.eomodeler.utils.SelectionThrottle.IThrottledSelectionHandler;

public abstract class AbstractAddRemoveChangeRefresher<T> implements PropertyChangeListener, IThrottledSelectionHandler {
	private SelectionThrottle _selectionThrottle;

	public AbstractAddRemoveChangeRefresher(String name) {
		_selectionThrottle = new SelectionThrottle(name, this);
	}
	
	public void start() {
		_selectionThrottle.start();
	}

	public void stop() {
		_selectionThrottle.stop();
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent _event) {
		Collection<T> oldValues = (Collection<T>) _event.getOldValue();
		Collection<T> newValues = (Collection<T>) _event.getNewValue();
		if (newValues != null && oldValues != null) {
			if (newValues.size() > oldValues.size()) {
				List<T> newList = new LinkedList<T>(newValues);
				newList.removeAll(oldValues);
				objectsAdded(newList);
				_selectionThrottle.objectsAdded(newList);
			} else if (newValues.size() < oldValues.size()) {
				List<T> oldList = new LinkedList<T>(oldValues);
				oldList.removeAll(newValues);
				objectsRemoved(oldList);
				_selectionThrottle.objectsRemoved(oldList);
			}
		}
	}

	protected abstract void objectsAdded(List<T> _addedObjects);

	protected abstract void objectsRemoved(List<T> _removedObjects);
}
