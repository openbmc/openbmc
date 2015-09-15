#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2008        Intel Corporation
#
# Authored by Rob Bradford <rob@linux.intel.com>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import gtk
import gobject
import gtk.glade
import threading
import urllib2
import os
import contextlib

from bb.ui.crumbs.buildmanager import BuildManager, BuildConfiguration
from bb.ui.crumbs.buildmanager import BuildManagerTreeView

from bb.ui.crumbs.runningbuild import RunningBuild, RunningBuildTreeView

# The metadata loader is used by the BuildSetupDialog to download the
# available options to populate the dialog
class MetaDataLoader(gobject.GObject):
    """ This class provides the mechanism for loading the metadata (the
    fetching and parsing) from a given URL. The metadata encompasses details
    on what machines are available. The distribution and images available for
    the machine and the the uris to use for building the given machine."""
    __gsignals__ = {
        'success' : (gobject.SIGNAL_RUN_LAST,
                     gobject.TYPE_NONE,
                     ()),
        'error' : (gobject.SIGNAL_RUN_LAST,
                   gobject.TYPE_NONE,
                   (gobject.TYPE_STRING,))
        }

    # We use these little helper functions to ensure that we take the gdk lock
    # when emitting the signal. These functions are called as idles (so that
    # they happen in the gtk / main thread's main loop.
    def emit_error_signal (self, remark):
        gtk.gdk.threads_enter()
        self.emit ("error", remark)
        gtk.gdk.threads_leave()

    def emit_success_signal (self):
        gtk.gdk.threads_enter()
        self.emit ("success")
        gtk.gdk.threads_leave()

    def __init__ (self):
        gobject.GObject.__init__ (self)

    class LoaderThread(threading.Thread):
        """ This class provides an asynchronous loader for the metadata (by
        using threads and signals). This is useful since the metadata may be
        at a remote URL."""
        class LoaderImportException (Exception):
            pass

        def __init__(self, loader, url):
            threading.Thread.__init__ (self)
            self.url = url
            self.loader = loader

        def run (self):
            result = {}
            try:
                with contextlib.closing (urllib2.urlopen (self.url)) as f:
                    # Parse the metadata format. The format is....
                    # <machine>;<default distro>|<distro>...;<default image>|<image>...;<type##url>|...
                    for line in f:
                        components = line.split(";")
                        if (len (components) < 4):
                            raise MetaDataLoader.LoaderThread.LoaderImportException
                        machine = components[0]
                        distros = components[1].split("|")
                        images = components[2].split("|")
                        urls = components[3].split("|")

                        result[machine] = (distros, images, urls)

                # Create an object representing this *potential*
                # configuration. It can become concrete if the machine, distro
                # and image are all chosen in the UI
                configuration = BuildConfiguration()
                configuration.metadata_url = self.url
                configuration.machine_options = result
                self.loader.configuration = configuration

                # Emit that we've actually got a configuration
                gobject.idle_add (MetaDataLoader.emit_success_signal,
                    self.loader)

            except MetaDataLoader.LoaderThread.LoaderImportException as e:
                gobject.idle_add (MetaDataLoader.emit_error_signal, self.loader,
                    "Repository metadata corrupt")
            except Exception as e:
                gobject.idle_add (MetaDataLoader.emit_error_signal, self.loader,
                    "Unable to download repository metadata")
                print(e)

    def try_fetch_from_url (self, url):
        # Try and download the metadata. Firing a signal if successful
        thread = MetaDataLoader.LoaderThread(self, url)
        thread.start()

