#!/usr/bin/env python
#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2012        Intel Corporation
#
# Authored by Bogdan Marinescu <bogdan.a.marinescu@intel.com>
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

import gtk, gobject
from bb.ui.crumbs.progressbar import HobProgressBar
from bb.ui.crumbs.hobwidget import hic
from bb.ui.crumbs.hobpages import HobPage

#
# SanityCheckPage
#
class SanityCheckPage (HobPage):

    def __init__(self, builder):
        super(SanityCheckPage, self).__init__(builder)
        self.running = False
        self.create_visual_elements()
        self.show_all()

    def make_label(self, text, bold=True):
        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        mark = "<span %s>%s</span>" % (self.span_tag('x-large', 'bold') if bold else self.span_tag('medium'), text)
        label.set_markup(mark)
        return label

    def start(self):
        if not self.running:
          self.running = True
          gobject.timeout_add(100, self.timer_func)

    def stop(self):
        self.running = False

    def is_running(self):
        return self.running

    def timer_func(self):
        self.progress_bar.pulse()
        return self.running

    def create_visual_elements(self):
        # Table'd layout. 'rows' and 'cols' give the table size
        rows, cols = 30, 50
        self.table = gtk.Table(rows, cols, True)
        self.pack_start(self.table, expand=False, fill=False)
        sx, sy = 2, 2
        # 'info' icon
        image = gtk.Image()
        image.set_from_file(hic.ICON_INFO_DISPLAY_FILE)
        self.table.attach(image, sx, sx + 2, sy, sy + 3 )
        image.show()
        # 'Checking' message
        label = self.make_label('Hob is checking for correct build system setup')
        self.table.attach(label, sx + 2, cols, sy, sy + 3, xpadding=5 )
        label.show()
        # 'Shouldn't take long' message.
        label = self.make_label("The check shouldn't take long.", False)
        self.table.attach(label, sx + 2, cols, sy + 3, sy + 4, xpadding=5)
        label.show()
        # Progress bar
        self.progress_bar = HobProgressBar()
        self.table.attach(self.progress_bar, sx + 2, cols - 3, sy + 5, sy + 7, xpadding=5)
        self.progress_bar.show()
        # All done
        self.table.show()

