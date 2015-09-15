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
import threading
import os
import datetime
import time

class BuildConfiguration:
    """ Represents a potential *or* historic *or* concrete build. It
    encompasses all the things that we need to tell bitbake to do to make it
    build what we want it to build.

    It also stored the metadata URL and the set of possible machines (and the
    distros / images / uris for these. Apart from the metdata URL these are
    not serialised to file (since they may be transient). In some ways this
    functionality might be shifted to the loader class."""

    def __init__ (self):
        self.metadata_url = None

        # Tuple of (distros, image, urls)
        self.machine_options = {}

        self.machine = None
        self.distro = None
        self.image = None
        self.urls = []
        self.extra_urls = []
        self.extra_pkgs = []

    def get_machines_model (self):
        model = gtk.ListStore (gobject.TYPE_STRING)
        for machine in self.machine_options.keys():
            model.append ([machine])

        return model

    def get_distro_and_images_models (self, machine):
        distro_model = gtk.ListStore (gobject.TYPE_STRING)

        for distro in self.machine_options[machine][0]:
            distro_model.append ([distro])

        image_model = gtk.ListStore (gobject.TYPE_STRING)

        for image in self.machine_options[machine][1]:
            image_model.append ([image])

        return (distro_model, image_model)

    def get_repos (self):
        self.urls = self.machine_options[self.machine][2]
        return self.urls

    # It might be a lot lot better if we stored these in like, bitbake conf
    # file format.
    @staticmethod
    def load_from_file (filename):

        conf = BuildConfiguration()
        with open(filename, "r") as f:
            for line in f:
                data = line.split (";")[1]
                if (line.startswith ("metadata-url;")):
                    conf.metadata_url = data.strip()
                    continue
                if (line.startswith ("url;")):
                    conf.urls += [data.strip()]
                    continue
                if (line.startswith ("extra-url;")):
                    conf.extra_urls += [data.strip()]
                    continue
                if (line.startswith ("machine;")):
                    conf.machine = data.strip()
                    continue
                if (line.startswith ("distribution;")):
                    conf.distro = data.strip()
                    continue
                if (line.startswith ("image;")):
                    conf.image = data.strip()
                    continue

        return conf

    # Serialise to a file. This is part of the build process and we use this
    # to be able to repeat a given build (using the same set of parameters)
    # but also so that we can include the details of the image / machine /
    # distro in the build manager tree view.
    def write_to_file (self, filename):
        f = open (filename, "w")

        lines = []

        if (self.metadata_url):
            lines += ["metadata-url;%s\n" % (self.metadata_url)]

        for url in self.urls:
            lines += ["url;%s\n" % (url)]

        for url in self.extra_urls:
            lines += ["extra-url;%s\n" % (url)]

        if (self.machine):
            lines += ["machine;%s\n" % (self.machine)]

        if (self.distro):
            lines += ["distribution;%s\n" % (self.distro)]

        if (self.image):
            lines += ["image;%s\n" % (self.image)]

        f.writelines (lines)
        f.close ()

class BuildResult(gobject.GObject):
    """ Represents an historic build. Perhaps not successful. But it includes
    things such as the files that are in the directory (the output from the
    build) as well as a deserialised BuildConfiguration file that is stored in
    ".conf" in the directory for the build.

    This is GObject so that it can be included in the TreeStore."""

    (STATE_COMPLETE, STATE_FAILED, STATE_ONGOING) = \
        (0, 1, 2)

    def __init__ (self, parent, identifier):
        gobject.GObject.__init__ (self)
        self.date = None

        self.files = []
        self.status = None
        self.identifier = identifier
        self.path = os.path.join (parent, identifier)

        # Extract the date, since the directory name is of the
        # format build-<year><month><day>-<ordinal> we can easily
        # pull it out.
        # TODO: Better to stat a file?
        (_, date, revision) = identifier.split ("-")
        print(date)

        year = int (date[0:4])
        month = int (date[4:6])
        day = int (date[6:8])

        self.date = datetime.date (year, month, day)

        self.conf = None

        # By default builds are STATE_FAILED unless we find a "complete" file
        # in which case they are STATE_COMPLETE
        self.state = BuildResult.STATE_FAILED
        for file in os.listdir (self.path):
            if (file.startswith (".conf")):
                conffile = os.path.join (self.path, file)
                self.conf = BuildConfiguration.load_from_file (conffile)
            elif (file.startswith ("complete")):
                self.state = BuildResult.STATE_COMPLETE
            else:
                self.add_file (file)

    def add_file (self, file):
        # Just add the file for now. Don't care about the type.
        self.files += [(file, None)]

