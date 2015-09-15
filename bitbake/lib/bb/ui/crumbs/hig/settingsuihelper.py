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
import os
from bb.ui.crumbs.hobwidget import HobInfoButton, HobButton, HobAltButton

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class SettingsUIHelper():

    def gen_label_widget(self, content):
        label = gtk.Label()
        label.set_alignment(0, 0)
        label.set_markup(content)
        label.show()
        return label

    def gen_label_info_widget(self, content, tooltip):
        table = gtk.Table(1, 10, False)
        label = self.gen_label_widget(content)
        info = HobInfoButton(tooltip, self)
        table.attach(label, 0, 1, 0, 1, xoptions=gtk.FILL)
        table.attach(info, 1, 2, 0, 1, xoptions=gtk.FILL, xpadding=10)
        return table

    def gen_spinner_widget(self, content, lower, upper, tooltip=""):
        hbox = gtk.HBox(False, 12)
        adjust = gtk.Adjustment(value=content, lower=lower, upper=upper, step_incr=1)
        spinner = gtk.SpinButton(adjustment=adjust, climb_rate=1, digits=0)

        spinner.set_value(content)
        hbox.pack_start(spinner, expand=False, fill=False)

        info = HobInfoButton(tooltip, self)
        hbox.pack_start(info, expand=False, fill=False)

        hbox.show_all()
        return hbox, spinner

    def gen_combo_widget(self, curr_item, all_item, tooltip=""):
        hbox = gtk.HBox(False, 12)
        combo = gtk.combo_box_new_text()
        hbox.pack_start(combo, expand=False, fill=False)

        index = 0
        for item in all_item or []:
            combo.append_text(item)
            if item == curr_item:
                combo.set_active(index)
            index += 1

        info = HobInfoButton(tooltip, self)
        hbox.pack_start(info, expand=False, fill=False)

        hbox.show_all()
        return hbox, combo

    def entry_widget_select_path_cb(self, action, parent, entry):
        dialog = gtk.FileChooserDialog("", parent,
                                       gtk.FILE_CHOOSER_ACTION_SELECT_FOLDER)
        text = entry.get_text()
        dialog.set_current_folder(text if len(text) > 0 else os.getcwd())
        button = dialog.add_button("Cancel", gtk.RESPONSE_NO)
        HobAltButton.style_button(button)
        button = dialog.add_button("Open", gtk.RESPONSE_YES)
        HobButton.style_button(button)
        response = dialog.run()
        if response == gtk.RESPONSE_YES:
            path = dialog.get_filename()
            entry.set_text(path)

        dialog.destroy()

    def gen_entry_widget(self, content, parent, tooltip="", need_button=True):
        hbox = gtk.HBox(False, 12)
        entry = gtk.Entry()
        entry.set_text(content)
        entry.set_size_request(350,30)

        if need_button:
            table = gtk.Table(1, 10, False)
            hbox.pack_start(table, expand=True, fill=True)
            table.attach(entry, 0, 9, 0, 1, xoptions=gtk.SHRINK)
            image = gtk.Image()
            image.set_from_stock(gtk.STOCK_OPEN,gtk.ICON_SIZE_BUTTON)
            open_button = gtk.Button()
            open_button.set_image(image)
            open_button.connect("clicked", self.entry_widget_select_path_cb, parent, entry)
            table.attach(open_button, 9, 10, 0, 1, xoptions=gtk.SHRINK)
        else:
            hbox.pack_start(entry, expand=True, fill=True)

        if tooltip != "":
            info = HobInfoButton(tooltip, self)
            hbox.pack_start(info, expand=False, fill=False)

        hbox.show_all()
        return hbox, entry
