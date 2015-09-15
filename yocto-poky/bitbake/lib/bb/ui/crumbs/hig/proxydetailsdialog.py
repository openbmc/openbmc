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
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class ProxyDetailsDialog (CrumbsDialog):

    def __init__(self, title, user, passwd, parent, flags, buttons=None):
        super(ProxyDetailsDialog, self).__init__(title, parent, flags, buttons)
        self.connect("response", self.response_cb)

        self.auth = not (user == None or passwd == None or user == "")
        self.user = user or ""
        self.passwd = passwd or ""

        # create visual elements on the dialog
        self.create_visual_elements()

    def create_visual_elements(self):
        self.auth_checkbox = gtk.CheckButton("Use authentication")
        self.auth_checkbox.set_tooltip_text("Check this box to set the username and the password")
        self.auth_checkbox.set_active(self.auth)
        self.auth_checkbox.connect("toggled", self.auth_checkbox_toggled_cb)
        self.vbox.pack_start(self.auth_checkbox, expand=False, fill=False)

        hbox = gtk.HBox(False, 6)
        self.user_label = gtk.Label("Username:")
        self.user_text = gtk.Entry()
        self.user_text.set_text(self.user)
        hbox.pack_start(self.user_label, expand=False, fill=False)
        hbox.pack_end(self.user_text, expand=False, fill=False)
        self.vbox.pack_start(hbox, expand=False, fill=False)

        hbox = gtk.HBox(False, 6)
        self.passwd_label = gtk.Label("Password:")
        self.passwd_text = gtk.Entry()
        self.passwd_text.set_text(self.passwd)
        hbox.pack_start(self.passwd_label, expand=False, fill=False)
        hbox.pack_end(self.passwd_text, expand=False, fill=False)
        self.vbox.pack_start(hbox, expand=False, fill=False)

        self.refresh_auth_components()
        self.show_all()

    def refresh_auth_components(self):
        self.user_label.set_sensitive(self.auth)
        self.user_text.set_editable(self.auth)
        self.user_text.set_sensitive(self.auth)
        self.passwd_label.set_sensitive(self.auth)
        self.passwd_text.set_editable(self.auth)
        self.passwd_text.set_sensitive(self.auth)

    def auth_checkbox_toggled_cb(self, button):
        self.auth = self.auth_checkbox.get_active()
        self.refresh_auth_components()

    def response_cb(self, dialog, response_id):
        if response_id == gtk.RESPONSE_OK:
            if self.auth:
                self.user = self.user_text.get_text()
                self.passwd = self.passwd_text.get_text()
            else:
                self.user = None
                self.passwd = None
