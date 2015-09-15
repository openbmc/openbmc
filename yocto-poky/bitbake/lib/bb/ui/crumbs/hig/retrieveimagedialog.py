#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2013   Intel Corporation
#
# Authored by Cristiana Voicu <cristiana.voicu@intel.com>
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

class RetrieveImageDialog (gtk.FileChooserDialog):
    """
    This class is used to create a dialog that permits to retrieve
    a custom image saved previously from Hob.
    """
    def __init__(self, directory,title, parent, flags, buttons=None):
        super(RetrieveImageDialog, self).__init__(title, None, gtk.FILE_CHOOSER_ACTION_OPEN,
            (gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL,gtk.STOCK_OPEN, gtk.RESPONSE_OK))
        self.directory = directory

        # create visual elements on the dialog
        self.create_visual_elements()

    def create_visual_elements(self):
        self.set_show_hidden(True)
        self.set_default_response(gtk.RESPONSE_OK)
        self.set_current_folder(self.directory)

        vbox = self.get_children()[0].get_children()[0].get_children()[0]
        for child in vbox.get_children()[0].get_children()[0].get_children()[0].get_children():
            vbox.get_children()[0].get_children()[0].get_children()[0].remove(child)

        label1 = gtk.Label()
        label1.set_text("File system" + self.directory)
        label1.show()
        vbox.get_children()[0].get_children()[0].get_children()[0].pack_start(label1, expand=False, fill=False, padding=0)
        vbox.get_children()[0].get_children()[1].get_children()[0].hide()

        self.get_children()[0].get_children()[1].get_children()[0].set_label("Select")
