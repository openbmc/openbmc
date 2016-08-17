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
import os
from bb.ui.crumbs.hobwidget import HobViewTable, HobInfoButton, HobButton, HobAltButton
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.layerselectiondialog import LayerSelectionDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class ImageSelectionDialog (CrumbsDialog):

    __columns__ = [{
            'col_name' : 'Image name',
            'col_id'   : 0,
            'col_style': 'text',
            'col_min'  : 400,
            'col_max'  : 400
        }, {
            'col_name' : 'Select',
            'col_id'   : 1,
            'col_style': 'radio toggle',
            'col_min'  : 160,
            'col_max'  : 160
    }]


    def __init__(self, image_folder, image_types, title, parent, flags, buttons=None, image_extension = {}):
        super(ImageSelectionDialog, self).__init__(title, parent, flags, buttons)
        self.connect("response", self.response_cb)

        self.image_folder = image_folder
        self.image_types  = image_types
        self.image_list = []
        self.image_names = []
        self.image_extension = image_extension

        # create visual elements on the dialog
        self.create_visual_elements()

        self.image_store = gtk.ListStore(gobject.TYPE_STRING, gobject.TYPE_BOOLEAN)
        self.fill_image_store()

    def create_visual_elements(self):
        hbox = gtk.HBox(False, 6)

        self.vbox.pack_start(hbox, expand=False, fill=False)

        entry = gtk.Entry()
        entry.set_text(self.image_folder)
        table = gtk.Table(1, 10, True)
        table.set_size_request(560, -1)
        hbox.pack_start(table, expand=False, fill=False)
        table.attach(entry, 0, 9, 0, 1)
        image = gtk.Image()
        image.set_from_stock(gtk.STOCK_OPEN, gtk.ICON_SIZE_BUTTON)
        open_button = gtk.Button()
        open_button.set_image(image)
        open_button.connect("clicked", self.select_path_cb, self, entry)
        table.attach(open_button, 9, 10, 0, 1)

        self.image_table = HobViewTable(self.__columns__, "Images")
        self.image_table.set_size_request(-1, 300)
        self.image_table.connect("toggled", self.toggled_cb)
        self.image_table.connect_group_selection(self.table_selected_cb)
        self.image_table.connect("row-activated", self.row_actived_cb)
        self.vbox.pack_start(self.image_table, expand=True, fill=True)

        self.show_all()

    def change_image_cb(self, model, path, columnid):
        if not model:
            return
        iter = model.get_iter_first()
        while iter:
            rowpath = model.get_path(iter)
            model[rowpath][columnid] = False
            iter = model.iter_next(iter)

        model[path][columnid] = True

    def toggled_cb(self, table, cell, path, columnid, tree):
        model = tree.get_model()
        self.change_image_cb(model, path, columnid)

    def table_selected_cb(self, selection):
        model, paths = selection.get_selected_rows()
        if paths:
            self.change_image_cb(model, paths[0], 1)

    def row_actived_cb(self, tab, model, path):
        self.change_image_cb(model, path, 1)
        self.emit('response', gtk.RESPONSE_YES)

    def select_path_cb(self, action, parent, entry):
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
            self.image_folder = path
            self.fill_image_store()

        dialog.destroy()

    def fill_image_store(self):
        self.image_list = []
        self.image_store.clear()
        imageset = set()
        for root, dirs, files in os.walk(self.image_folder):
            # ignore the sub directories
            dirs[:] = []
            for f in files:
                for image_type in self.image_types:
                    if image_type in self.image_extension:
                        real_types = self.image_extension[image_type]
                    else:
                        real_types = [image_type]
                    for real_image_type in real_types:
                        if f.endswith('.' + real_image_type):
                            imageset.add(f.rsplit('.' + real_image_type)[0].rsplit('.rootfs')[0])
                            self.image_list.append(f)

        for image in imageset:
            self.image_store.set(self.image_store.append(), 0, image, 1, False)

        self.image_table.set_model(self.image_store)

    def response_cb(self, dialog, response_id):
        self.image_names = []
        if response_id == gtk.RESPONSE_YES:
            iter = self.image_store.get_iter_first()
            while iter:
                path = self.image_store.get_path(iter)
                if self.image_store[path][1]:
                    for f in self.image_list:
                        if f.startswith(self.image_store[path][0] + '.'):
                            self.image_names.append(f)
                    break
                iter = self.image_store.iter_next(iter)
