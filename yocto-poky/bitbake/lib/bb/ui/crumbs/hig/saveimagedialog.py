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
import glib
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.crumbsmessagedialog import CrumbsMessageDialog
from bb.ui.crumbs.hobwidget import HobButton

class SaveImageDialog (CrumbsDialog):
    """
    This class is used to create a dialog that permits to save
    a custom image in a predefined directory.
    """
    def __init__(self, directory, name, description, title, parent, flags, buttons=None):
        super(SaveImageDialog, self).__init__(title, parent, flags, buttons)
        self.directory = directory
        self.builder = parent
        self.name_field = name
        self.description_field = description

        # create visual elements on the dialog
        self.create_visual_elements()

    def create_visual_elements(self):
        self.set_default_response(gtk.RESPONSE_OK)
        self.vbox.set_border_width(6)

        sub_vbox = gtk.VBox(False, 12)
        self.vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = gtk.Label()
        label.set_alignment(0, 0)
        label.set_markup("<b>Name</b>")
        sub_label = gtk.Label()
        sub_label.set_alignment(0, 0)
        content = "Image recipe names should be all lowercase and include only alphanumeric\n"
        content += "characters. The only special character you can use is the ASCII hyphen (-)."
        sub_label.set_markup(content)
        self.name_entry = gtk.Entry()
        self.name_entry.set_text(self.name_field)
        self.name_entry.set_size_request(350,30)
        self.name_entry.connect("changed", self.name_entry_changed)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(sub_label, expand=False, fill=False)
        sub_vbox.pack_start(self.name_entry, expand=False, fill=False)

        sub_vbox = gtk.VBox(False, 12)
        self.vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = gtk.Label()
        label.set_alignment(0, 0)
        label.set_markup("<b>Description</b> (optional)")
        sub_label = gtk.Label()
        sub_label.set_alignment(0, 0)
        sub_label.set_markup("The description should be less than 150 characters long.")
        self.description_entry = gtk.TextView()
        description_buffer = self.description_entry.get_buffer()
        description_buffer.set_text(self.description_field)
        description_buffer.connect("insert-text", self.limit_description_length)
        self.description_entry.set_wrap_mode(gtk.WRAP_WORD)
        self.description_entry.set_size_request(350,50)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(sub_label, expand=False, fill=False)
        sub_vbox.pack_start(self.description_entry, expand=False, fill=False)

        sub_vbox = gtk.VBox(False, 12)
        self.vbox.pack_start(sub_vbox, expand=False, fill=False)
        label = gtk.Label()
        label.set_alignment(0, 0)
        label.set_markup("Your image recipe will be saved to:")
        sub_label = gtk.Label()
        sub_label.set_alignment(0, 0)
        sub_label.set_markup(self.directory)
        sub_vbox.pack_start(label, expand=False, fill=False)
        sub_vbox.pack_start(sub_label, expand=False, fill=False)

        table = gtk.Table(1, 4, True)

        cancel_button = gtk.Button()
        cancel_button.set_label("Cancel")
        cancel_button.connect("clicked", self.cancel_button_cb)
        cancel_button.set_size_request(110, 30)

        self.save_button = gtk.Button()
        self.save_button.set_label("Save")
        self.save_button.connect("clicked", self.save_button_cb)
        self.save_button.set_size_request(110, 30)
        if self.name_entry.get_text() == '':
            self.save_button.set_sensitive(False)

        table.attach(cancel_button, 2, 3, 0, 1)
        table.attach(self.save_button, 3, 4, 0, 1)
        self.vbox.pack_end(table, expand=False, fill=False)

        self.show_all()

    def limit_description_length(self, textbuffer, iter, text, length):
        buffer_bounds = textbuffer.get_bounds()
        entire_text = textbuffer.get_text(*buffer_bounds)
        entire_text += text
        if len(entire_text)>150 or text=="\n":
            textbuffer.emit_stop_by_name("insert-text")

    def name_entry_changed(self, entry):
        text = entry.get_text()
        if text == '':
            self.save_button.set_sensitive(False)
        else:
            self.save_button.set_sensitive(True)

    def cancel_button_cb(self, button):
        self.destroy()

    def save_button_cb(self, button):
        text = self.name_entry.get_text()
        new_text = text.replace("-","")
        description_buffer = self.description_entry.get_buffer()
        description = description_buffer.get_text(description_buffer.get_start_iter(),description_buffer.get_end_iter())
        if new_text.islower() and new_text.isalnum():
            self.builder.image_details_page.image_saved = True
            self.builder.customized = False
            self.builder.generate_new_image(self.directory+text, description)
            self.builder.recipe_model.set_in_list(text, description)
            self.builder.recipe_model.set_selected_image(text)
            self.builder.image_details_page.show_page(self.builder.IMAGE_GENERATED)
            self.builder.image_details_page.name_field_template = text
            self.builder.image_details_page.description_field_template = description
            self.destroy()
        else:
            self.show_invalid_input_error_dialog()

    def show_invalid_input_error_dialog(self):
        lbl = "<b>Invalid characters in image recipe name</b>"
        msg = "Image recipe names should be all lowercase and\n"
        msg += "include only alphanumeric characters. The only\n"
        msg += "special character you can use is the ASCII hyphen (-)."
        dialog = CrumbsMessageDialog(self, lbl, gtk.MESSAGE_ERROR, msg)
        button = dialog.add_button("Close", gtk.RESPONSE_OK)
        HobButton.style_button(button)

        res = dialog.run()
        self.name_entry.grab_focus()
        dialog.destroy()
