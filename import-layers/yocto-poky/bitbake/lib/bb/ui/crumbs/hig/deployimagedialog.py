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

import glob
import gtk
import gobject
import os
import re
import shlex
import subprocess
import tempfile
from bb.ui.crumbs.hobwidget import hic, HobButton
from bb.ui.crumbs.progressbar import HobProgressBar
import bb.ui.crumbs.utils
import bb.process
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.crumbsmessagedialog import CrumbsMessageDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class DeployImageDialog (CrumbsDialog):

    __dummy_usb__ = "--select a usb drive--"

    def __init__(self, title, image_path, parent, flags, buttons=None, standalone=False):
        super(DeployImageDialog, self).__init__(title, parent, flags, buttons)

        self.image_path = image_path
        self.standalone = standalone

        self.create_visual_elements()
        self.connect("response", self.response_cb)

    def create_visual_elements(self):
        self.set_size_request(600, 400)
        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        markup = "<span font_desc='12'>The image to be written into usb drive:</span>"
        label.set_markup(markup)
        self.vbox.pack_start(label, expand=False, fill=False, padding=2)

        table = gtk.Table(2, 10, False)
        table.set_col_spacings(5)
        table.set_row_spacings(5)
        self.vbox.pack_start(table, expand=True, fill=True)

        scroll = gtk.ScrolledWindow()
        scroll.set_policy(gtk.POLICY_NEVER, gtk.POLICY_AUTOMATIC)
        scroll.set_shadow_type(gtk.SHADOW_IN)
        tv = gtk.TextView()
        tv.set_editable(False)
        tv.set_wrap_mode(gtk.WRAP_WORD)
        tv.set_cursor_visible(False)
        self.buf = gtk.TextBuffer()
        self.buf.set_text(self.image_path)
        tv.set_buffer(self.buf)
        scroll.add(tv)
        table.attach(scroll, 0, 10, 0, 1)

        # There are 2 ways to use DeployImageDialog
        # One way is that called by HOB when the 'Deploy Image' button is clicked
        # The other way is that called by a standalone script.
        # Following block of codes handles the latter way. It adds a 'Select Image' button and
        # emit a signal when the button is clicked.
        if self.standalone:
                gobject.signal_new("select_image_clicked", self, gobject.SIGNAL_RUN_FIRST,
                                   gobject.TYPE_NONE, ())
                icon = gtk.Image()
                pix_buffer = gtk.gdk.pixbuf_new_from_file(hic.ICON_IMAGES_DISPLAY_FILE)
                icon.set_from_pixbuf(pix_buffer)
                button = gtk.Button("Select Image")
                button.set_image(icon)
                #button.set_size_request(140, 50)
                table.attach(button, 9, 10, 1, 2, gtk.FILL, 0, 0, 0)
                button.connect("clicked", self.select_image_button_clicked_cb)

        separator = gtk.HSeparator()
        self.vbox.pack_start(separator, expand=False, fill=False, padding=10)

        self.usb_desc = gtk.Label()
        self.usb_desc.set_alignment(0.0, 0.5)
        markup = "<span font_desc='12'>You haven't chosen any USB drive.</span>"
        self.usb_desc.set_markup(markup)

        self.usb_combo = gtk.combo_box_new_text()
        self.usb_combo.connect("changed", self.usb_combo_changed_cb)
        model = self.usb_combo.get_model()
        model.clear()
        self.usb_combo.append_text(self.__dummy_usb__)
        for usb in self.find_all_usb_devices():
            self.usb_combo.append_text("/dev/" + usb)
        self.usb_combo.set_active(0)
        self.vbox.pack_start(self.usb_combo, expand=False, fill=False)
        self.vbox.pack_start(self.usb_desc, expand=False, fill=False, padding=2)

        self.progress_bar = HobProgressBar()
        self.vbox.pack_start(self.progress_bar, expand=False, fill=False)
        separator = gtk.HSeparator()
        self.vbox.pack_start(separator, expand=False, fill=True, padding=10)

        self.vbox.show_all()
        self.progress_bar.hide()

    def set_image_text_buffer(self, image_path):
        self.buf.set_text(image_path)

    def set_image_path(self, image_path):
        self.image_path = image_path

    def popen_read(self, cmd):
        tmpout, errors = bb.process.run("%s" % cmd)
        return tmpout.strip()

    def find_all_usb_devices(self):
        usb_devs = [ os.readlink(u)
            for u in glob.glob('/dev/disk/by-id/usb*')
            if not re.search(r'part\d+', u) ]
        return [ '%s' % u[u.rfind('/')+1:] for u in usb_devs ]

    def get_usb_info(self, dev):
        return "%s %s" % \
            (self.popen_read('cat /sys/class/block/%s/device/vendor' % dev),
            self.popen_read('cat /sys/class/block/%s/device/model' % dev))

    def select_image_button_clicked_cb(self, button):
            self.emit('select_image_clicked')

    def usb_combo_changed_cb(self, usb_combo):
        combo_item = self.usb_combo.get_active_text()
        if not combo_item or combo_item == self.__dummy_usb__:
            markup = "<span font_desc='12'>You haven't chosen any USB drive.</span>"
            self.usb_desc.set_markup(markup)
        else:
            markup = "<span font_desc='12'>" + self.get_usb_info(combo_item.lstrip("/dev/")) + "</span>"
            self.usb_desc.set_markup(markup)

    def response_cb(self, dialog, response_id):
        if response_id == gtk.RESPONSE_YES:
            lbl = ''
            msg = ''
            combo_item = self.usb_combo.get_active_text()
            if combo_item and combo_item != self.__dummy_usb__ and self.image_path:
                cmdline = bb.ui.crumbs.utils.which_terminal()
                if cmdline:
                    tmpfile = tempfile.NamedTemporaryFile()
                    cmdline += "\"sudo dd if=" + self.image_path + \
                                " of=" + combo_item + " && sync; echo $? > " + tmpfile.name + "\""
                    subprocess.call(shlex.split(cmdline))

                    if int(tmpfile.readline().strip()) == 0:
                        lbl = "<b>Deploy image successfully.</b>"
                    else:
                        lbl = "<b>Failed to deploy image.</b>"
                        msg = "Please check image <b>%s</b> exists and USB device <b>%s</b> is writable." % (self.image_path, combo_item)
                    tmpfile.close()
            else:
                if not self.image_path:
                    lbl = "<b>No selection made.</b>"
                    msg = "You have not selected an image to deploy."
                else:
                    lbl = "<b>No selection made.</b>"
                    msg = "You have not selected a USB device."
            if len(lbl):
                crumbs_dialog = CrumbsMessageDialog(self, lbl, gtk.MESSAGE_INFO, msg)
                button = crumbs_dialog.add_button("Close", gtk.RESPONSE_OK)
                HobButton.style_button(button)
                crumbs_dialog.run()
                crumbs_dialog.destroy()

    def update_progress_bar(self, title, fraction, status=None):
        self.progress_bar.update(fraction)
        self.progress_bar.set_title(title)
        self.progress_bar.set_rcstyle(status)

    def write_file(self, ifile, ofile):
        self.progress_bar.reset()
        self.progress_bar.show()

        f_from = os.open(ifile, os.O_RDONLY)
        f_to = os.open(ofile, os.O_WRONLY)

        total_size = os.stat(ifile).st_size
        written_size = 0

        while True:
            buf = os.read(f_from, 1024*1024)
            if not buf:
                break
            os.write(f_to, buf)
            written_size += 1024*1024
            self.update_progress_bar("Writing to usb:", written_size * 1.0/total_size)

        self.update_progress_bar("Writing completed:", 1.0)
        os.close(f_from)
        os.close(f_to)
        self.progress_bar.hide()