class BuildManagerModel (gtk.TreeStore):
    """ Model for the BuildManagerTreeView. This derives from gtk.TreeStore
    but it abstracts nicely what the columns mean and the setup of the columns
    in the model. """

    (COL_IDENT, COL_DESC, COL_MACHINE, COL_DISTRO, COL_BUILD_RESULT, COL_DATE, COL_STATE) = \
        (0, 1, 2, 3, 4, 5, 6)

    def __init__ (self):
        gtk.TreeStore.__init__ (self,
            gobject.TYPE_STRING,
            gobject.TYPE_STRING,
            gobject.TYPE_STRING,
            gobject.TYPE_STRING,
            gobject.TYPE_OBJECT,
            gobject.TYPE_INT64,
            gobject.TYPE_INT)

class BuildManager (gobject.GObject):
    """ This class manages the historic builds that have been found in the
    "results" directory but is also used for starting a new build."""

    __gsignals__ = {
        'population-finished' : (gobject.SIGNAL_RUN_LAST,
                                 gobject.TYPE_NONE,
                                 ()),
        'populate-error' : (gobject.SIGNAL_RUN_LAST,
                            gobject.TYPE_NONE,
                            ())
    }

    def update_build_result (self, result, iter):
        # Convert the date into something we can sort by.
        date = long (time.mktime (result.date.timetuple()))

        # Add a top level entry for the build

        self.model.set (iter,
            BuildManagerModel.COL_IDENT, result.identifier,
            BuildManagerModel.COL_DESC, result.conf.image,
            BuildManagerModel.COL_MACHINE, result.conf.machine,
            BuildManagerModel.COL_DISTRO, result.conf.distro,
            BuildManagerModel.COL_BUILD_RESULT, result,
            BuildManagerModel.COL_DATE, date,
            BuildManagerModel.COL_STATE, result.state)

        # And then we use the files in the directory as the children for the
        # top level iter.
        for file in result.files:
            self.model.append (iter, (None, file[0], None, None, None, date, -1))

    # This function is called as an idle by the BuildManagerPopulaterThread
    def add_build_result (self, result):
        gtk.gdk.threads_enter()
        self.known_builds += [result]

        self.update_build_result (result, self.model.append (None))

        gtk.gdk.threads_leave()

    def notify_build_finished (self):
        # This is a bit of a hack. If we have a running build running then we
        # will have a row in the model in STATE_ONGOING. Find it and make it
        # as if it was a proper historic build (well, it is completed now....)

        # We need to use the iters here rather than the Python iterator
        # interface to the model since we need to pass it into
        # update_build_result

        iter = self.model.get_iter_first()

        while (iter):
            (ident, state) = self.model.get(iter,
                BuildManagerModel.COL_IDENT,
                BuildManagerModel.COL_STATE)

            if state == BuildResult.STATE_ONGOING:
                result = BuildResult (self.results_directory, ident)
                self.update_build_result (result, iter)
            iter = self.model.iter_next(iter)

    def notify_build_succeeded (self):
        # Write the "complete" file so that when we create the BuildResult
        # object we put into the model

        complete_file_path = os.path.join (self.cur_build_directory, "complete")
        f = file (complete_file_path, "w")
        f.close()
        self.notify_build_finished()

    def notify_build_failed (self):
        # Without a "complete" file then this will mark the build as failed:
        self.notify_build_finished()

    # This function is called as an idle
    def emit_population_finished_signal (self):
        gtk.gdk.threads_enter()
        self.emit ("population-finished")
        gtk.gdk.threads_leave()

    class BuildManagerPopulaterThread (threading.Thread):
        def __init__ (self, manager, directory):
            threading.Thread.__init__ (self)
            self.manager = manager
            self.directory = directory

        def run (self):
            # For each of the "build-<...>" directories ..

            if os.path.exists (self.directory):
                for directory in os.listdir (self.directory):

                    if not directory.startswith ("build-"):
                        continue

                    build_result = BuildResult (self.directory, directory)
                    self.manager.add_build_result (build_result)

            gobject.idle_add (BuildManager.emit_population_finished_signal,
                self.manager)

    def __init__ (self, server, results_directory):
        gobject.GObject.__init__ (self)

        # The builds that we've found from walking the result directory
        self.known_builds = []

        # Save out the bitbake server, we need this for issuing commands to
        # the cooker:
        self.server = server

        # The TreeStore that we use
        self.model = BuildManagerModel ()

        # The results directory is where we create (and look for) the
        # build-<xyz>-<n> directories. We need to populate ourselves from
        # directory
        self.results_directory = results_directory
        self.populate_from_directory (self.results_directory)

    def populate_from_directory (self, directory):
        thread = BuildManager.BuildManagerPopulaterThread (self, directory)
        thread.start()

    # Come up with the name for the next build ident by combining "build-"
    # with the date formatted as yyyymmdd and then an ordinal. We do this by
    # an optimistic algorithm incrementing the ordinal if we find that it
    # already exists.
    def get_next_build_ident (self):
        today = datetime.date.today ()
        datestr = str (today.year) + str (today.month) + str (today.day)

        revision = 0
        test_name = "build-%s-%d" % (datestr, revision)
        test_path = os.path.join (self.results_directory, test_name)

        while (os.path.exists (test_path)):
            revision += 1
            test_name = "build-%s-%d" % (datestr, revision)
            test_path = os.path.join (self.results_directory, test_name)

        return test_name

    # Take a BuildConfiguration and then try and build it based on the
    # parameters of that configuration. S
    def do_build (self, conf):
        server = self.server

        # Work out the build directory. Note we actually create the
        # directories here since we need to write the ".conf" file. Otherwise
        # we could have relied on bitbake's builder thread to actually make
        # the directories as it proceeds with the build.
        ident = self.get_next_build_ident ()
        build_directory = os.path.join (self.results_directory,
            ident)
        self.cur_build_directory = build_directory
        os.makedirs (build_directory)

        conffile = os.path.join (build_directory, ".conf")
        conf.write_to_file (conffile)

        # Add a row to the model representing this ongoing build. It's kinda a
        # fake entry. If this build completes or fails then this gets updated
        # with the real stuff like the historic builds
        date = long (time.time())
        self.model.append (None, (ident, conf.image, conf.machine, conf.distro,
            None, date, BuildResult.STATE_ONGOING))
        try:
            server.runCommand(["setVariable", "BUILD_IMAGES_FROM_FEEDS", 1])
            server.runCommand(["setVariable", "MACHINE", conf.machine])
            server.runCommand(["setVariable", "DISTRO", conf.distro])
            server.runCommand(["setVariable", "PACKAGE_CLASSES", "package_ipk"])
            server.runCommand(["setVariable", "BBFILES", \
              """${OEROOT}/meta/packages/*/*.bb ${OEROOT}/meta-moblin/packages/*/*.bb"""])
            server.runCommand(["setVariable", "TMPDIR", "${OEROOT}/build/tmp"])
            server.runCommand(["setVariable", "IPK_FEED_URIS", \
                " ".join(conf.get_repos())])
            server.runCommand(["setVariable", "DEPLOY_DIR_IMAGE",
                build_directory])
            server.runCommand(["buildTargets", [conf.image], "rootfs"])

        except Exception as e:
            print(e)

