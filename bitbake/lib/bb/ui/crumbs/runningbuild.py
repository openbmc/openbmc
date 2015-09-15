
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
import logging
import time
import urllib
import urllib2
import pango
from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.hobwidget import HobWarpCellRendererText, HobCellRendererPixbuf

class RunningBuildModel (gtk.TreeStore):
    (COL_LOG, COL_PACKAGE, COL_TASK, COL_MESSAGE, COL_ICON, COL_COLOR, COL_NUM_ACTIVE) = range(7)

    def __init__ (self):
        gtk.TreeStore.__init__ (self,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_INT)

    def failure_model_filter(self, model, it):
        color = model.get(it, self.COL_COLOR)[0]
        if not color:
            return False
        if color == HobColors.ERROR or color == HobColors.WARNING:
            return True
        return False

    def failure_model(self):
        model = self.filter_new()
        model.set_visible_func(self.failure_model_filter)
        return model

    def foreach_cell_func(self, model, path, iter, usr_data=None):
        if model.get_value(iter, self.COL_ICON) == "gtk-execute":
            model.set(iter, self.COL_ICON, "")

    def close_task_refresh(self):
        self.foreach(self.foreach_cell_func, None)

class RunningBuild (gobject.GObject):
    __gsignals__ = {
          'build-started'   :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'build-succeeded' :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'build-failed'    :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'build-complete'  :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'build-aborted'   :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'task-started'    :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               (gobject.TYPE_PYOBJECT,)),
          'log-error'       :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'log-warning'     :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'disk-full'       :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               ()),
          'no-provider'     :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               (gobject.TYPE_PYOBJECT,)),
          'log'             :  (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                               (gobject.TYPE_STRING, gobject.TYPE_PYOBJECT,)),
          }
    pids_to_task = {}
    tasks_to_iter = {}

    def __init__ (self, sequential=False):
        gobject.GObject.__init__ (self)
        self.model = RunningBuildModel()
        self.sequential = sequential
        self.buildaborted = False

    def reset (self):
        self.pids_to_task.clear()
        self.tasks_to_iter.clear()
        self.model.clear()

    def handle_event (self, event, pbar=None):
        # Handle an event from the event queue, this may result in updating
        # the model and thus the UI. Or it may be to tell us that the build
        # has finished successfully (or not, as the case may be.)

        parent = None
        pid = 0
        package = None
        task = None

        # If we have a pid attached to this message/event try and get the
        # (package, task) pair for it. If we get that then get the parent iter
        # for the message.
        if hasattr(event, 'pid'):
            pid = event.pid
        if hasattr(event, 'process'):
            pid = event.process

        if pid and pid in self.pids_to_task:
            (package, task) = self.pids_to_task[pid]
            parent = self.tasks_to_iter[(package, task)]

        if(isinstance(event, logging.LogRecord)):
            if event.taskpid == 0 or event.levelno > logging.INFO:
                self.emit("log", "handle", event)
            # FIXME: this is a hack! More info in Yocto #1433
            # http://bugzilla.pokylinux.org/show_bug.cgi?id=1433, temporarily
            # mask the error message as it's not informative for the user.
            if event.msg.startswith("Execution of event handler 'run_buildstats' failed"):
                return

            if (event.levelno < logging.INFO or
                event.msg.startswith("Running task")):
                return # don't add these to the list

            if event.levelno >= logging.ERROR:
                icon = "dialog-error"
                color = HobColors.ERROR
                self.emit("log-error")
            elif event.levelno >= logging.WARNING:
                icon = "dialog-warning"
                color = HobColors.WARNING
                self.emit("log-warning")
            else:
                icon = None
                color = HobColors.OK

            # if we know which package we belong to, we'll append onto its list.
            # otherwise, we'll jump to the top of the master list
            if self.sequential or not parent:
                tree_add = self.model.append
            else:
                tree_add = self.model.prepend
            tree_add(parent,
                     (None,
                      package,
                      task,
                      event.getMessage(),
                      icon,
                      color,
                      0))

            # if there are warnings while processing a package
            #  (parent), mark the task with warning color;
            # in case there are errors, the updates will be
            #  handled on TaskFailed.
            if color == HobColors.WARNING and parent:
                self.model.set(parent, self.model.COL_COLOR, color)
                if task: #then we have a parent (package), and update it's color
                    self.model.set(self.tasks_to_iter[(package, None)], self.model.COL_COLOR, color)

        elif isinstance(event, bb.build.TaskStarted):
            (package, task) = (event._package, event._task)

            # Save out this PID.
            self.pids_to_task[pid] = (package, task)

            # Check if we already have this package in our model. If so then
            # that can be the parent for the task. Otherwise we create a new
            # top level for the package.
            if ((package, None) in self.tasks_to_iter):
                parent = self.tasks_to_iter[(package, None)]
            else:
                if self.sequential:
                    add = self.model.append
                else:
                    add = self.model.prepend
                parent = add(None, (None,
                                    package,
                                    None,
                                    "Package: %s" % (package),
                                    None,
                                    HobColors.OK,
                                    0))
                self.tasks_to_iter[(package, None)] = parent

            # Because this parent package now has an active child mark it as
            # such.
            self.model.set(parent, self.model.COL_ICON, "gtk-execute")
            parent_color = self.model.get(parent, self.model.COL_COLOR)[0]
            if parent_color != HobColors.ERROR and parent_color != HobColors.WARNING:
                self.model.set(parent, self.model.COL_COLOR, HobColors.RUNNING)

            # Add an entry in the model for this task
            i = self.model.append (parent, (None,
                                            package,
                                            task,
                                            "Task: %s" % (task),
                                            "gtk-execute",
                                            HobColors.RUNNING,
                                            0))

            # update the parent's active task count
            num_active = self.model.get(parent, self.model.COL_NUM_ACTIVE)[0] + 1
            self.model.set(parent, self.model.COL_NUM_ACTIVE, num_active)

            # Save out the iter so that we can find it when we have a message
            # that we need to attach to a task.
            self.tasks_to_iter[(package, task)] = i

        elif isinstance(event, bb.build.TaskBase):
            self.emit("log", "info", event._message)
            current = self.tasks_to_iter[(package, task)]
            parent = self.tasks_to_iter[(package, None)]

            # remove this task from the parent's active count
            num_active = self.model.get(parent, self.model.COL_NUM_ACTIVE)[0] - 1
            self.model.set(parent, self.model.COL_NUM_ACTIVE, num_active)

            if isinstance(event, bb.build.TaskFailed):
                # Mark the task and parent as failed
                icon = "dialog-error"
                color = HobColors.ERROR

                logfile = event.logfile
                if logfile and os.path.exists(logfile):
                    with open(logfile) as f:
                        logdata = f.read()
                        self.model.append(current, ('pastebin', None, None, logdata, 'gtk-error', HobColors.OK, 0))

                for i in (current, parent):
                    self.model.set(i, self.model.COL_ICON, icon,
                                   self.model.COL_COLOR, color)
            else:
                # Mark the parent package and the task as inactive,
                # but make sure to preserve error, warnings and active
                # states
                parent_color = self.model.get(parent, self.model.COL_COLOR)[0]
                task_color = self.model.get(current, self.model.COL_COLOR)[0]

                # Mark the task as inactive
                self.model.set(current, self.model.COL_ICON, None)
                if task_color != HobColors.ERROR:
                    if task_color == HobColors.WARNING:
                        self.model.set(current, self.model.COL_ICON, 'dialog-warning')
                    else:
                        self.model.set(current, self.model.COL_COLOR, HobColors.OK)

                # Mark the parent as inactive
                if parent_color != HobColors.ERROR:
                    if parent_color == HobColors.WARNING:
                        self.model.set(parent, self.model.COL_ICON, "dialog-warning")
                    else:
                        self.model.set(parent, self.model.COL_ICON, None)
                        if num_active == 0:
                            self.model.set(parent, self.model.COL_COLOR, HobColors.OK)

            # Clear the iters and the pids since when the task goes away the
            # pid will no longer be used for messages
            del self.tasks_to_iter[(package, task)]
            del self.pids_to_task[pid]

        elif isinstance(event, bb.event.BuildStarted):

            self.emit("build-started")
            self.model.prepend(None, (None,
                                      None,
                                      None,
                                      "Build Started (%s)" % time.strftime('%m/%d/%Y %H:%M:%S'),
                                      None,
                                      HobColors.OK,
                                      0))
            if pbar:
                pbar.update(0, self.progress_total)
                pbar.set_title(bb.event.getName(event))

        elif isinstance(event, bb.event.BuildCompleted):
            failures = int (event._failures)
            self.model.prepend(None, (None,
                                      None,
                                      None,
                                      "Build Completed (%s)" % time.strftime('%m/%d/%Y %H:%M:%S'),
                                      None,
                                      HobColors.OK,
                                      0))

            # Emit the appropriate signal depending on the number of failures
            if self.buildaborted:
                self.emit ("build-aborted")
                self.buildaborted = False
            elif (failures >= 1):
                self.emit ("build-failed")
            else:
                self.emit ("build-succeeded")
            # Emit a generic "build-complete" signal for things wishing to
            # handle when the build is finished
            self.emit("build-complete")
            # reset the all cell's icon indicator
            self.model.close_task_refresh()
            if pbar:
                pbar.set_text(event.msg)

        elif isinstance(event, bb.event.DiskFull):
            self.buildaborted = True
            self.emit("disk-full")

        elif isinstance(event, bb.command.CommandFailed):
            self.emit("log", "error", "Command execution failed: %s" % (event.error))
            if event.error.startswith("Exited with"):
                # If the command fails with an exit code we're done, emit the
                # generic signal for the UI to notify the user
                self.emit("build-complete")
                # reset the all cell's icon indicator
                self.model.close_task_refresh()

        elif isinstance(event, bb.event.CacheLoadStarted) and pbar:
            pbar.set_title("Loading cache")
            self.progress_total = event.total
            pbar.update(0, self.progress_total)
        elif isinstance(event, bb.event.CacheLoadProgress) and pbar:
            pbar.update(event.current, self.progress_total)
        elif isinstance(event, bb.event.CacheLoadCompleted) and pbar:
            pbar.update(self.progress_total, self.progress_total)
            pbar.hide()
        elif isinstance(event, bb.event.ParseStarted) and pbar:
            if event.total == 0:
                return
            pbar.set_title("Processing recipes")
            self.progress_total = event.total
            pbar.update(0, self.progress_total)
        elif isinstance(event, bb.event.ParseProgress) and pbar:
            pbar.update(event.current, self.progress_total)
        elif isinstance(event, bb.event.ParseCompleted) and pbar:
            pbar.hide()
        #using runqueue events as many as possible to update the progress bar
        elif isinstance(event, bb.runqueue.runQueueTaskFailed):
            self.emit("log", "error", "Task %s (%s) failed with exit code '%s'" % (event.taskid, event.taskstring, event.exitcode))
        elif isinstance(event, bb.runqueue.sceneQueueTaskFailed):
            self.emit("log", "warn", "Setscene task %s (%s) failed with exit code '%s' - real task will be run instead" \
                                     % (event.taskid, event.taskstring, event.exitcode))
        elif isinstance(event, (bb.runqueue.runQueueTaskStarted, bb.runqueue.sceneQueueTaskStarted)):
            if isinstance(event, bb.runqueue.sceneQueueTaskStarted):
                self.emit("log", "info", "Running setscene task %d of %d (%s)" % \
                                         (event.stats.completed + event.stats.active + event.stats.failed + 1,
                                          event.stats.total, event.taskstring))
            else:
                if event.noexec:
                    tasktype = 'noexec task'
                else:
                    tasktype = 'task'
                self.emit("log", "info", "Running %s %s of %s (ID: %s, %s)" % \
                                         (tasktype, event.stats.completed + event.stats.active + event.stats.failed + 1,
                                          event.stats.total, event.taskid, event.taskstring))
            message = {}
            message["eventname"] = bb.event.getName(event)
            num_of_completed = event.stats.completed + event.stats.failed
            message["current"] = num_of_completed
            message["total"] = event.stats.total
            message["title"] = ""
            message["task"] = event.taskstring
            self.emit("task-started", message)
        elif isinstance(event, bb.event.MultipleProviders):
            self.emit("log", "info", "multiple providers are available for %s%s (%s)" \
                                     % (event._is_runtime and "runtime " or "", event._item, ", ".join(event._candidates)))
            self.emit("log", "info", "consider defining a PREFERRED_PROVIDER entry to match %s" % (event._item))
        elif isinstance(event, bb.event.NoProvider):
            msg = ""
            if event._runtime:
                r = "R"
            else:
                r = ""

            extra = ''
            if not event._reasons:
                if event._close_matches:
                    extra = ". Close matches:\n  %s" % '\n  '.join(event._close_matches)

            if event._dependees:
                msg = "Nothing %sPROVIDES '%s' (but %s %sDEPENDS on or otherwise requires it)%s\n" % (r, event._item, ", ".join(event._dependees), r, extra)
            else:
                msg = "Nothing %sPROVIDES '%s'%s\n" % (r, event._item, extra)
            if event._reasons:
                for reason in event._reasons:
                    msg += ("%s\n" % reason)
            self.emit("no-provider", msg)
            self.emit("log", "error", msg)
        elif isinstance(event, bb.event.LogExecTTY):
            icon = "dialog-warning"
            color = HobColors.WARNING
            if self.sequential or not parent:
                tree_add = self.model.append
            else:
                tree_add = self.model.prepend
            tree_add(parent,
                     (None,
                      package,
                      task,
                      event.msg,
                      icon,
                      color,
                      0))
        else:
            if not isinstance(event, (bb.event.BuildBase,
                                      bb.event.StampUpdate,
                                      bb.event.ConfigParsed,
                                      bb.event.RecipeParsed,
                                      bb.event.RecipePreFinalise,
                                      bb.runqueue.runQueueEvent,
                                      bb.runqueue.runQueueExitWait,
                                      bb.event.OperationStarted,
                                      bb.event.OperationCompleted,
                                      bb.event.OperationProgress)):
                self.emit("log", "error", "Unknown event: %s" % (event.error if hasattr(event, 'error') else 'error'))

        return


