package titan.ast.runtime;

import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * .
 *
 * @author tian wei jun
 */
public class TreeViewerModel {
  protected EventListenerList listenerList = new EventListenerList();
  protected transient ChangeEvent changeEvent = null;
  private StringTree stringTree;
  private float scale = 1.5f;

  public TreeViewerModel() {}

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    setTreeViewerModelProperties(stringTree, scale);
  }

  public StringTree getStringTree() {
    return stringTree;
  }

  public void setStringTree(StringTree stringTree) {
    setTreeViewerModelProperties(stringTree, scale);
  }

  public void setTreeViewerModelProperties(StringTree stringTree, float scale) {
    if (this.stringTree != stringTree || this.scale != scale) {
      this.stringTree = stringTree;
      this.scale = scale;
      fireStateChanged();
    }
  }

  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }

  public void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }

  public ChangeListener[] getChangeListeners() {
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

  public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    return listenerList.getListeners(listenerType);
  }
}
