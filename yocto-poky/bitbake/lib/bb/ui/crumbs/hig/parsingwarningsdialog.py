#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2011-2012   Intel Corporation
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
import gobject
from bb.ui.crumbs.hobwidget import HobAltButton
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

#
# ParsingWarningsDialog
#
class ParsingWarningsDialog (CrumbsDialog):

    def __init__(self, title, warnings, parent, flags, buttons=None):
        super(ParsingWarningsDialog, self).__init__(title, parent, flags, buttons)

        self.warnings = warnings
        self.warning_on = 0
        self.warn_nb = len(warnings)

        # create visual elements on the dialog
        self.create_visual_elements()

    def cancel_button_cb(self, button):
        self.destroy()

    def previous_button_cb(self, button):
        self.warning_on = self.warning_on - 1
        self.refresh_components()

    def next_button_cb(self, button):
        self.warning_on = self.warning_on + 1
        self.refresh_components()

    def refresh_components(self):
        lbl = self.warnings[self.warning_on]
        #when the warning text has more than 400 chars, it uses a scroll bar
        if 0<= len(lbl) < 400:
            self.warning_label.set_size_request(320, 230)
            self.warning_label.set_use_markup(True)
            self.warning_label.set_line_wrap(True)
            self.warning_label.set_markup(lbl)
            self.warning_label.set_property("yalign", 0.00)
        else:
            self.textWindow.set_shadow_type(gtk.SHADOW_IN)
            self.textWindow.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
            self.msgView = gtk.TextView()
            self.msgView.set_editable(False)
            self.msgView.set_wrap_mode(gtk.WRAP_WORD)
            self.msgView.set_cursor_visible(False)
            self.msgView.set_size_request(320, 230)
            self.buf = gtk.TextBuffer()
            self.buf.set_text(lbl)
            self.msgView.set_buffer(self.buf)
            self.textWindow.add(self.msgView)
            self.msgView.show()

        if self.warning_on==0:
            self.previous_button.set_sensitive(False)
        else:
            self.previous_button.set_sensitive(True)

        if self.warning_on==self.warn_nb-1:
            self.next_button.set_sensitive(False)
        else:
            self.next_button.set_sensitive(True)

        if self.warn_nb>1:
            self.heading = "Warning " + str(self.warning_on + 1) + " of " + str(self.warn_nb)
            self.heading_label.set_markup('<span weight="bold">%s</span>' % self.heading)
        else:
            self.heading = "Warning"
            self.heading_label.set_markup('<span weight="bold">%s</span>' % self.heading)

        self.show_all()

        if 0<= len(lbl) < 400:
            self.textWindow.hide()
        else:
            self.warning_label.hide()

    def create_visual_elements(self):
        self.set_size_request(350, 350)
        self.heading_label = gtk.Label()
        self.heading_label.set_alignment(0, 0)
        self.warning_label = gtk.Label()
        self.warning_label.set_selectable(True)
        self.warning_label.set_alignment(0, 0)
        self.textWindow = gtk.ScrolledWindow()

        table = gtk.Table(1, 10, False)

        cancel_button = gtk.Button()
        cancel_button.set_label("Close")
        cancel_button.connect("clicked", self.cancel_button_cb)
        cancel_button.set_size_request(110, 30)

        self.previous_button = gtk.Button()
        image1 = gtk.image_new_from_stock(gtk.STOCK_GO_BACK, gtk.ICON_SIZE_BUTTON)
        image1.show()
        box = gtk.HBox(False, 6)
        box.show()
        self.previous_button.add(box)
        lbl = gtk.Label("Previous")
        lbl.show()
        box.pack_start(image1, expand=False, fill=False, padding=3)
        box.pack_start(lbl, expand=True, fill=True, padding=3)
        self.previous_button.connect("clicked", self.previous_button_cb)
        self.previous_button.set_size_request(110, 30)

        self.next_button = gtk.Button()
        image2 = gtk.image_new_from_stock(gtk.STOCK_GO_FORWARD, gtk.ICON_SIZE_BUTTON)
        image2.show()
        box = gtk.HBox(False, 6)
        box.show()
        self.next_button.add(box)
        lbl = gtk.Label("Next")
        lbl.show()
        box.pack_start(lbl, expand=True, fill=True, padding=3)
        box.pack_start(image2, expand=False, fill=False, padding=3)
        self.next_button.connect("clicked", self.next_button_cb)
        self.next_button.set_size_request(110, 30)

        #when there more than one warning, we need "previous" and "next" button
        if self.warn_nb>1:
            self.vbox.pack_start(self.heading_label, expand=False, fill=False)
            self.vbox.pack_start(self.warning_label, expand=False, fill=False)
            self.vbox.pack_start(self.textWindow, expand=False, fill=False)
            table.attach(cancel_button, 6, 7, 0, 1, xoptions=gtk.SHRINK)
            table.attach(self.previous_button, 7, 8, 0, 1, xoptions=gtk.SHRINK)
            table.attach(self.next_button, 8, 9, 0, 1, xoptions=gtk.SHRINK)
            self.vbox.pack_end(table, expand=False, fill=False)
        else:
            self.vbox.pack_start(self.heading_label, expand=False, fill=False)
            self.vbox.pack_start(self.warning_label, expand=False, fill=False)
            self.vbox.pack_start(self.textWindow, expand=False, fill=False)
            cancel_button = self.add_button("Close", gtk.RESPONSE_CANCEL)
            HobAltButton.style_button(cancel_button)

        self.refresh_components()
