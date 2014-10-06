package org.objectstyle.wolips.womodeler;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.objectstyle.wolips.preferences.PreferencesPlugin;
import org.objectstyle.wolips.womodeler.preferences.PreferenceConstants;
import org.objectstyle.wolips.womodeler.server.Webserver;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractBaseUIActivator implements IStartup {
  public static final int WOMODELER_PORT = 9485;
  public static final String PLUGIN_ID = "org.objectstyle.wolips.womodeler";

  private static Activator _plugin;

  private Webserver _server;

  /**
   * The constructor
   */
  public Activator() {
    _plugin = this;

    IPreferenceStore store = PreferencesPlugin.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.WOMODELER_SERVER_PORT, Activator.WOMODELER_PORT);
    store.setDefault(PreferenceConstants.WOMODELER_SERVER_ENABLED, false);

    _server = new Webserver(store.getInt(PreferenceConstants.WOMODELER_SERVER_PORT));
    _server.addRequestHandler("/womodeler", new WOModelerRequestHandler());
    _server.addRequestHandler("/refresh", new RefreshRequestHandler());
    _server.addRequestHandler("/openComponent", new OpenComponentRequestHandler());
  }

  public void earlyStartup() {
    if (PreferencesPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WOMODELER_SERVER_ENABLED)) {
      _server.start(true);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    _plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return _plugin;
  }
}