class BuildSetupDialog (gtk.Dialog):
    RESPONSE_BUILD = 1

    # A little helper method that just sets the states on the widgets based on
    # whether we've got good metadata or not.
    def set_configurable (self, configurable):
        if (self.configurable == configurable):
            return

        self.configurable = configurable
        for widget in self.conf_widgets:
            widget.set_sensitive (configurable)

        if not configurable:
            self.machine_combo.set_active (-1)
            self.distribution_combo.set_active (-1)
            self.image_combo.set_active (-1)

    # GTK widget callbacks
    def refresh_button_clicked (self, button):
        # Refresh button clicked.

        url = self.location_entry.get_chars (0, -1)
        self.loader.try_fetch_from_url(url)

    def repository_entry_editable_changed (self, entry):
        if (len (entry.get_chars (0, -1)) > 0):
            self.refresh_button.set_sensitive (True)
        else:
            self.refresh_button.set_sensitive (False)
            self.clear_status_message()

        # If we were previously configurable we are no longer since the
        # location entry has been changed
        self.set_configurable (False)

    def machine_combo_changed (self, combobox):
        active_iter = combobox.get_active_iter()

        if not active_iter:
            return

        model = combobox.get_model()

        if model:
            chosen_machine = model.get (active_iter, 0)[0]

        (distros_model, images_model) = \
            self.loader.configuration.get_distro_and_images_models (chosen_machine)

        self.distribution_combo.set_model (distros_model)
        self.image_combo.set_model (images_model)

    # Callbacks from the loader
    def loader_success_cb (self, loader):
        self.status_image.set_from_icon_name ("info",
            gtk.ICON_SIZE_BUTTON)
        self.status_image.show()
        self.status_label.set_label ("Repository metadata successfully downloaded")

        # Set the models on the combo boxes based on the models generated from
        # the configuration that the loader has created

        # We just need to set the machine here, that then determines the
        # distro and image options. Cunning huh? :-)

        self.configuration = self.loader.configuration
        model = self.configuration.get_machines_model ()
        self.machine_combo.set_model (model)

        self.set_configurable (True)

    def loader_error_cb (self, loader, message):
        self.status_image.set_from_icon_name ("error",
            gtk.ICON_SIZE_BUTTON)
        self.status_image.show()
        self.status_label.set_text ("Error downloading repository metadata")
        for widget in self.conf_widgets:
            widget.set_sensitive (False)

    def clear_status_message (self):
        self.status_image.hide()
        self.status_label.set_label (
            """<i>Enter the repository location and press _Refresh</i>""")

    def __init__ (self):
        gtk.Dialog.__init__ (self)

        # Cancel
        self.add_button (gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL)

        # Build
        button = gtk.Button ("_Build", None, True)
        image = gtk.Image ()
        image.set_from_stock (gtk.STOCK_EXECUTE, gtk.ICON_SIZE_BUTTON)
        button.set_image (image)
        self.add_action_widget (button, BuildSetupDialog.RESPONSE_BUILD)
        button.show_all ()

        # Pull in *just* the table from the Glade XML data.
        gxml = gtk.glade.XML (os.path.dirname(__file__) + "/crumbs/puccho.glade",
            root = "build_table")
        table = gxml.get_widget ("build_table")
        self.vbox.pack_start (table, True, False, 0)

        # Grab all the widgets that we need to turn on/off when we refresh...
        self.conf_widgets = []
        self.conf_widgets += [gxml.get_widget ("machine_label")]
        self.conf_widgets += [gxml.get_widget ("distribution_label")]
        self.conf_widgets += [gxml.get_widget ("image_label")]
        self.conf_widgets += [gxml.get_widget ("machine_combo")]
        self.conf_widgets += [gxml.get_widget ("distribution_combo")]
        self.conf_widgets += [gxml.get_widget ("image_combo")]

        # Grab the status widgets
        self.status_image = gxml.get_widget ("status_image")
        self.status_label = gxml.get_widget ("status_label")

        # Grab the refresh button and connect to the clicked signal
        self.refresh_button = gxml.get_widget ("refresh_button")
        self.refresh_button.connect ("clicked", self.refresh_button_clicked)

        # Grab the location entry and connect to editable::changed
        self.location_entry = gxml.get_widget ("location_entry")
        self.location_entry.connect ("changed",
            self.repository_entry_editable_changed)

        # Grab the machine combo and hook onto the changed signal. This then
        # allows us to populate the distro and image combos
        self.machine_combo = gxml.get_widget ("machine_combo")
        self.machine_combo.connect ("changed", self.machine_combo_changed)

        # Setup the combo
        cell = gtk.CellRendererText()
        self.machine_combo.pack_start(cell, True)
        self.machine_combo.add_attribute(cell, 'text', 0)

        # Grab the distro and image combos. We need these to populate with
        # models once the machine is chosen
        self.distribution_combo = gxml.get_widget ("distribution_combo")
        cell = gtk.CellRendererText()
        self.distribution_combo.pack_start(cell, True)
        self.distribution_combo.add_attribute(cell, 'text', 0)

        self.image_combo = gxml.get_widget ("image_combo")
        cell = gtk.CellRendererText()
        self.image_combo.pack_start(cell, True)
        self.image_combo.add_attribute(cell, 'text', 0)

        # Put the default descriptive text in the status box
        self.clear_status_message()

        # Mark as non-configurable, this is just greys out the widgets the
        # user can't yet use
        self.configurable = False
        self.set_configurable(False)

        # Show the table
        table.show_all ()

        # The loader and some signals connected to it to update the status
        # area
        self.loader = MetaDataLoader()
        self.loader.connect ("success", self.loader_success_cb)
        self.loader.connect ("error", self.loader_error_cb)

    def update_configuration (self):
        """ A poorly named function but it updates the internal configuration
        from the widgets. This can make that configuration concrete and can
        thus be used for building """
        # Extract the chosen machine from the combo
        model = self.machine_combo.get_model()
        active_iter = self.machine_combo.get_active_iter()
        if (active_iter):
            self.configuration.machine = model.get(active_iter, 0)[0]

        # Extract the chosen distro from the combo
        model = self.distribution_combo.get_model()
        active_iter = self.distribution_combo.get_active_iter()
        if (active_iter):
            self.configuration.distro = model.get(active_iter, 0)[0]

        # Extract the chosen image from the combo
        model = self.image_combo.get_model()
        active_iter = self.image_combo.get_active_iter()
        if (active_iter):
            self.configuration.image = model.get(active_iter, 0)[0]

