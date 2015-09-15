#!/usr/bin/env python
#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2012        Intel Corporation
#
# Authored by Dongxiao Xu <dongxiao.xu@intel.com>
# Authored by Shane Wang <shane.wang@intel.com>
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
import pango
import gobject
import bb.process
from bb.ui.crumbs.progressbar import HobProgressBar
from bb.ui.crumbs.hobwidget import hic, HobNotebook, HobAltButton, HobWarpCellRendererText, HobButton, HobInfoButton
from bb.ui.crumbs.runningbuild import RunningBuildTreeView
from bb.ui.crumbs.runningbuild import BuildFailureTreeView
from bb.ui.crumbs.hobpages import HobPage
from bb.ui.crumbs.hobcolor import HobColors

class BuildConfigurationTreeView(gtk.TreeView):
    def __init__ (self):
        gtk.TreeView.__init__(self)
        self.set_rules_hint(False)
        self.set_headers_visible(False)
        self.set_property("hover-expand", True)
        self.get_selection().set_mode(gtk.SELECTION_SINGLE)

        # The icon that indicates whether we're building or failed.
        renderer0 = gtk.CellRendererText()
        renderer0.set_property('font-desc', pango.FontDescription('courier bold 12'))
        col0 = gtk.TreeViewColumn ("Name", renderer0, text=0)
        self.append_column (col0)

        # The message of configuration.
        renderer1 = HobWarpCellRendererText(col_number=1)
        col1 = gtk.TreeViewColumn ("Values", renderer1, text=1)
        self.append_column (col1)

    def set_vars(self, key="", var=[""]):
        d = {}
        if type(var) == str:
            d = {key: [var]}
        elif type(var) == list and len(var) > 1:
            #create the sub item line
            l = []
            text = ""
            for item in var:
                text = " - " + item
                l.append(text)
            d = {key: var}

        return d

    def set_config_model(self, show_vars):
        listmodel = gtk.TreeStore(gobject.TYPE_STRING, gobject.TYPE_STRING)
        parent = None
        for var in show_vars:
            for subitem in var.items():
                name = subitem[0]
                is_parent = True
                for value in subitem[1]:
                    if is_parent:
                        parent = listmodel.append(parent, (name, value))
                        is_parent = False
                    else:
                        listmodel.append(parent, (None, value))
                    name = "     - "
                parent = None
        # renew the tree model after get the configuration messages
        self.set_model(listmodel)

    def show(self, src_config_info, src_params):
        vars = []
        vars.append(self.set_vars("BB version:", src_params.bb_version))
        vars.append(self.set_vars("Target arch:", src_params.target_arch))
        vars.append(self.set_vars("Target OS:", src_params.target_os))
        vars.append(self.set_vars("Machine:", src_config_info.curr_mach))
        vars.append(self.set_vars("Distro:", src_config_info.curr_distro))
        vars.append(self.set_vars("Distro version:", src_params.distro_version))
        vars.append(self.set_vars("SDK machine:", src_config_info.curr_sdk_machine))
        vars.append(self.set_vars("Tune features:", src_params.tune_pkgarch))
        vars.append(self.set_vars("Layers:", src_config_info.layers))

        for path in src_config_info.layers:
            import os, os.path
            if os.path.exists(path):
                branch = bb.process.run('cd %s; git branch | grep "^* " | tr -d "* "' % path)[0]
                if branch.startswith("fatal:"):
                    branch = "(unknown)"
                if branch:
                    branch = branch.strip('\n')
                    vars.append(self.set_vars("Branch:", branch))
                break

        self.set_config_model(vars)

    def reset(self):
        self.set_model(None)

#
# BuildDetailsPage
#

