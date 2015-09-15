#!/usr/bin/env python
#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2012        Intel Corporation
#
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
from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.hobwidget import hwc

#
# HobPage: the super class for all Hob-related pages
#    
class HobPage (gtk.VBox):

    def __init__(self, builder, title = None):
        super(HobPage, self).__init__(False, 0)
        self.builder = builder
        self.builder_width, self.builder_height = self.builder.size_request()

        if not title:
            self.title = "Hob -- Image Creator"
        else:
            self.title = title
        self.title_label = gtk.Label()

        self.box_group_area = gtk.VBox(False, 12)
        self.box_group_area.set_size_request(self.builder_width - 73 - 73, self.builder_height - 88 - 15 - 15)
        self.group_align = gtk.Alignment(xalign = 0, yalign=0.5, xscale=1, yscale=1)
        self.group_align.set_padding(15, 15, 73, 73)
        self.group_align.add(self.box_group_area)
        self.box_group_area.set_homogeneous(False)

    def set_title(self, title):
        self.title = title
        self.title_label.set_markup("<span size='x-large'>%s</span>" % self.title)

    def add_onto_top_bar(self, widget = None, padding = 0):
        # the top button occupies 1/7 of the page height
        # setup an event box
        eventbox = gtk.EventBox()
        style = eventbox.get_style().copy()
        style.bg[gtk.STATE_NORMAL] = eventbox.get_colormap().alloc_color(HobColors.LIGHT_GRAY, False, False)
        eventbox.set_style(style)
        eventbox.set_size_request(-1, 88)

        hbox = gtk.HBox()

        self.title_label = gtk.Label()
        self.title_label.set_markup("<span size='x-large'>%s</span>" % self.title)
        hbox.pack_start(self.title_label, expand=False, fill=False, padding=20)

        if widget:
            # add the widget in the event box
            hbox.pack_end(widget, expand=False, fill=False, padding=padding)
        eventbox.add(hbox)

        return eventbox

    def span_tag(self, size="medium", weight="normal", forground="#1c1c1c"):
        span_tag = "weight='%s' foreground='%s' size='%s'" % (weight, forground, size)
        return span_tag

    def append_toolbar_button(self, toolbar, buttonname, icon_disp, icon_hovor, tip, cb):
        # Create a button and append it on the toolbar according to button name
        icon = gtk.Image()
        icon_display = icon_disp
        icon_hover = icon_hovor
        pix_buffer = gtk.gdk.pixbuf_new_from_file(icon_display)
        icon.set_from_pixbuf(pix_buffer)
        tip_text = tip
        button = toolbar.append_item(buttonname, tip, None, icon, cb)
        return button

    @staticmethod
    def _size_to_string(size):
        try:
            if not size:
                size_str = "0 B"
            else:
                if len(str(int(size))) > 6:
                    size_str = '%.1f' % (size*1.0/(1024*1024)) + ' MB'
                elif len(str(int(size))) > 3:
                    size_str = '%.1f' % (size*1.0/1024) + ' KB'
                else:
                    size_str = str(size) + ' B'
        except:
            size_str = "0 B"
        return size_str

    @staticmethod
    def _string_to_size(str_size):
        try:
            if not str_size:
                size = 0
            else:
                unit = str_size.split()
                if len(unit) > 1:
                    if unit[1] == 'MB':
                        size = float(unit[0])*1024*1024
                    elif unit[1] == 'KB':
                        size = float(unit[0])*1024
                    elif unit[1] == 'B':
                        size = float(unit[0])
                    else:
                        size = 0
                else:
                    size = float(unit[0])
        except:
            size = 0
        return size

