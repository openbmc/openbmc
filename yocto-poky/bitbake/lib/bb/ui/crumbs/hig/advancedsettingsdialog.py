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
import hashlib
from bb.ui.crumbs.hobwidget import HobInfoButton, HobButton
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

class AdvancedSettingsDialog (CrumbsDialog, SettingsUIHelper):
    
    def details_cb(self, button, parent, protocol):
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

    def set_save_button(self, button):
        self.save_button = button

    def rootfs_combo_changed_cb(self, rootfs_combo, all_package_format, check_hbox):
        combo_item = self.rootfs_combo.get_active_text()
        modified = False
        for child in check_hbox.get_children():
            if isinstance(child, gtk.CheckButton):
                check_hbox.remove(child)
                modified = True
        for format in all_package_format:
            if format != combo_item:
                check_button = gtk.CheckButton(format)
                check_hbox.pack_start(check_button, expand=False, fill=False)
                modified = True
        if modified:
            check_hbox.remove(self.pkgfmt_info)
            check_hbox.pack_start(self.pkgfmt_info, expand=False, fill=False)
        check_hbox.show_all()

    def gen_pkgfmt_widget(self, curr_package_format, all_package_format, tooltip_combo="", tooltip_extra=""):
        pkgfmt_vbox = gtk.VBox(False, 6)

        label = self.gen_label_widget("Root file system package format")
        pkgfmt_vbox.pack_start(label, expand=False, fill=False)

        rootfs_format = ""
        if curr_package_format:
            rootfs_format = curr_package_format.split()[0]

        rootfs_format_widget, rootfs_combo = self.gen_combo_widget(rootfs_format, all_package_format, tooltip_combo)
        pkgfmt_vbox.pack_start(rootfs_format_widget, expand=False, fill=False)

        label = self.gen_label_widget("Additional package formats")
        pkgfmt_vbox.pack_start(label, expand=False, fill=False)

        check_hbox = gtk.HBox(False, 12)
        pkgfmt_vbox.pack_start(check_hbox, expand=False, fill=False)
        for format in all_package_format:
            if format != rootfs_format:
                check_button = gtk.CheckButton(format)
                is_active = (format in curr_package_format.split())
                check_button.set_active(is_active)
                check_hbox.pack_start(check_button, expand=False, fill=False)

        self.pkgfmt_info = HobInfoButton(tooltip_extra, self)
        check_hbox.pack_start(self.pkgfmt_info, expand=False, fill=False)

        rootfs_combo.connect("changed", self.rootfs_combo_changed_cb, all_package_format, check_hbox)

        pkgfmt_vbox.show_all()

        return pkgfmt_vbox, rootfs_combo, check_hbox

    def __init__(self, title, configuration, all_image_types,
            all_package_formats, all_distros, all_sdk_machines,
            max_threads, parent, flags, buttons=None):
        super(AdvancedSettingsDialog, self).__init__(title, parent, flags, buttons)

        # class members from other objects
        # bitbake settings from Builder.Configuration
        self.configuration = configuration
        self.image_types = all_image_types
        self.all_package_formats = all_package_formats
        self.all_distros = all_distros[:]
        self.all_sdk_machines = all_sdk_machines
        self.max_threads = max_threads

        # class members for internal use
        self.distro_combo = None
        self.dldir_text = None
        self.sstatedir_text = None
        self.sstatemirror_text = None
        self.bb_spinner = None
        self.pmake_spinner = None
        self.rootfs_size_spinner = None
        self.extra_size_spinner = None
        self.gplv3_checkbox = None
        self.sdk_checkbox = None
        self.image_types_checkbuttons = {}

        self.md5 = self.config_md5()
        self.settings_changed = False

        # create visual elements on the dialog
        self.save_button = None
        self.create_visual_elements()
        self.connect("response", self.response_cb)

    def _get_sorted_value(self, var):
        return " ".join(sorted(str(var).split())) + "\n"

    def config_md5(self):
        data = ""
        data += ("PACKAGE_CLASSES: "      + self.configuration.curr_package_format + '\n')
        data += ("DISTRO: "               + self._get_sorted_value(self.configuration.curr_distro))
        data += ("IMAGE_ROOTFS_SIZE: "    + self._get_sorted_value(self.configuration.image_rootfs_size))
        data += ("IMAGE_EXTRA_SIZE: "     + self._get_sorted_value(self.configuration.image_extra_size))
        data += ("INCOMPATIBLE_LICENSE: " + self._get_sorted_value(self.configuration.incompat_license))
        data += ("SDK_MACHINE: "          + self._get_sorted_value(self.configuration.curr_sdk_machine))
        data += ("TOOLCHAIN_BUILD: "      + self._get_sorted_value(self.configuration.toolchain_build))
        data += ("IMAGE_FSTYPES: "        + self._get_sorted_value(self.configuration.image_fstypes))
        return hashlib.md5(data).hexdigest()

    def create_visual_elements(self):
        self.nb = gtk.Notebook()
        self.nb.set_show_tabs(True)
        self.nb.append_page(self.create_image_types_page(), gtk.Label("Image types"))
        self.nb.append_page(self.create_output_page(), gtk.Label("Output"))
        self.nb.set_current_page(0)
        self.vbox.pack_start(self.nb, expand=True, fill=True)
        self.vbox.pack_end(gtk.HSeparator(), expand=True, fill=True)

        self.show_all()

    def get_num_checked_image_types(self):
        total = 0
        for b in self.image_types_checkbuttons.values():
            if b.get_active():
              total = total + 1
        return total

    def set_save_button_state(self):
        if self.save_button:
            self.save_button.set_sensitive(self.get_num_checked_image_types() > 0)

    def image_type_checkbutton_clicked_cb(self, button):
        self.set_save_button_state()
        if self.get_num_checked_image_types() == 0:
            # Show an error dialog
            lbl = "<b>Select an image type</b>"
            msg = "You need to select at least one image type."
            dialog = CrumbsMessageDialog(self, lbl, gtk.MESSAGE_WARNING, msg)
            button = dialog.add_button("OK", gtk.RESPONSE_OK)
            HobButton.style_button(button)
            response = dialog.run()
            dialog.destroy()

    def create_image_types_page(self):
        main_vbox = gtk.VBox(False, 16)
        main_vbox.set_border_width(6)

        advanced_vbox = gtk.VBox(False, 6)
        advanced_vbox.set_border_width(6)

        distro_vbox = gtk.VBox(False, 6)        
        label = self.gen_label_widget("Distro:")
        tooltip = "Selects the Yocto Project distribution you want"
        try:
            i = self.all_distros.index( "defaultsetup" )
        except ValueError:
            i = -1
        if i != -1:
            self.all_distros[ i ] = "Default"
            if self.configuration.curr_distro == "defaultsetup":
                self.configuration.curr_distro = "Default"
        distro_widget, self.distro_combo = self.gen_combo_widget(self.configuration.curr_distro, self.all_distros,"<b>Distro</b>" + "*" + tooltip)
        distro_vbox.pack_start(label, expand=False, fill=False)
        distro_vbox.pack_start(distro_widget, expand=False, fill=False)
        main_vbox.pack_start(distro_vbox, expand=False, fill=False)


        rows = (len(self.image_types)+1)/3
        table = gtk.Table(rows + 1, 10, True)
        advanced_vbox.pack_start(table, expand=False, fill=False)

        tooltip = "Image file system types you want."
        info = HobInfoButton("<b>Image types</b>" + "*" + tooltip, self)
        label = self.gen_label_widget("Image types:")
        align = gtk.Alignment(0, 0.5, 0, 0)
        table.attach(align, 0, 4, 0, 1)
        align.add(label)
        table.attach(info, 4, 5, 0, 1)

        i = 1
        j = 1
        for image_type in sorted(self.image_types):
            self.image_types_checkbuttons[image_type] = gtk.CheckButton(image_type)
            self.image_types_checkbuttons[image_type].connect("toggled", self.image_type_checkbutton_clicked_cb)
            article = ""
            if image_type.startswith(("a", "e", "i", "o", "u")):
                article = "n"
            if image_type == "live":
                self.image_types_checkbuttons[image_type].set_tooltip_text("Build iso and hddimg images")
            else:
                self.image_types_checkbuttons[image_type].set_tooltip_text("Build a%s %s image" % (article, image_type))
            table.attach(self.image_types_checkbuttons[image_type], j - 1, j + 3, i, i + 1)
            if image_type in self.configuration.image_fstypes.split():
                self.image_types_checkbuttons[image_type].set_active(True)
            i += 1
            if i > rows:
                i = 1
                j = j + 4

        main_vbox.pack_start(advanced_vbox, expand=False, fill=False)
        self.set_save_button_state()
        
        return main_vbox

    def create_output_page(self):
        advanced_vbox = gtk.VBox(False, 6)
        advanced_vbox.set_border_width(6)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">Package format</span>'), expand=False, fill=False)
        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        tooltip_combo = "Selects the package format used to generate rootfs."
        tooltip_extra = "Selects extra package formats to build"
        pkgfmt_widget, self.rootfs_combo, self.check_hbox = self.gen_pkgfmt_widget(self.configuration.curr_package_format, self.all_package_formats,"<b>Root file system package format</b>" + "*" + tooltip_combo,"<b>Additional package formats</b>" + "*" + tooltip_extra)
        sub_vbox.pack_start(pkgfmt_widget, expand=False, fill=False)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">Image size</span>'), expand=False, fill=False)
        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("Image basic size (in MB)")
        tooltip = "Defines the size for the generated image. The OpenEmbedded build system determines the final size for the generated image using an algorithm that takes into account the initial disk space used for the generated image, the Image basic size value, and the Additional free space value.\n\nFor more information, check the <a href=\"http://www.yoctoproject.org/docs/current/poky-ref-manual/poky-ref-manual.html#var-IMAGE_ROOTFS_SIZE\">Yocto Project Reference Manual</a>."
        rootfs_size_widget, self.rootfs_size_spinner = self.gen_spinner_widget(int(self.configuration.image_rootfs_size*1.0/1024), 0, 65536,"<b>Image basic size</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(rootfs_size_widget, expand=False, fill=False)

        sub_vbox = gtk.VBox(False, 6)
        advanced_vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = self.gen_label_widget("Additional free space (in MB)")
        tooltip = "Sets extra free disk space to be added to the generated image. Use this variable when you want to ensure that a specific amount of free disk space is available on a device after an image is installed and running."
        extra_size_widget, self.extra_size_spinner = self.gen_spinner_widget(int(self.configuration.image_extra_size*1.0/1024), 0, 65536,"<b>Additional free space</b>" + "*" + tooltip)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(extra_size_widget, expand=False, fill=False)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">Licensing</span>'), expand=False, fill=False)
        self.gplv3_checkbox = gtk.CheckButton("Exclude GPLv3 packages")
        self.gplv3_checkbox.set_tooltip_text("Check this box to prevent GPLv3 packages from being included in your image")
        if "GPLv3" in self.configuration.incompat_license.split():
            self.gplv3_checkbox.set_active(True)
        else:
            self.gplv3_checkbox.set_active(False)
        advanced_vbox.pack_start(self.gplv3_checkbox, expand=False, fill=False)

        advanced_vbox.pack_start(self.gen_label_widget('<span weight="bold">SDK</span>'), expand=False, fill=False)
        sub_hbox = gtk.HBox(False, 6)
        advanced_vbox.pack_start(sub_hbox, expand=False, fill=False)
        self.sdk_checkbox = gtk.CheckButton("Populate SDK")
        tooltip = "Check this box to generate an SDK tarball that consists of the cross-toolchain and a sysroot that contains development packages for your image."
        self.sdk_checkbox.set_tooltip_text(tooltip)
        self.sdk_checkbox.set_active(self.configuration.toolchain_build)
        sub_hbox.pack_start(self.sdk_checkbox, expand=False, fill=False)

        tooltip = "Select the host platform for which you want to run the toolchain contained in the SDK tarball."
        sdk_machine_widget, self.sdk_machine_combo = self.gen_combo_widget(self.configuration.curr_sdk_machine, self.all_sdk_machines,"<b>Populate SDK</b>" + "*" + tooltip)
        sub_hbox.pack_start(sdk_machine_widget, expand=False, fill=False)

        return advanced_vbox

    def response_cb(self, dialog, response_id):
        package_format = []
        package_format.append(self.rootfs_combo.get_active_text())
        for child in self.check_hbox:
            if isinstance(child, gtk.CheckButton) and child.get_active():
                package_format.append(child.get_label())
        self.configuration.curr_package_format = " ".join(package_format)

        distro = self.distro_combo.get_active_text()
        if distro == "Default":
            distro = "defaultsetup"
        self.configuration.curr_distro = distro
        self.configuration.image_rootfs_size = self.rootfs_size_spinner.get_value_as_int() * 1024
        self.configuration.image_extra_size = self.extra_size_spinner.get_value_as_int() * 1024

        self.configuration.image_fstypes = ""
        for image_type in self.image_types:
            if self.image_types_checkbuttons[image_type].get_active():
                self.configuration.image_fstypes += (" " + image_type)
        self.configuration.image_fstypes.strip()

        if self.gplv3_checkbox.get_active():
            if "GPLv3" not in self.configuration.incompat_license.split():
                self.configuration.incompat_license += " GPLv3"
        else:
            if "GPLv3" in self.configuration.incompat_license.split():
                self.configuration.incompat_license = self.configuration.incompat_license.split().remove("GPLv3")
                self.configuration.incompat_license = " ".join(self.configuration.incompat_license or [])
        self.configuration.incompat_license = self.configuration.incompat_license.strip()

        self.configuration.toolchain_build = self.sdk_checkbox.get_active()
        self.configuration.curr_sdk_machine = self.sdk_machine_combo.get_active_text()
        md5 = self.config_md5()
        self.settings_changed = (self.md5 != md5)