class BuildDetailsPage (HobPage):

    def __init__(self, builder):
        super(BuildDetailsPage, self).__init__(builder, "Building ...")

        self.num_of_issues = 0
        self.endpath = (0,)
        # create visual elements
        self.create_visual_elements()

    def create_visual_elements(self):
        # create visual elements
        self.vbox = gtk.VBox(False, 12)

        self.progress_box = gtk.VBox(False, 12)
        self.task_status = gtk.Label("\n") # to ensure layout is correct
        self.task_status.set_alignment(0.0, 0.5)
        self.progress_box.pack_start(self.task_status, expand=False, fill=False)
        self.progress_hbox = gtk.HBox(False, 6)
        self.progress_box.pack_end(self.progress_hbox, expand=True, fill=True)
        self.progress_bar = HobProgressBar()
        self.progress_hbox.pack_start(self.progress_bar, expand=True, fill=True)
        self.stop_button = HobAltButton("Stop")
        self.stop_button.connect("clicked", self.stop_button_clicked_cb)
        self.stop_button.set_sensitive(False)
        self.progress_hbox.pack_end(self.stop_button, expand=False, fill=False)

        self.notebook = HobNotebook()
        self.config_tv = BuildConfigurationTreeView()
        self.scrolled_view_config = gtk.ScrolledWindow ()
        self.scrolled_view_config.set_policy(gtk.POLICY_NEVER, gtk.POLICY_ALWAYS)
        self.scrolled_view_config.add(self.config_tv)
        self.notebook.append_page(self.scrolled_view_config, "Build configuration")

        self.failure_tv = BuildFailureTreeView()
        self.failure_model = self.builder.handler.build.model.failure_model()
        self.failure_tv.set_model(self.failure_model)
        self.scrolled_view_failure = gtk.ScrolledWindow ()
        self.scrolled_view_failure.set_policy(gtk.POLICY_NEVER, gtk.POLICY_ALWAYS)
        self.scrolled_view_failure.add(self.failure_tv)
        self.notebook.append_page(self.scrolled_view_failure, "Issues")

        self.build_tv = RunningBuildTreeView(readonly=True, hob=True)
        self.build_tv.set_model(self.builder.handler.build.model)
        self.scrolled_view_build = gtk.ScrolledWindow ()
        self.scrolled_view_build.set_policy(gtk.POLICY_NEVER, gtk.POLICY_ALWAYS)
        self.scrolled_view_build.add(self.build_tv)
        self.notebook.append_page(self.scrolled_view_build, "Log")

        self.builder.handler.build.model.connect_after("row-changed", self.scroll_to_present_row, self.scrolled_view_build.get_vadjustment(), self.build_tv)

        self.button_box = gtk.HBox(False, 6)
        self.back_button = HobAltButton('&lt;&lt; Back')
        self.back_button.connect("clicked", self.back_button_clicked_cb)
        self.button_box.pack_start(self.back_button, expand=False, fill=False)

    def update_build_status(self, current, total, task):
        recipe_path, recipe_task = task.split(", ")
        recipe = os.path.basename(recipe_path).rstrip(".bb")
        tsk_msg = "<b>Running task %s of %s:</b> %s\n<b>Recipe:</b> %s" % (current, total, recipe_task, recipe)
        self.task_status.set_markup(tsk_msg)
        self.stop_button.set_sensitive(True)

    def reset_build_status(self):
        self.task_status.set_markup("\n") # to ensure layout is correct
        self.endpath = (0,)

    def show_issues(self):
        self.num_of_issues += 1
        self.notebook.show_indicator_icon("Issues", self.num_of_issues)
        self.notebook.queue_draw()

    def reset_issues(self):
        self.num_of_issues = 0
        self.notebook.hide_indicator_icon("Issues")

    def _remove_all_widget(self):
        children = self.vbox.get_children() or []
        for child in children:
            self.vbox.remove(child)
        children = self.box_group_area.get_children() or []
        for child in children:
            self.box_group_area.remove(child)
        children = self.get_children() or []
        for child in children:
            self.remove(child)

    def add_build_fail_top_bar(self, actions, log_file=None):
        primary_action = "Edit %s" % actions

        color = HobColors.ERROR
        build_fail_top = gtk.EventBox()
        #build_fail_top.set_size_request(-1, 200)
        build_fail_top.modify_bg(gtk.STATE_NORMAL, gtk.gdk.color_parse(color))

        build_fail_tab = gtk.Table(14, 46, True)
        build_fail_top.add(build_fail_tab)

        icon = gtk.Image()
        icon_pix_buffer = gtk.gdk.pixbuf_new_from_file(hic.ICON_INDI_ERROR_FILE)
        icon.set_from_pixbuf(icon_pix_buffer)
        build_fail_tab.attach(icon, 1, 4, 0, 6)

        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        label.set_markup("<span size='x-large'><b>%s</b></span>" % self.title)
        build_fail_tab.attach(label, 4, 26, 0, 6)

        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        # Ensure variable disk_full is defined
        if not hasattr(self.builder, 'disk_full'):
            self.builder.disk_full = False

        if self.builder.disk_full:
            markup = "<span size='medium'>There is no disk space left, so Hob cannot finish building your image. Free up some disk space\n"
            markup += "and restart the build. Check the \"Issues\" tab for more details</span>"
            label.set_markup(markup)
        else:
            label.set_markup("<span size='medium'>Check the \"Issues\" information for more details</span>")
        build_fail_tab.attach(label, 4, 40, 4, 9)

        # create button 'Edit packages'
        action_button = HobButton(primary_action)
        #action_button.set_size_request(-1, 40)
        action_button.set_tooltip_text("Edit the %s parameters" % actions)
        action_button.connect('clicked', self.failure_primary_action_button_clicked_cb, primary_action)

        if log_file:
            open_log_button = HobAltButton("Open log")
            open_log_button.set_relief(gtk.RELIEF_HALF)
            open_log_button.set_tooltip_text("Open the build's log file")
            open_log_button.connect('clicked', self.open_log_button_clicked_cb, log_file)

        attach_pos = (24 if log_file else 14)
        file_bug_button = HobAltButton('File a bug')
        file_bug_button.set_relief(gtk.RELIEF_HALF)
        file_bug_button.set_tooltip_text("Open the Yocto Project bug tracking website")
        file_bug_button.connect('clicked', self.failure_activate_file_bug_link_cb)

        if not self.builder.disk_full:
            build_fail_tab.attach(action_button, 4, 13, 9, 12)
            if log_file:
                build_fail_tab.attach(open_log_button, 14, 23, 9, 12)
            build_fail_tab.attach(file_bug_button, attach_pos, attach_pos + 9, 9, 12)

        else:
            restart_build = HobButton("Restart the build")
            restart_build.set_tooltip_text("Restart the build")
            restart_build.connect('clicked', self.restart_build_button_clicked_cb)

            build_fail_tab.attach(restart_build, 4, 13, 9, 12)
            build_fail_tab.attach(action_button, 14, 23, 9, 12)
            if log_file:
                build_fail_tab.attach(open_log_button, attach_pos, attach_pos + 9, 9, 12)

        self.builder.disk_full = False
        return build_fail_top

    def show_fail_page(self, title):
        self._remove_all_widget()
        self.title = "Hob cannot build your %s" % title

        self.build_fail_bar = self.add_build_fail_top_bar(title, self.builder.current_logfile)

        self.pack_start(self.group_align, expand=True, fill=True)
        self.box_group_area.pack_start(self.build_fail_bar, expand=False, fill=False)
        self.box_group_area.pack_start(self.vbox, expand=True, fill=True)

        self.vbox.pack_start(self.notebook, expand=True, fill=True)
        self.show_all()
        self.notebook.set_page("Issues")
        self.back_button.hide()

    def add_build_stop_top_bar(self, action, log_file=None):
        color = HobColors.LIGHT_GRAY
        build_stop_top = gtk.EventBox()
        #build_stop_top.set_size_request(-1, 200)
        build_stop_top.modify_bg(gtk.STATE_NORMAL, gtk.gdk.color_parse(color))
        build_stop_top.set_flags(gtk.CAN_DEFAULT)
        build_stop_top.grab_default()

        build_stop_tab = gtk.Table(11, 46, True)
        build_stop_top.add(build_stop_tab)

        icon = gtk.Image()
        icon_pix_buffer = gtk.gdk.pixbuf_new_from_file(hic.ICON_INFO_HOVER_FILE)
        icon.set_from_pixbuf(icon_pix_buffer)
        build_stop_tab.attach(icon, 1, 4, 0, 6)

        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        label.set_markup("<span size='x-large'><b>%s</b></span>" % self.title)
        build_stop_tab.attach(label, 4, 26, 0, 6)

        action_button = HobButton("Edit %s" % action)
        action_button.set_size_request(-1, 40)
        if action == "image":
            action_button.set_tooltip_text("Edit the image parameters")
        elif action == "recipes":
            action_button.set_tooltip_text("Edit the included recipes")
        elif action == "packages":
            action_button.set_tooltip_text("Edit the included packages")
        action_button.connect('clicked', self.stop_primary_action_button_clicked_cb, action)
        build_stop_tab.attach(action_button, 4, 13, 6, 9)

        if log_file:
            open_log_button = HobAltButton("Open log")
            open_log_button.set_relief(gtk.RELIEF_HALF)
            open_log_button.set_tooltip_text("Open the build's log file")
            open_log_button.connect('clicked', self.open_log_button_clicked_cb, log_file)
            build_stop_tab.attach(open_log_button, 14, 23, 6, 9)

        attach_pos = (24 if log_file else 14)
        build_button = HobAltButton("Build new image")
        #build_button.set_size_request(-1, 40)
        build_button.set_tooltip_text("Create a new image from scratch")
        build_button.connect('clicked', self.new_image_button_clicked_cb)
        build_stop_tab.attach(build_button, attach_pos, attach_pos + 9, 6, 9)

        return build_stop_top, action_button

    def show_stop_page(self, action):
        self._remove_all_widget()
        self.title = "Build stopped"
        self.build_stop_bar, action_button = self.add_build_stop_top_bar(action, self.builder.current_logfile)

        self.pack_start(self.group_align, expand=True, fill=True)
        self.box_group_area.pack_start(self.build_stop_bar, expand=False, fill=False)
        self.box_group_area.pack_start(self.vbox, expand=True, fill=True)

        self.vbox.pack_start(self.notebook, expand=True, fill=True)
        self.show_all()
        self.back_button.hide()
        return action_button

    def show_page(self, step):
        self._remove_all_widget()
        if step == self.builder.PACKAGE_GENERATING or step == self.builder.FAST_IMAGE_GENERATING:
            self.title = "Building packages ..."
        else:
            self.title = "Building image ..."
        self.build_details_top = self.add_onto_top_bar(None)
        self.pack_start(self.build_details_top, expand=False, fill=False)
        self.pack_start(self.group_align, expand=True, fill=True)

        self.box_group_area.pack_start(self.vbox, expand=True, fill=True)

        self.progress_bar.reset()
        self.config_tv.reset()
        self.vbox.pack_start(self.progress_box, expand=False, fill=False)

        self.vbox.pack_start(self.notebook, expand=True, fill=True)

        self.box_group_area.pack_end(self.button_box, expand=False, fill=False)
        self.show_all()
        self.notebook.set_page("Log")
        self.back_button.hide()

        self.reset_build_status()
        self.reset_issues()

    def update_progress_bar(self, title, fraction, status=None):
        self.progress_bar.update(fraction)
        self.progress_bar.set_title(title)
        self.progress_bar.set_rcstyle(status)

    def back_button_clicked_cb(self, button):
        self.builder.show_configuration()

    def new_image_button_clicked_cb(self, button):
        self.builder.reset()

    def show_back_button(self):
        self.back_button.show()

    def stop_button_clicked_cb(self, button):
        self.builder.stop_build()

    def hide_stop_button(self):
        self.stop_button.set_sensitive(False)
        self.stop_button.hide()

    def scroll_to_present_row(self, model, path, iter, v_adj, treeview):
        if treeview and v_adj:
            if path[0] > self.endpath[0]: # check the event is a new row append or not
                self.endpath = path
                # check the gtk.adjustment position is at end boundary or not
                if (v_adj.upper <= v_adj.page_size) or (v_adj.value == v_adj.upper - v_adj.page_size):
                    treeview.scroll_to_cell(path)

    def show_configurations(self, configurations, params):
        self.config_tv.show(configurations, params)

    def failure_primary_action_button_clicked_cb(self, button, action):
        if "Edit recipes" in action:
            self.builder.show_recipes()
        elif "Edit packages" in action:
            self.builder.show_packages()
        elif "Edit image" in action:
            self.builder.show_configuration()

    def restart_build_button_clicked_cb(self, button):
        self.builder.just_bake()

    def stop_primary_action_button_clicked_cb(self, button, action):
        if "recipes" in action:
            self.builder.show_recipes()
        elif "packages" in action:
            self.builder.show_packages()
        elif "image" in action:
            self.builder.show_configuration()

    def open_log_button_clicked_cb(self, button, log_file):
        if log_file:
            log_file = "file:///" + log_file
            gtk.show_uri(screen=button.get_screen(), uri=log_file, timestamp=0)

    def failure_activate_file_bug_link_cb(self, button):
        button.child.emit('activate-link', "http://bugzilla.yoctoproject.org")
