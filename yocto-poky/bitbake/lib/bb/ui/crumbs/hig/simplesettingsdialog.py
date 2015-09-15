#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2011-2012   Intel Corporation
#
# Authored by Joshua Lock <josh@linux.intel.com>
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
import gobject
import hashlib
from bb.ui.crumbs.hobwidget import hic, HobInfoButton, HobButton, HobAltButton 
from bb.ui.crumbs.progressbar import HobProgressBar
from bb.ui.crumbs.hig.settingsuihelper import SettingsUIHelper
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.crumbsmessagedialog import CrumbsMessageDialog
from bb.ui.crumbs.hig.proxydetailsdialog import ProxyDetailsDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class SimpleSettingsDialog (CrumbsDialog, SettingsUIHelper):

    (BUILD_ENV_PAGE_ID,
     SHARED_STATE_PAGE_ID,
     PROXIES_PAGE_ID,
     OTHERS_PAGE_ID) = range(4)

    (TEST_NETWORK_NONE,
     TEST_NETWORK_INITIAL,
     TEST_NETWORK_RUNNING,
     TEST_NETWORK_PASSED,
     TEST_NETWORK_FAILED,
     TEST_NETWORK_CANCELED) = range(6)

    TARGETS = [
        ("MY_TREE_MODEL_ROW", gtk.TARGET_SAME_WIDGET, 0),
        ("text/plain", 0, 1),
        ("TEXT", 0, 2),
        ("STRING", 0, 3),
        ]

    def __init__(self, title, configuration, all_image_types,
            all_package_formats, all_distros, all_sdk_machines,
            max_threads, parent, flags, handler, buttons=None):
        super(SimpleSettingsDialog, self).__init__(title, parent, flags, buttons)

        # class members from other objects
        # bitbake settings from Builder.Configuration
        self.configuration = configuration
        self.image_types = all_image_types
        self.all_package_formats = all_package_formats
        self.all_distros = all_distros
        self.all_sdk_machines = all_sdk_machines
        self.max_threads = max_threads

        # class members for internal use
        self.dldir_text = None
        self.sstatedir_text = None
        self.sstatemirrors_list = []
        self.sstatemirrors_changed = 0
        self.bb_spinner = None
        self.pmake_spinner = None
        self.rootfs_size_spinner = None
        self.extra_size_spinner = None
        self.gplv3_checkbox = None
        self.toolchain_checkbox = None
        self.setting_store = None
        self.image_types_checkbuttons = {}

        self.md5 = self.config_md5()
        self.proxy_md5 = self.config_proxy_md5()
        self.settings_changed = False
        self.proxy_settings_changed = False
        self.handler = handler
        self.proxy_test_ran = False
        self.selected_mirror_row = 0
        self.new_mirror = False

        # create visual elements on the dialog
        self.create_visual_elements()
        self.connect("response", self.response_cb)

    def _get_sorted_value(self, var):
        return " ".join(sorted(str(var).split())) + "\n"

    def config_proxy_md5(self):
        data = ("ENABLE_PROXY: "         + self._get_sorted_value(self.configuration.enable_proxy))
        if self.configuration.enable_proxy:
            for protocol in self.configuration.proxies.keys():
                data += (protocol + ": " + self._get_sorted_value(self.configuration.combine_proxy(protocol)))
        return hashlib.md5(data).hexdigest()

    def config_md5(self):
        data = ""
        for key in self.configuration.extra_setting.keys():
            data += (key + ": " + self._get_sorted_value(self.configuration.extra_setting[key]))
        return hashlib.md5(data).hexdigest()

    def gen_proxy_entry_widget(self, protocol, parent, need_button=True, line=0):
        label = gtk.Label(protocol.upper() + " proxy")
        self.proxy_table.attach(label, 0, 1, line, line+1, xpadding=24)

        proxy_entry = gtk.Entry()
        proxy_entry.set_size_request(300, -1)
        self.proxy_table.attach(proxy_entry, 1, 2, line, line+1, ypadding=4)

        self.proxy_table.attach(gtk.Label(":"), 2, 3, line, line+1, xpadding=12, ypadding=4)

        port_entry = gtk.Entry()
        port_entry.set_size_request(60, -1)
        self.proxy_table.attach(port_entry, 3, 4, line, line+1, ypadding=4)

        details_button = HobAltButton("Details")
        details_button.connect("clicked", self.details_cb, parent, protocol)
        self.proxy_table.attach(details_button, 4, 5, line, line+1, xpadding=4, yoptions=gtk.EXPAND)

        return proxy_entry, port_entry, details_button

    def refresh_proxy_components(self):
        self.same_checkbox.set_sensitive(self.configuration.enable_proxy)

        self.http_proxy.set_text(self.configuration.combine_host_only("http"))
        self.http_proxy.set_editable(self.configuration.enable_proxy)
        self.http_proxy.set_sensitive(self.configuration.enable_proxy)
        self.http_proxy_port.set_text(self.configuration.combine_port_only("http"))
        self.http_proxy_port.set_editable(self.configuration.enable_proxy)
        self.http_proxy_port.set_sensitive(self.configuration.enable_proxy)
        self.http_proxy_details.set_sensitive(self.configuration.enable_proxy)

        self.https_proxy.set_text(self.configuration.combine_host_only("https"))
        self.https_proxy.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.https_proxy.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.https_proxy_port.set_text(self.configuration.combine_port_only("https"))
        self.https_proxy_port.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.https_proxy_port.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.https_proxy_details.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))

        self.ftp_proxy.set_text(self.configuration.combine_host_only("ftp"))
        self.ftp_proxy.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.ftp_proxy.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.ftp_proxy_port.set_text(self.configuration.combine_port_only("ftp"))
        self.ftp_proxy_port.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.ftp_proxy_port.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.ftp_proxy_details.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))

        self.socks_proxy.set_text(self.configuration.combine_host_only("socks"))
        self.socks_proxy.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.socks_proxy.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.socks_proxy_port.set_text(self.configuration.combine_port_only("socks"))
        self.socks_proxy_port.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.socks_proxy_port.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.socks_proxy_details.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))

        self.cvs_proxy.set_text(self.configuration.combine_host_only("cvs"))
        self.cvs_proxy.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.cvs_proxy.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.cvs_proxy_port.set_text(self.configuration.combine_port_only("cvs"))
        self.cvs_proxy_port.set_editable(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.cvs_proxy_port.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))
        self.cvs_proxy_details.set_sensitive(self.configuration.enable_proxy and (not self.configuration.same_proxy))

        if self.configuration.same_proxy:
            if self.http_proxy.get_text():
                [w.set_text(self.http_proxy.get_text()) for w in self.same_proxy_addresses]
            if self.http_proxy_port.get_text():
                [w.set_text(self.http_proxy_port.get_text()) for w in self.same_proxy_ports]

    def proxy_checkbox_toggled_cb(self, button):
        self.configuration.enable_proxy = self.proxy_checkbox.get_active()
        if not self.configuration.enable_proxy:
            self.configuration.same_proxy = False
            self.same_checkbox.set_active(self.configuration.same_proxy)
        self.save_proxy_data()
        self.refresh_proxy_components()

    def same_checkbox_toggled_cb(self, button):
        self.configuration.same_proxy = self.same_checkbox.get_active()
        self.save_proxy_data()
        self.refresh_proxy_components()

    def save_proxy_data(self):
        self.configuration.split_proxy("http", self.http_proxy.get_text() + ":" + self.http_proxy_port.get_text())
        if self.configuration.same_proxy:
            self.configuration.split_proxy("https", self.http_proxy.get_text() + ":" + self.http_proxy_port.get_text())
            self.configuration.split_proxy("ftp", self.http_proxy.get_text() + ":" + self.http_proxy_port.get_text())
            self.configuration.split_proxy("socks", self.http_proxy.get_text() + ":" + self.http_proxy_port.get_text())
            self.configuration.split_proxy("cvs", self.http_proxy.get_text() + ":" + self.http_proxy_port.get_text())
        else:
            self.configuration.split_proxy("https", self.https_proxy.get_text() + ":" + self.https_proxy_port.get_text())
            self.configuration.split_proxy("ftp", self.ftp_proxy.get_text() + ":" + self.ftp_proxy_port.get_text())
            self.configuration.split_proxy("socks", self.socks_proxy.get_text() + ":" + self.socks_proxy_port.get_text())
            self.configuration.split_proxy("cvs", self.cvs_proxy.get_text() + ":" + self.cvs_proxy_port.get_text())       

    def response_cb(self, dialog, response_id):
        if response_id == gtk.RESPONSE_YES:
            if self.proxy_checkbox.get_active():
                # Check that all proxy entries have a corresponding port
                for proxy, port in zip(self.all_proxy_addresses, self.all_proxy_ports):
                    if proxy.get_text() and not port.get_text():
                        lbl = "<b>Enter all port numbers</b>"
                        msg = "Proxy servers require a port number. Please make sure you have entered a port number for each proxy server."
                        dialog = CrumbsMessageDialog(self, lbl, gtk.MESSAGE_WARNING, msg)
                        button = dialog.add_button("Close", gtk.RESPONSE_OK)
                        HobButton.style_button(button)
                        response = dialog.run()
                        dialog.destroy()
                        self.emit_stop_by_name("response")
                        return

        self.configuration.dldir = self.dldir_text.get_text()
        self.configuration.sstatedir = self.sstatedir_text.get_text()
        self.configuration.sstatemirror = ""
        for mirror in self.sstatemirrors_list:
            if mirror[1] != "" and mirror[2].startswith("file://"):
                smirror = mirror[2] + " " + mirror[1] + " \\n "
                self.configuration.sstatemirror += smirror
        self.configuration.bbthread = self.bb_spinner.get_value_as_int()
        self.configuration.pmake = self.pmake_spinner.get_value_as_int()
        self.save_proxy_data()
        self.configuration.extra_setting = {}
        it = self.setting_store.get_iter_first()
        while it:
            key = self.setting_store.get_value(it, 0)
            value = self.setting_store.get_value(it, 1)
            self.configuration.extra_setting[key] = value
            it = self.setting_store.iter_next(it)

        md5 = self.config_md5()
        self.settings_changed = (self.md5 != md5)
        self.proxy_settings_changed = (self.proxy_md5 != self.config_proxy_md5())

    def create_build_environment_page(self):
        advanced_vbox = gtk.VBox(False, 6)
        advanced_vbox.set_border_width(6)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">Parallel threads</span>'), expand=False, fill=False)
        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("BitBake parallel threads")
        tooltip = "Sets the number of threads that BitBake tasks can simultaneously run. See the <a href=\""
        tooltip += "http://www.yoctoproject.org/docs/current/poky-ref-manual/"
        tooltip += "poky-ref-manual.html#var-BB_NUMBER_THREADS\">Poky reference manual</a> for information"
        bbthread_widget, self.bb_spinner = self.gen_spinner_widget(self.configuration.bbthread, 1, self.max_threads,"<b>BitBake prallalel threads</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(bbthread_widget, expand=False, fill=False)

        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("Make parallel threads")
        tooltip = "Sets the maximum number of threads the host can use during the build. See the <a href=\""
        tooltip += "http://www.yoctoproject.org/docs/current/poky-ref-manual/"
        tooltip += "poky-ref-manual.html#var-PARALLEL_MAKE\">Poky reference manual</a> for information"
        pmake_widget, self.pmake_spinner = self.gen_spinner_widget(self.configuration.pmake, 1, self.max_threads,"<b>Make parallel threads</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(pmake_widget, expand=False, fill=False)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">Downloaded source code</span>'), expand=False, fill=False)
        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("Downloads directory")
        tooltip = "Select a folder that caches the upstream project source code"
        dldir_widget, self.dldir_text = self.gen_entry_widget(self.configuration.dldir, self,"<b>Downloaded source code</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(dldir_widget, expand=False, fill=False)

        return advanced_vbox

    def create_shared_state_page(self):
        advanced_vbox = gtk.VBox(False)
        advanced_vbox.set_border_width(12)

        sub_vbox = gtk.VBox(False)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False, padding=24)
        content = "<span>Shared state directory</span>"
        tooltip = "Select a folder that caches your prebuilt results"
        label = self.gen_label_info_widget(content,"<b>Shared state directory</b>" + "*" + tooltip)
        sstatedir_widget, self.sstatedir_text = self.gen_entry_widget(self.configuration.sstatedir, self)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(sstatedir_widget, expand=False, fill=False, padding=6)

        content = "<span weight=\"bold\">Shared state mirrors</span>"
        tooltip = "URLs pointing to pre-built mirrors that will speed your build. "
        tooltip += "Select the \'Standard\' configuration if the structure of your "
        tooltip += "mirror replicates the structure of your local shared state directory. "
        tooltip += "For more information on shared state mirrors, check the <a href=\""
        tooltip += "http://www.yoctoproject.org/docs/current/poky-ref-manual/"
        tooltip += "poky-ref-manual.html#shared-state\">Yocto Project Reference Manual</a>."
        table = self.gen_label_info_widget(content,"<b>Shared state mirrors</b>" + "*" + tooltip)
        advanced_vbox.pack_start(table, expand=False, fill=False, padding=6)

        sub_vbox = gtk.VBox(False)
        advanced_vbox.pack_start(sub_vbox, gtk.TRUE, gtk.TRUE, 0)

        if self.sstatemirrors_changed == 0:
            self.sstatemirrors_changed = 1
            sstatemirrors = self.configuration.sstatemirror
            if sstatemirrors == "":
                sm_list = ["Standard", "", "file://(.*)"]
                self.sstatemirrors_list.append(sm_list)
            else:
                sstatemirrors = [x for x in sstatemirrors.split('\\n')]
                for sstatemirror in sstatemirrors:
                    sstatemirror_fields = [x for x in sstatemirror.split(' ') if x.strip()]
                    if len(sstatemirror_fields) == 2:
                        if sstatemirror_fields[0] == "file://(.*)" or sstatemirror_fields[0] == "file://.*":
                            sm_list = ["Standard", sstatemirror_fields[1], sstatemirror_fields[0]]
                        else:
                            sm_list = ["Custom", sstatemirror_fields[1], sstatemirror_fields[0]]
                        self.sstatemirrors_list.append(sm_list)

        sstatemirrors_widget, sstatemirrors_store = self.gen_shared_sstate_widget(self.sstatemirrors_list, self)
        sub_vbox.pack_start(sstatemirrors_widget, expand=True, fill=True)

        table = gtk.Table(1, 10, False)
        table.set_col_spacings(6)
        add_mirror_button = HobAltButton("Add mirror")
        add_mirror_button.connect("clicked", self.add_mirror)
        add_mirror_button.set_size_request(120,30)
        table.attach(add_mirror_button, 1, 2, 0, 1, xoptions=gtk.SHRINK)

        self.delete_button = HobAltButton("Delete mirror")
        self.delete_button.connect("clicked", self.delete_cb)
        self.delete_button.set_size_request(120, 30)
        table.attach(self.delete_button, 3, 4, 0, 1, xoptions=gtk.SHRINK)

        advanced_vbox.pack_start(table, expand=False, fill=False, padding=6)

        return advanced_vbox

    def gen_shared_sstate_widget(self, sstatemirrors_list, window):
        hbox = gtk.HBox(False)

        sstatemirrors_store = gtk.ListStore(str, str, str)
        for sstatemirror in sstatemirrors_list:
            sstatemirrors_store.append(sstatemirror)

        self.sstatemirrors_tv = gtk.TreeView()
        self.sstatemirrors_tv.set_rules_hint(True)
        self.sstatemirrors_tv.set_headers_visible(True)
        tree_selection = self.sstatemirrors_tv.get_selection()
        tree_selection.set_mode(gtk.SELECTION_SINGLE)

        # Allow enable drag and drop of rows including row move
        self.sstatemirrors_tv.enable_model_drag_source( gtk.gdk.BUTTON1_MASK,
            self.TARGETS,
            gtk.gdk.ACTION_DEFAULT|
            gtk.gdk.ACTION_MOVE)
        self.sstatemirrors_tv.enable_model_drag_dest(self.TARGETS,
            gtk.gdk.ACTION_DEFAULT)
        self.sstatemirrors_tv.connect("drag_data_get", self.drag_data_get_cb)
        self.sstatemirrors_tv.connect("drag_data_received", self.drag_data_received_cb)


        self.scroll = gtk.ScrolledWindow()
        self.scroll.set_policy(gtk.POLICY_NEVER, gtk.POLICY_AUTOMATIC)
        self.scroll.set_shadow_type(gtk.SHADOW_IN)
        self.scroll.connect('size-allocate', self.scroll_changed)
        self.scroll.add(self.sstatemirrors_tv)

        #list store for cell renderer
        m = gtk.ListStore(gobject.TYPE_STRING)
        m.append(["Standard"])
        m.append(["Custom"])

        cell0 = gtk.CellRendererCombo()
        cell0.set_property("model",m)
        cell0.set_property("text-column", 0)
        cell0.set_property("editable", True)
        cell0.set_property("has-entry", False)
        col0 = gtk.TreeViewColumn("Configuration")
        col0.pack_start(cell0, False)
        col0.add_attribute(cell0, "text", 0)
        col0.set_cell_data_func(cell0, self.configuration_field)
        self.sstatemirrors_tv.append_column(col0)

        cell0.connect("edited", self.combo_changed, sstatemirrors_store)

        self.cell1 = gtk.CellRendererText()
        self.cell1.set_padding(5,2)
        col1 = gtk.TreeViewColumn('Regex', self.cell1)
        col1.set_cell_data_func(self.cell1, self.regex_field)
        self.sstatemirrors_tv.append_column(col1)

        self.cell1.connect("edited", self.regex_changed, sstatemirrors_store)

        cell2 = gtk.CellRendererText()
        cell2.set_padding(5,2)
        cell2.set_property("editable", True)
        col2 = gtk.TreeViewColumn('URL', cell2)
        col2.set_cell_data_func(cell2, self.url_field)
        self.sstatemirrors_tv.append_column(col2)

        cell2.connect("edited", self.url_changed, sstatemirrors_store)

        self.sstatemirrors_tv.set_model(sstatemirrors_store)
        self.sstatemirrors_tv.set_cursor(self.selected_mirror_row)
        hbox.pack_start(self.scroll, expand=True, fill=True)
        hbox.show_all()

        return hbox, sstatemirrors_store

    def drag_data_get_cb(self, treeview, context, selection, target_id, etime):
        treeselection = treeview.get_selection()
        model, iter = treeselection.get_selected()
        data = model.get_string_from_iter(iter)
        selection.set(selection.target, 8, data)

    def drag_data_received_cb(self, treeview, context, x, y, selection, info, etime):
        model = treeview.get_model()
        data = []
        tree_iter = model.get_iter_from_string(selection.data)
        data.append(model.get_value(tree_iter, 0))
        data.append(model.get_value(tree_iter, 1))
        data.append(model.get_value(tree_iter, 2))

        drop_info = treeview.get_dest_row_at_pos(x, y)
        if drop_info:
            path, position = drop_info
            iter = model.get_iter(path)
            if (position == gtk.TREE_VIEW_DROP_BEFORE or position == gtk.TREE_VIEW_DROP_INTO_OR_BEFORE):
                model.insert_before(iter, data)
            else:
                model.insert_after(iter, data)
        else:
            model.append(data)
        if context.action == gtk.gdk.ACTION_MOVE:
            context.finish(True, True, etime)
        return

    def delete_cb(self, button):
        selection = self.sstatemirrors_tv.get_selection()
        tree_model, tree_iter = selection.get_selected()
        index = int(tree_model.get_string_from_iter(tree_iter))
        if index == 0:
            self.selected_mirror_row = index
        else:
            self.selected_mirror_row = index - 1
        self.sstatemirrors_list.pop(index)
        self.refresh_shared_state_page()
        if not self.sstatemirrors_list:
            self.delete_button.set_sensitive(False)

    def add_mirror(self, button):
        self.new_mirror = True
        tooltip = "Select the pre-built mirror that will speed your build"
        index = len(self.sstatemirrors_list)
        self.selected_mirror_row = index
        sm_list = ["Standard", "", "file://(.*)"]
        self.sstatemirrors_list.append(sm_list)
        self.refresh_shared_state_page()

    def scroll_changed(self, widget, event, data=None):
        if self.new_mirror == True:
            adj = widget.get_vadjustment()
            adj.set_value(adj.upper - adj.page_size)
            self.new_mirror = False

    def combo_changed(self, widget, path, text, model):
        model[path][0] = text
        selection = self.sstatemirrors_tv.get_selection()
        tree_model, tree_iter = selection.get_selected()
        index = int(tree_model.get_string_from_iter(tree_iter))
        self.sstatemirrors_list[index][0] = text

    def regex_changed(self, cell, path, new_text, user_data):
        user_data[path][2] = new_text
        selection = self.sstatemirrors_tv.get_selection()
        tree_model, tree_iter = selection.get_selected()
        index = int(tree_model.get_string_from_iter(tree_iter))
        self.sstatemirrors_list[index][2] = new_text
        return

    def url_changed(self, cell, path, new_text, user_data):
        if new_text!="Enter the mirror URL" and new_text!="Match regex and replace it with this URL":
            user_data[path][1] = new_text
            selection = self.sstatemirrors_tv.get_selection()
            tree_model, tree_iter = selection.get_selected()
            index = int(tree_model.get_string_from_iter(tree_iter))
            self.sstatemirrors_list[index][1] = new_text
        return

    def configuration_field(self, column, cell, model, iter):
        cell.set_property('text', model.get_value(iter, 0))
        if model.get_value(iter, 0) == "Standard":
            self.cell1.set_property("sensitive", False)
            self.cell1.set_property("editable", False)
        else:
            self.cell1.set_property("sensitive", True)
            self.cell1.set_property("editable", True)
        return

    def regex_field(self, column, cell, model, iter):
        cell.set_property('text', model.get_value(iter, 2))
        return

    def url_field(self, column, cell, model, iter):
        text = model.get_value(iter, 1)
        if text == "":
            if model.get_value(iter, 0) == "Standard":
                text = "Enter the mirror URL"
            else:
                text = "Match regex and replace it with this URL"
        cell.set_property('text', text)
        return

    def refresh_shared_state_page(self):
        page_num = self.nb.get_current_page()
        self.nb.remove_page(page_num);
        self.nb.insert_page(self.create_shared_state_page(), gtk.Label("Shared state"),page_num)
        self.show_all()
        self.nb.set_current_page(page_num)

    def test_proxy_ended(self, passed):
        self.proxy_test_running = False
        self.set_test_proxy_state(self.TEST_NETWORK_PASSED if passed else self.TEST_NETWORK_FAILED)
        self.set_sensitive(True)
        self.refresh_proxy_components()

    def timer_func(self):
        self.test_proxy_progress.pulse()
        return self.proxy_test_running

    def test_network_button_cb(self, b):
        self.set_test_proxy_state(self.TEST_NETWORK_RUNNING)
        self.set_sensitive(False)
        self.save_proxy_data()
        if self.configuration.enable_proxy == True:
            self.handler.set_http_proxy(self.configuration.combine_proxy("http"))
            self.handler.set_https_proxy(self.configuration.combine_proxy("https"))
            self.handler.set_ftp_proxy(self.configuration.combine_proxy("ftp"))
            self.handler.set_socks_proxy(self.configuration.combine_proxy("socks"))
            self.handler.set_cvs_proxy(self.configuration.combine_host_only("cvs"), self.configuration.combine_port_only("cvs"))
        elif self.configuration.enable_proxy == False:
            self.handler.set_http_proxy("")
            self.handler.set_https_proxy("")
            self.handler.set_ftp_proxy("")
            self.handler.set_socks_proxy("")
            self.handler.set_cvs_proxy("", "")
        self.proxy_test_ran = True
        self.proxy_test_running = True
        gobject.timeout_add(100, self.timer_func)
        self.handler.trigger_network_test()

    def test_proxy_focus_event(self, w, direction):
        if self.test_proxy_state in [self.TEST_NETWORK_PASSED, self.TEST_NETWORK_FAILED]:
            self.set_test_proxy_state(self.TEST_NETWORK_INITIAL)
        return False

    def http_proxy_changed(self, e):
        if not self.configuration.same_proxy:
            return
        if e == self.http_proxy:
            [w.set_text(self.http_proxy.get_text()) for w in self.same_proxy_addresses]
        else:
            [w.set_text(self.http_proxy_port.get_text()) for w in self.same_proxy_ports]

    def proxy_address_focus_out_event(self, w, direction):
        text = w.get_text()
        if not text:
            return False
        if text.find("//") == -1:
            w.set_text("http://" + text)
        return False

    def set_test_proxy_state(self, state):
        if self.test_proxy_state == state:
            return
        [self.proxy_table.remove(w) for w in self.test_gui_elements]
        if state == self.TEST_NETWORK_INITIAL:
            self.proxy_table.attach(self.test_network_button, 1, 2, 5, 6)
            self.test_network_button.show()
        elif state == self.TEST_NETWORK_RUNNING:
            self.test_proxy_progress.set_rcstyle("running")
            self.test_proxy_progress.set_text("Testing network configuration")
            self.proxy_table.attach(self.test_proxy_progress, 0, 5, 5, 6, xpadding=4)
            self.test_proxy_progress.show()
        else: # passed or failed
            self.dummy_progress.update(1.0)
            if state == self.TEST_NETWORK_PASSED:
                self.dummy_progress.set_text("Your network is properly configured")
                self.dummy_progress.set_rcstyle("running")
            else:
                self.dummy_progress.set_text("Network test failed")
                self.dummy_progress.set_rcstyle("fail")
            self.proxy_table.attach(self.dummy_progress, 0, 4, 5, 6)
            self.proxy_table.attach(self.retest_network_button, 4, 5, 5, 6, xpadding=4)
            self.dummy_progress.show()
            self.retest_network_button.show()
        self.test_proxy_state = state

    def create_network_page(self):
        advanced_vbox = gtk.VBox(False, 6)
        advanced_vbox.set_border_width(6)
        self.same_proxy_addresses = []
        self.same_proxy_ports = []
        self.all_proxy_ports = []
        self.all_proxy_addresses = []

        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("<span weight=\"bold\">Set the proxies used when fetching source code</span>")
        tooltip = "Set the proxies used when fetching source code.  A blank field uses a direct internet connection."
        info = HobInfoButton("<span weight=\"bold\">Set the proxies used when fetching source code</span>" + "*" + tooltip, self)
        hbox = gtk.HBox(False, 12)
        hbox.pack_start(label, expand=True, fill=True)
        hbox.pack_start(info, expand=False, fill=False)
        sub_vbox.pack_start(hbox, expand=False, fill=False)

        proxy_test_focus = []
        self.direct_checkbox = gtk.RadioButton(None, "Direct network connection")
        proxy_test_focus.append(self.direct_checkbox)
        self.direct_checkbox.set_tooltip_text("Check this box to use a direct internet connection with no proxy")
        self.direct_checkbox.set_active(not self.configuration.enable_proxy)
        sub_vbox.pack_start(self.direct_checkbox, expand=False, fill=False)

        self.proxy_checkbox = gtk.RadioButton(self.direct_checkbox, "Manual proxy configuration")
        proxy_test_focus.append(self.proxy_checkbox)
        self.proxy_checkbox.set_tooltip_text("Check this box to manually set up a specific proxy")
        self.proxy_checkbox.set_active(self.configuration.enable_proxy)
        sub_vbox.pack_start(self.proxy_checkbox, expand=False, fill=False)

        self.same_checkbox = gtk.CheckButton("Use the HTTP proxy for all protocols")
        proxy_test_focus.append(self.same_checkbox)
        self.same_checkbox.set_tooltip_text("Check this box to use the HTTP proxy for all five proxies")
        self.same_checkbox.set_active(self.configuration.same_proxy)
        hbox = gtk.HBox(False, 12)
        hbox.pack_start(self.same_checkbox, expand=False, fill=False, padding=24)
        sub_vbox.pack_start(hbox, expand=False, fill=False)

        self.proxy_table = gtk.Table(6, 5, False)
        self.http_proxy, self.http_proxy_port, self.http_proxy_details = self.gen_proxy_entry_widget(
            "http", self, True, 0)
        proxy_test_focus +=[self.http_proxy, self.http_proxy_port]
        self.http_proxy.connect("changed", self.http_proxy_changed)
        self.http_proxy_port.connect("changed", self.http_proxy_changed)

        self.https_proxy, self.https_proxy_port, self.https_proxy_details = self.gen_proxy_entry_widget(
            "https", self, True, 1)
        proxy_test_focus += [self.https_proxy, self.https_proxy_port]
        self.same_proxy_addresses.append(self.https_proxy)
        self.same_proxy_ports.append(self.https_proxy_port)

        self.ftp_proxy, self.ftp_proxy_port, self.ftp_proxy_details = self.gen_proxy_entry_widget(
            "ftp", self, True, 2)
        proxy_test_focus += [self.ftp_proxy, self.ftp_proxy_port]
        self.same_proxy_addresses.append(self.ftp_proxy)
        self.same_proxy_ports.append(self.ftp_proxy_port)

        self.socks_proxy, self.socks_proxy_port, self.socks_proxy_details = self.gen_proxy_entry_widget(
            "socks", self, True, 3)
        proxy_test_focus += [self.socks_proxy, self.socks_proxy_port]
        self.same_proxy_addresses.append(self.socks_proxy)
        self.same_proxy_ports.append(self.socks_proxy_port)

        self.cvs_proxy, self.cvs_proxy_port, self.cvs_proxy_details = self.gen_proxy_entry_widget(
            "cvs", self, True, 4)
        proxy_test_focus += [self.cvs_proxy, self.cvs_proxy_port]
        self.same_proxy_addresses.append(self.cvs_proxy)
        self.same_proxy_ports.append(self.cvs_proxy_port)
        self.all_proxy_ports = self.same_proxy_ports + [self.http_proxy_port]
        self.all_proxy_addresses = self.same_proxy_addresses + [self.http_proxy]
        sub_vbox.pack_start(self.proxy_table, expand=False, fill=False)
        self.proxy_table.show_all()

        # Create the graphical elements for the network test feature, but don't display them yet
        self.test_network_button = HobAltButton("Test network configuration")
        self.test_network_button.connect("clicked", self.test_network_button_cb)
        self.test_proxy_progress = HobProgressBar()
        self.dummy_progress = HobProgressBar()
        self.retest_network_button = HobAltButton("Retest")
        self.retest_network_button.connect("clicked", self.test_network_button_cb)
        self.test_gui_elements = [self.test_network_button, self.test_proxy_progress, self.dummy_progress, self.retest_network_button]
        # Initialize the network tester
        self.test_proxy_state = self.TEST_NETWORK_NONE
        self.set_test_proxy_state(self.TEST_NETWORK_INITIAL)
        self.proxy_test_passed_id = self.handler.connect("network-passed", lambda h:self.test_proxy_ended(True))
        self.proxy_test_failed_id = self.handler.connect("network-failed", lambda h:self.test_proxy_ended(False))
        [w.connect("focus-in-event", self.test_proxy_focus_event) for w in proxy_test_focus]
        [w.connect("focus-out-event", self.proxy_address_focus_out_event) for w in self.all_proxy_addresses]

        self.direct_checkbox.connect("toggled", self.proxy_checkbox_toggled_cb)
        self.proxy_checkbox.connect("toggled", self.proxy_checkbox_toggled_cb)
        self.same_checkbox.connect("toggled", self.same_checkbox_toggled_cb)

        self.refresh_proxy_components()
        return advanced_vbox

    def switch_to_page(self, page_id):
        self.nb.set_current_page(page_id)

    def details_cb(self, button, parent, protocol):
        self.save_proxy_data()
        dialog = ProxyDetailsDialog(title = protocol.upper() + " Proxy Details",
            user = self.configuration.proxies[protocol][1],
            passwd = self.configuration.proxies[protocol][2],
            parent = parent,
            flags = gtk.DIALOG_MODAL
                    | gtk.DIALOG_DESTROY_WITH_PARENT
                    | gtk.DIALOG_NO_SEPARATOR)
        dialog.add_button(gtk.STOCK_CLOSE, gtk.RESPONSE_OK)
        response = dialog.run()
        if response == gtk.RESPONSE_OK:
            self.configuration.proxies[protocol][1] = dialog.user
            self.configuration.proxies[protocol][2] = dialog.passwd
            self.refresh_proxy_components()
        dialog.destroy()    

    def rootfs_combo_changed_cb(self, rootfs_combo, all_package_format, check_hbox):
        combo_item = self.rootfs_combo.get_active_text()
        for child in check_hbox.get_children():
            if isinstance(child, gtk.CheckButton):
                check_hbox.remove(child)
        for format in all_package_format:
            if format != combo_item:
                check_button = gtk.CheckButton(format)
                check_hbox.pack_start(check_button, expand=False, fill=False)
        check_hbox.show_all()

    def gen_pkgfmt_widget(self, curr_package_format, all_package_format, tooltip_combo="", tooltip_extra=""):
        pkgfmt_hbox = gtk.HBox(False, 24)

        rootfs_vbox = gtk.VBox(False, 6)
        pkgfmt_hbox.pack_start(rootfs_vbox, expand=False, fill=False)

        label = self.gen_label_widget("Root file system package format")
        rootfs_vbox.pack_start(label, expand=False, fill=False)

        rootfs_format = ""
        if curr_package_format:
            rootfs_format = curr_package_format.split()[0]

        rootfs_format_widget, rootfs_combo = self.gen_combo_widget(rootfs_format, all_package_format, tooltip_combo)
        rootfs_vbox.pack_start(rootfs_format_widget, expand=False, fill=False)

        extra_vbox = gtk.VBox(False, 6)
        pkgfmt_hbox.pack_start(extra_vbox, expand=False, fill=False)

        label = self.gen_label_widget("Additional package formats")
        extra_vbox.pack_start(label, expand=False, fill=False)

        check_hbox = gtk.HBox(False, 12)
        extra_vbox.pack_start(check_hbox, expand=False, fill=False)
        for format in all_package_format:
            if format != rootfs_format:
                check_button = gtk.CheckButton(format)
                is_active = (format in curr_package_format.split())
                check_button.set_active(is_active)
                check_hbox.pack_start(check_button, expand=False, fill=False)

        info = HobInfoButton(tooltip_extra, self)
        check_hbox.pack_end(info, expand=False, fill=False)

        rootfs_combo.connect("changed", self.rootfs_combo_changed_cb, all_package_format, check_hbox)

        pkgfmt_hbox.show_all()

        return pkgfmt_hbox, rootfs_combo, check_hbox

    def editable_settings_cell_edited(self, cell, path_string, new_text, model):
        it = model.get_iter_from_string(path_string)
        column = cell.get_data("column")
        model.set(it, column, new_text)

    def editable_settings_add_item_clicked(self, button, model):
        new_item = ["##KEY##", "##VALUE##"]

        iter = model.append()
        model.set (iter,
            0, new_item[0],
            1, new_item[1],
       )

    def editable_settings_remove_item_clicked(self, button, treeview):
        selection = treeview.get_selection()
        model, iter = selection.get_selected()

        if iter:
            path = model.get_path(iter)[0]
            model.remove(iter)
 
    def gen_editable_settings(self, setting, tooltip=""):
        setting_hbox = gtk.HBox(False, 12)

        vbox = gtk.VBox(False, 12)
        setting_hbox.pack_start(vbox, expand=True, fill=True)

        setting_store = gtk.ListStore(gobject.TYPE_STRING, gobject.TYPE_STRING)
        for key in setting.keys():
            setting_store.set(setting_store.append(), 0, key, 1, setting[key])

        setting_tree = gtk.TreeView(setting_store)
        setting_tree.set_headers_visible(True)
        setting_tree.set_size_request(300, 100)

        col = gtk.TreeViewColumn('Key')
        col.set_min_width(100)
        col.set_max_width(150)
        col.set_resizable(True)
        col1 = gtk.TreeViewColumn('Value')
        col1.set_min_width(100)
        col1.set_max_width(150)
        col1.set_resizable(True)
        setting_tree.append_column(col)
        setting_tree.append_column(col1)
        cell = gtk.CellRendererText()
        cell.set_property('width-chars', 10)
        cell.set_property('editable', True)
        cell.set_data("column", 0)
        cell.connect("edited", self.editable_settings_cell_edited, setting_store)
        cell1 = gtk.CellRendererText()
        cell1.set_property('width-chars', 10)
        cell1.set_property('editable', True)
        cell1.set_data("column", 1)
        cell1.connect("edited", self.editable_settings_cell_edited, setting_store)
        col.pack_start(cell, True)
        col1.pack_end(cell1, True)
        col.set_attributes(cell, text=0)
        col1.set_attributes(cell1, text=1)

        scroll = gtk.ScrolledWindow()
        scroll.set_shadow_type(gtk.SHADOW_IN)
        scroll.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
        scroll.add(setting_tree)
        vbox.pack_start(scroll, expand=True, fill=True)

        # some buttons
        hbox = gtk.HBox(True, 6)
        vbox.pack_start(hbox, False, False)

        button = gtk.Button(stock=gtk.STOCK_ADD)
        button.connect("clicked", self.editable_settings_add_item_clicked, setting_store)
        hbox.pack_start(button)

        button = gtk.Button(stock=gtk.STOCK_REMOVE)
        button.connect("clicked", self.editable_settings_remove_item_clicked, setting_tree)
        hbox.pack_start(button)

        info = HobInfoButton(tooltip, self)
        setting_hbox.pack_start(info, expand=False, fill=False)

        return setting_hbox, setting_store

    def create_others_page(self):
        advanced_vbox = gtk.VBox(False, 6)
        advanced_vbox.set_border_width(6)

        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=True, fill=True)
        label = self.gen_label_widget("<span weight=\"bold\">Add your own variables:</span>")
        tooltip = "These are key/value pairs for your extra settings. Click \'Add\' and then directly edit the key and the value"
        setting_widget, self.setting_store = self.gen_editable_settings(self.configuration.extra_setting,"<b>Add your own variables</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(setting_widget, expand=True, fill=True)

        return advanced_vbox

    def create_visual_elements(self):
        self.nb = gtk.Notebook()
        self.nb.set_show_tabs(True)        
        self.nb.append_page(self.create_build_environment_page(), gtk.Label("Build environment"))
        self.nb.append_page(self.create_shared_state_page(), gtk.Label("Shared state"))
        self.nb.append_page(self.create_network_page(), gtk.Label("Network"))        
        self.nb.append_page(self.create_others_page(), gtk.Label("Others"))
        self.nb.set_current_page(0)
        self.vbox.pack_start(self.nb, expand=True, fill=True)
        self.vbox.pack_end(gtk.HSeparator(), expand=True, fill=True)

        self.show_all()

    def destroy(self):
        self.handler.disconnect(self.proxy_test_passed_id)
        self.handler.disconnect(self.proxy_test_failed_id)
        super(SimpleSettingsDialog, self).destroy()
