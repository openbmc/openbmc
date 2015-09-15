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

import glib
import gtk
from bb.ui.crumbs.hobwidget import HobIconChecker
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class CrumbsMessageDialog(gtk.MessageDialog):
    """
    A GNOME HIG compliant dialog widget.
    Add buttons with gtk.Dialog.add_button or gtk.Dialog.add_buttons
    """
    def __init__(self, parent = None, label="", dialog_type = gtk.MESSAGE_QUESTION, msg=""):
        super(CrumbsMessageDialog, self).__init__(None,
                                                  gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT,
                                                  dialog_type,
                                                  gtk.BUTTONS_NONE,
                                                  None)

        self.set_skip_taskbar_hint(False)

        self.set_markup(label)

        if 0 <= len(msg) < 300:
            self.format_secondary_markup(msg)
        else:
            vbox = self.get_message_area()
            vbox.set_border_width(1)
            vbox.set_property("spacing", 12)
            self.textWindow = gtk.ScrolledWindow()
            self.textWindow.set_shadow_type(gtk.SHADOW_IN)
            self.textWindow.set_policy(gtk.POLICY_AUTOMATIC, gtk.POLICY_AUTOMATIC)
            self.msgView = gtk.TextView()
            self.msgView.set_editable(False)
            self.msgView.set_wrap_mode(gtk.WRAP_WORD)
            self.msgView.set_cursor_visible(False)
            self.msgView.set_size_request(300, 300)
            self.buf = gtk.TextBuffer()
            self.buf.set_text(msg)
            self.msgView.set_buffer(self.buf)
            self.textWindow.add(self.msgView)
            self.msgView.show()
            vbox.add(self.textWindow)
            self.textWindow.show()