class BuildManagerTreeView (gtk.TreeView):
    """ The tree view for the build manager. This shows the historic builds
    and so forth. """

    # We use this function to control what goes in the cell since we store
    # the date in the model as seconds since the epoch (for sorting) and so we
    # need to make it human readable.
    def date_format_custom_cell_data_func (self, col, cell, model, iter):
        date = model.get (iter, BuildManagerModel.COL_DATE)[0]
        datestr = time.strftime("%A %d %B %Y", time.localtime(date))
        cell.set_property ("text", datestr)

    # This format function controls what goes in the cell. We use this to map
    # the integer state to a string and also to colourise the text
    def state_format_custom_cell_data_fun (self, col, cell, model, iter):
        state = model.get (iter, BuildManagerModel.COL_STATE)[0]

        if (state == BuildResult.STATE_ONGOING):
            cell.set_property ("text", "Active")
            cell.set_property ("foreground", "#000000")
        elif (state == BuildResult.STATE_FAILED):
            cell.set_property ("text", "Failed")
            cell.set_property ("foreground", "#ff0000")
        elif (state == BuildResult.STATE_COMPLETE):
            cell.set_property ("text", "Complete")
            cell.set_property ("foreground", "#00ff00")
        else:
            cell.set_property ("text", "")

    def __init__ (self):
        gtk.TreeView.__init__(self)

        # Misc descriptiony thing
        renderer = gtk.CellRendererText ()
        col = gtk.TreeViewColumn (None, renderer,
            text=BuildManagerModel.COL_DESC)
        self.append_column (col)

        # Machine
        renderer = gtk.CellRendererText ()
        col = gtk.TreeViewColumn ("Machine", renderer,
            text=BuildManagerModel.COL_MACHINE)
        self.append_column (col)

        # distro
        renderer = gtk.CellRendererText ()
        col = gtk.TreeViewColumn ("Distribution", renderer,
            text=BuildManagerModel.COL_DISTRO)
        self.append_column (col)

        # date (using a custom function for formatting the cell contents it
        # takes epoch -> human readable string)
        renderer = gtk.CellRendererText ()
        col = gtk.TreeViewColumn ("Date", renderer,
            text=BuildManagerModel.COL_DATE)
        self.append_column (col)
        col.set_cell_data_func (renderer,
            self.date_format_custom_cell_data_func)

        # For status.
        renderer = gtk.CellRendererText ()
        col = gtk.TreeViewColumn ("Status", renderer,
            text = BuildManagerModel.COL_STATE)
        self.append_column (col)
        col.set_cell_data_func (renderer,
            self.state_format_custom_cell_data_fun)
