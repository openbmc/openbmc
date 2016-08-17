#
# BitBake Graphical GTK based Dependency Explorer
#
# Copyright (C) 2007        Ross Burton
# Copyright (C) 2007 - 2008 Richard Purdie
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

import sys
import gobject
import gtk
import Queue
import threading
import xmlrpclib
import bb
import bb.event
from bb.ui.crumbs.progressbar import HobProgressBar

# Package Model
(COL_PKG_NAME) = (0)

# Dependency Model
(TYPE_DEP, TYPE_RDEP) = (0, 1)
(COL_DEP_TYPE, COL_DEP_PARENT, COL_DEP_PACKAGE) = (0, 1, 2)


class PackageDepView(gtk.TreeView):
    def __init__(self, model, dep_type, label):
        gtk.TreeView.__init__(self)
        self.current = None
        self.dep_type = dep_type
        self.filter_model = model.filter_new()
        self.filter_model.set_visible_func(self._filter)
        self.set_model(self.filter_model)
        #self.connect("row-activated", self.on_package_activated, COL_DEP_PACKAGE)
        self.append_column(gtk.TreeViewColumn(label, gtk.CellRendererText(), text=COL_DEP_PACKAGE))

    def _filter(self, model, iter):
        (this_type, package) = model.get(iter, COL_DEP_TYPE, COL_DEP_PARENT)
        if this_type != self.dep_type: return False
        return package == self.current

    def set_current_package(self, package):
        self.current = package
        self.filter_model.refilter()


class PackageReverseDepView(gtk.TreeView):
    def __init__(self, model, label):
        gtk.TreeView.__init__(self)
        self.current = None
        self.filter_model = model.filter_new()
        self.filter_model.set_visible_func(self._filter)
        self.set_model(self.filter_model)
        self.append_column(gtk.TreeViewColumn(label, gtk.CellRendererText(), text=COL_DEP_PARENT))

    def _filter(self, model, iter):
        package = model.get_value(iter, COL_DEP_PACKAGE)
        return package == self.current

    def set_current_package(self, package):
        self.current = package
        self.filter_model.refilter()


class DepExplorer(gtk.Window):
    def __init__(self):
        gtk.Window.__init__(self)
        self.set_title("Dependency Explorer")
        self.set_default_size(500, 500)
        self.connect("delete-event", gtk.main_quit)

        # Create the data models
        self.pkg_model = gtk.ListStore(gobject.TYPE_STRING)
        self.pkg_model.set_sort_column_id(COL_PKG_NAME, gtk.SORT_ASCENDING)
        self.depends_model = gtk.ListStore(gobject.TYPE_INT, gobject.TYPE_STRING, gobject.TYPE_STRING)
        self.depends_model.set_sort_column_id(COL_DEP_PACKAGE, gtk.SORT_ASCENDING)

        pane = gtk.HPaned()
        pane.set_position(250)
        self.add(pane)

        # The master list of packages
        scrolled = gtk.ScrolledWindow()
        scrolled.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
        scrolled.set_shadow_type(gtk.SHADOW_IN)

        self.pkg_treeview = gtk.TreeView(self.pkg_model)
        self.pkg_treeview.get_selection().connect("changed", self.on_cursor_changed)
        column = gtk.TreeViewColumn("Package", gtk.CellRendererText(), text=COL_PKG_NAME)
        self.pkg_treeview.append_column(column)
        pane.add1(scrolled)
        scrolled.add(self.pkg_treeview)

        box = gtk.VBox(homogeneous=True, spacing=4)

        # Runtime Depends
        scrolled = gtk.ScrolledWindow()
        scrolled.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
        scrolled.set_shadow_type(gtk.SHADOW_IN)
        self.rdep_treeview = PackageDepView(self.depends_model, TYPE_RDEP, "Runtime Depends")
        self.rdep_treeview.connect("row-activated", self.on_package_activated, COL_DEP_PACKAGE)
        scrolled.add(self.rdep_treeview)
        box.add(scrolled)

        # Build Depends
        scrolled = gtk.ScrolledWindow()
        scrolled.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
        scrolled.set_shadow_type(gtk.SHADOW_IN)
        self.dep_treeview = PackageDepView(self.depends_model, TYPE_DEP, "Build Depends")
        self.dep_treeview.connect("row-activated", self.on_package_activated, COL_DEP_PACKAGE)
        scrolled.add(self.dep_treeview)
        box.add(scrolled)
        pane.add2(box)

        # Reverse Depends
        scrolled = gtk.ScrolledWindow()
        scrolled.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
        scrolled.set_shadow_type(gtk.SHADOW_IN)
        self.revdep_treeview = PackageReverseDepView(self.depends_model, "Reverse Depends")
        self.revdep_treeview.connect("row-activated", self.on_package_activated, COL_DEP_PARENT)
        scrolled.add(self.revdep_treeview)
        box.add(scrolled)
        pane.add2(box)

        self.show_all()

    def on_package_activated(self, treeview, path, column, data_col):
        model = treeview.get_model()
        package = model.get_value(model.get_iter(path), data_col)

        pkg_path = []
        def finder(model, path, iter, needle):
            package = model.get_value(iter, COL_PKG_NAME)
            if package == needle:
                pkg_path.append(path)
                return True
            else:
                return False
        self.pkg_model.foreach(finder, package)
        if pkg_path:
            self.pkg_treeview.get_selection().select_path(pkg_path[0])
            self.pkg_treeview.scroll_to_cell(pkg_path[0])

    def on_cursor_changed(self, selection):
        (model, it) = selection.get_selected()
        if it is None:
            current_package = None
        else:
            current_package = model.get_value(it, COL_PKG_NAME)
        self.rdep_treeview.set_current_package(current_package)
        self.dep_treeview.set_current_package(current_package)
        self.revdep_treeview.set_current_package(current_package)


    def parse(self, depgraph):
        for package in depgraph["pn"]:
            self.pkg_model.insert(0, (package,))

        for package in depgraph["depends"]:
            for depend in depgraph["depends"][package]:
                self.depends_model.insert (0, (TYPE_DEP, package, depend))

        for package in depgraph["rdepends-pn"]:
            for rdepend in depgraph["rdepends-pn"][package]:
                self.depends_model.insert (0, (TYPE_RDEP, package, rdepend))