def do_pastebin(text):
    url = 'http://pastebin.com/api_public.php'
    params = {'paste_code': text, 'paste_format': 'text'}

    req = urllib2.Request(url, urllib.urlencode(params))
    response = urllib2.urlopen(req)
    paste_url = response.read()

    return paste_url


class RunningBuildTreeView (gtk.TreeView):
    __gsignals__ = {
        "button_press_event" : "override"
        }
    def __init__ (self, readonly=False, hob=False):
        gtk.TreeView.__init__ (self)
        self.readonly = readonly

        # The icon that indicates whether we're building or failed.
        # add 'hob' flag because there has not only hob to share this code
        if hob:
            renderer = HobCellRendererPixbuf ()
        else:
            renderer = gtk.CellRendererPixbuf()
        col = gtk.TreeViewColumn ("Status", renderer)
        col.add_attribute (renderer, "icon-name", 4)
        self.append_column (col)

        # The message of the build.
        # add 'hob' flag because there has not only hob to share this code
        if hob:
            self.message_renderer = HobWarpCellRendererText (col_number=1)
        else:
            self.message_renderer = gtk.CellRendererText ()
        self.message_column = gtk.TreeViewColumn ("Message", self.message_renderer, text=3)
        self.message_column.add_attribute(self.message_renderer, 'background', 5)
        self.message_renderer.set_property('editable', (not self.readonly))
        self.append_column (self.message_column)

    def do_button_press_event(self, event):
        gtk.TreeView.do_button_press_event(self, event)

        if event.button == 3:
            selection = super(RunningBuildTreeView, self).get_selection()
            (model, it) = selection.get_selected()
            if it is not None:
                can_paste = model.get(it, model.COL_LOG)[0]
                if can_paste == 'pastebin':
                    # build a simple menu with a pastebin option
                    menu = gtk.Menu()
                    menuitem = gtk.MenuItem("Copy")
                    menu.append(menuitem)
                    menuitem.connect("activate", self.clipboard_handler, (model, it))
                    menuitem.show()
                    menuitem = gtk.MenuItem("Send log to pastebin")
                    menu.append(menuitem)
                    menuitem.connect("activate", self.pastebin_handler, (model, it))
                    menuitem.show()
                    menu.show()
                    menu.popup(None, None, None, event.button, event.time)

    def _add_to_clipboard(self, clipping):
        """
        Add the contents of clipping to the system clipboard.
        """
        clipboard = gtk.clipboard_get()
        clipboard.set_text(clipping)
        clipboard.store()

    def pastebin_handler(self, widget, data):
        """
        Send the log data to pastebin, then add the new paste url to the
        clipboard.
        """
        (model, it) = data
        paste_url = do_pastebin(model.get(it, model.COL_MESSAGE)[0])

        # @todo Provide visual feedback to the user that it is done and that
        # it worked.
        print paste_url

        self._add_to_clipboard(paste_url)

    def clipboard_handler(self, widget, data):
        """
        """
        (model, it) = data
        message = model.get(it, model.COL_MESSAGE)[0]

        self._add_to_clipboard(message)

class BuildFailureTreeView(gtk.TreeView):

    def __init__ (self):
        gtk.TreeView.__init__(self)
        self.set_rules_hint(False)
        self.set_headers_visible(False)
        self.get_selection().set_mode(gtk.SELECTION_SINGLE)

        # The icon that indicates whether we're building or failed.
        renderer = HobCellRendererPixbuf ()
        col = gtk.TreeViewColumn ("Status", renderer)
        col.add_attribute (renderer, "icon-name", RunningBuildModel.COL_ICON)
        self.append_column (col)

        # The message of the build.
        self.message_renderer = HobWarpCellRendererText (col_number=1)
        self.message_column = gtk.TreeViewColumn ("Message", self.message_renderer, text=RunningBuildModel.COL_MESSAGE, background=RunningBuildModel.COL_COLOR)
        self.append_column (self.message_column)
