package titan.ast.runtime;

import java.util.EventListener;
import java.util.Objects;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * .
 *
 * @author tian wei jun
 */
class TreeViewerModel {
  EventListenerList listenerList = new EventListenerList();
  transient ChangeEvent changeEvent = null;
  private StringTree stringTree;
  private float scale = 1.5f;

  TreeViewerModel() {}

  float getScale() {
    return scale;
  }

  void setScale(float scale) {
    setTreeViewerModelProperties(stringTree, scale);
  }

  StringTree getStringTree() {
    return stringTree;
  }

  void setStringTree(StringTree stringTree) {
    setTreeViewerModelProperties(stringTree, scale);
  }

  void setTreeViewerModelProperties(StringTree stringTree, float scale) {
    if (this.stringTree != stringTree || this.scale != scale) {
      this.stringTree = stringTree;
      this.scale = scale;
      fireStateChanged();
    }
  }

  void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }

  void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }

  ChangeListener[] getChangeListeners() {
    return listenerList.getListeners(ChangeListener.class);
  }

  protected void fireStateChanged() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
      }
    }
  }

  <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    return listenerList.getListeners(listenerType);
  }

  boolean equalsByProperties(TreeViewerModel that) {
    if (this == that) return true;
    if (that == null) return false;
    return Float.compare(scale, that.scale) == 0 && Objects.equals(stringTree, that.stringTree);
  }

  TreeViewerModel copyProperties() {
    TreeViewerModel treeViewerModel = new TreeViewerModel();
    treeViewerModel.stringTree = stringTree;
    treeViewerModel.scale = scale;
    return treeViewerModel;
  }
}