class gtkthread(threading.Thread):
    quit = threading.Event()
    def __init__(self, shutdown):
        threading.Thread.__init__(self)
        self.setDaemon(True)
        self.shutdown = shutdown

    def run(self):
        gobject.threads_init()
        gtk.gdk.threads_init()
        gtk.main()
        gtkthread.quit.set()


def main(server, eventHandler, params):
    try:
        params.updateFromServer(server)
        cmdline = params.parseActions()
        if not cmdline:
            print("Nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
            return 1
        if 'msg' in cmdline and cmdline['msg']:
            print(cmdline['msg'])
            return 1
        cmdline = cmdline['action']
        if not cmdline or cmdline[0] != "generateDotGraph":
            print("This UI requires the -g option")
            return 1
        ret, error = server.runCommand(["generateDepTreeEvent", cmdline[1], cmdline[2]])
        if error:
            print("Error running command '%s': %s" % (cmdline, error))
            return 1
        elif ret != True:
            print("Error running command '%s': returned %s" % (cmdline, ret))
            return 1
    except xmlrpclib.Fault as x:
        print("XMLRPC Fault getting commandline:\n %s" % x)
        return

    try:
        gtk.init_check()
    except RuntimeError:
        sys.stderr.write("Please set DISPLAY variable before running this command \n")
        return

    shutdown = 0

    gtkgui = gtkthread(shutdown)
    gtkgui.start()

    gtk.gdk.threads_enter()
    dep = DepExplorer()
    bardialog = gtk.Dialog(parent=dep,
                           flags=gtk.DIALOG_MODAL|gtk.DIALOG_DESTROY_WITH_PARENT)
    bardialog.set_default_size(400, 50)
    pbar = HobProgressBar()
    bardialog.vbox.pack_start(pbar)
    bardialog.show_all()
    bardialog.connect("delete-event", gtk.main_quit)
    gtk.gdk.threads_leave()

    progress_total = 0
    while True:
        try:
            event = eventHandler.waitEvent(0.25)
            if gtkthread.quit.isSet():
                _, error = server.runCommand(["stateForceShutdown"])
                if error:
                    print('Unable to cleanly stop: %s' % error)
                break

            if event is None:
                continue

            if isinstance(event, bb.event.CacheLoadStarted):
                progress_total = event.total
                gtk.gdk.threads_enter()
                bardialog.set_title("Loading Cache")
                pbar.update(0)
                gtk.gdk.threads_leave()

            if isinstance(event, bb.event.CacheLoadProgress):
                x = event.current
                gtk.gdk.threads_enter()
                pbar.update(x * 1.0 / progress_total)
                pbar.set_title('')
                gtk.gdk.threads_leave()
                continue

            if isinstance(event, bb.event.CacheLoadCompleted):
                bardialog.hide()
                continue

            if isinstance(event, bb.event.ParseStarted):
                progress_total = event.total
                if progress_total == 0:
                    continue
                gtk.gdk.threads_enter()
                pbar.update(0)
                bardialog.set_title("Processing recipes")

                gtk.gdk.threads_leave()

            if isinstance(event, bb.event.ParseProgress):
                x = event.current
                gtk.gdk.threads_enter()
                pbar.update(x * 1.0 / progress_total)
                pbar.set_title('')
                gtk.gdk.threads_leave()
                continue

            if isinstance(event, bb.event.ParseCompleted):
                bardialog.hide()
                continue

            if isinstance(event, bb.event.DepTreeGenerated):
                gtk.gdk.threads_enter()
                dep.parse(event._depgraph)
                gtk.gdk.threads_leave()

            if isinstance(event, bb.command.CommandCompleted):
                continue

            if isinstance(event, bb.command.CommandFailed):
                print("Command execution failed: %s" % event.error)
                return event.exitcode

            if isinstance(event, bb.command.CommandExit):
                return event.exitcode

            if isinstance(event, bb.cooker.CookerExit):
                break

            continue
        except EnvironmentError as ioerror:
            # ignore interrupted io
            if ioerror.args[0] == 4:
                pass
        except KeyboardInterrupt:
            if shutdown == 2:
                print("\nThird Keyboard Interrupt, exit.\n")
                break
            if shutdown == 1:
                print("\nSecond Keyboard Interrupt, stopping...\n")
                _, error = server.runCommand(["stateForceShutdown"])
                if error:
                    print('Unable to cleanly stop: %s' % error)
            if shutdown == 0:
                print("\nKeyboard Interrupt, closing down...\n")
                _, error = server.runCommand(["stateShutdown"])
                if error:
                    print('Unable to cleanly shutdown: %s' % error)
            shutdown = shutdown + 1
            pass