# This function operates to pull events out from the event queue and then push
# them into the RunningBuild (which then drives the RunningBuild which then
# pushes through and updates the progress tree view.)
#
# TODO: Should be a method on the RunningBuild class
def event_handle_timeout (eventHandler, build):
    # Consume as many messages as we can ...
    event = eventHandler.getEvent()
    while event:
        build.handle_event (event)
        event = eventHandler.getEvent()
    return True

class MainWindow (gtk.Window):

    # Callback that gets fired when the user hits a button in the
    # BuildSetupDialog.
    def build_dialog_box_response_cb (self, dialog, response_id):
        conf = None
        if (response_id == BuildSetupDialog.RESPONSE_BUILD):
            dialog.update_configuration()
            print(dialog.configuration.machine, dialog.configuration.distro, \
                dialog.configuration.image)
            conf = dialog.configuration

        dialog.destroy()

        if conf:
            self.manager.do_build (conf)

    def build_button_clicked_cb (self, button):
        dialog = BuildSetupDialog ()

        # For some unknown reason Dialog.run causes nice little deadlocks ... :-(
        dialog.connect ("response", self.build_dialog_box_response_cb)
        dialog.show()

    def __init__ (self):
        gtk.Window.__init__ (self)

        # Pull in *just* the main vbox from the Glade XML data and then pack
        # that inside the window
        gxml = gtk.glade.XML (os.path.dirname(__file__) + "/crumbs/puccho.glade",
            root = "main_window_vbox")
        vbox = gxml.get_widget ("main_window_vbox")
        self.add (vbox)

        # Create the tree views for the build manager view and the progress view
        self.build_manager_view = BuildManagerTreeView()
        self.running_build_view = RunningBuildTreeView()

        # Grab the scrolled windows that we put the tree views into
        self.results_scrolledwindow = gxml.get_widget ("results_scrolledwindow")
        self.progress_scrolledwindow = gxml.get_widget ("progress_scrolledwindow")

        # Put the tree views inside ...
        self.results_scrolledwindow.add (self.build_manager_view)
        self.progress_scrolledwindow.add (self.running_build_view)

        # Hook up the build button...
        self.build_button = gxml.get_widget ("main_toolbutton_build")
        self.build_button.connect ("clicked", self.build_button_clicked_cb)

# I'm not very happy about the current ownership of the RunningBuild. I have
# my suspicions that this object should be held by the BuildManager since we
# care about the signals in the manager

def running_build_succeeded_cb (running_build, manager):
    # Notify the manager that a build has succeeded. This is necessary as part
    # of the 'hack' that we use for making the row in the model / view
    # representing the ongoing build change into a row representing the
    # completed build. Since we know only one build can be running a time then
    # we can handle this.

    # FIXME: Refactor all this so that the RunningBuild is owned by the
    # BuildManager. It can then hook onto the signals directly and drive
    # interesting things it cares about.
    manager.notify_build_succeeded ()
    print("build succeeded")

def running_build_failed_cb (running_build, manager):
    # As above
    print("build failed")
    manager.notify_build_failed ()

def main (server, eventHandler):
    # Initialise threading...
    gobject.threads_init()
    gtk.gdk.threads_init()

    main_window = MainWindow ()
    main_window.show_all ()

    # Set up the build manager stuff in general
    builds_dir = os.path.join (os.getcwd(),  "results")
    manager = BuildManager (server, builds_dir)
    main_window.build_manager_view.set_model (manager.model)

    # Do the running build setup
    running_build = RunningBuild ()
    main_window.running_build_view.set_model (running_build.model)
    running_build.connect ("build-succeeded", running_build_succeeded_cb,
        manager)
    running_build.connect ("build-failed", running_build_failed_cb, manager)

    # We need to save the manager into the MainWindow so that the toolbar
    # button can use it.
    # FIXME: Refactor ?
    main_window.manager = manager

    # Use a timeout function for probing the event queue to find out if we
    # have a message waiting for us.
    gobject.timeout_add (200,
                         event_handle_timeout,
                         eventHandler,
                         running_build)

    gtk.main()
