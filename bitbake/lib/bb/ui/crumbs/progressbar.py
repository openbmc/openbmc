# BitBake Graphical GTK User Interface
#
# Copyright (C) 2011        Intel Corporation
#
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
from bb.ui.crumbs.hobcolor import HobColors

class HobProgressBar (gtk.ProgressBar):
    def __init__(self):
        gtk.ProgressBar.__init__(self)
        self.set_rcstyle(True)
        self.percentage = 0

    def set_rcstyle(self, status):
        rcstyle = gtk.RcStyle()
        rcstyle.fg[2] = gtk.gdk.Color(HobColors.BLACK)
        if status == "stop":
            rcstyle.bg[3] = gtk.gdk.Color(HobColors.WARNING)
        elif status == "fail":
            rcstyle.bg[3] = gtk.gdk.Color(HobColors.ERROR)
        else:
            rcstyle.bg[3] = gtk.gdk.Color(HobColors.RUNNING)
        self.modify_style(rcstyle)

    def set_title(self, text=None):
        if not text:
            text = ""
        text += " %.0f%%" % self.percentage
        self.set_text(text)

    def set_stop_title(self, text=None):
	if not text:
	    text = ""
	self.set_text(text)

    def reset(self):
        self.set_fraction(0)
        self.set_text("")
        self.set_rcstyle(True)
        self.percentage = 0

    def update(self, fraction):
        self.percentage = int(fraction * 100)
        self.set_fraction(fraction)
