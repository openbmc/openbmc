#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2012   Intel Corporation
#
# Authored by Joshua Lock <josh@linux.intel.com>
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

import gobject
import gtk
try:
    import gconf
except:
    pass

class PersistentTooltip(gtk.Window):
    """
    A tooltip which persists once shown until the user dismisses it with the Esc
    key or by clicking the close button.

    # FIXME: the PersistentTooltip should be disabled when the user clicks anywhere off
    # it. We can't do this with focus-out-event becuase modal ensures we have focus?

    markup: some Pango text markup to display in the tooltip
    """
    def __init__(self, markup, parent_win=None):
        gtk.Window.__init__(self, gtk.WINDOW_POPUP)

        # Inherit the system theme for a tooltip
        style = gtk.rc_get_style_by_paths(gtk.settings_get_default(),
                        'gtk-tooltip', 'gtk-tooltip', gobject.TYPE_NONE)
        self.set_style(style)

        # The placement of the close button on the tip should reflect how the
        # window manager of the users system places close buttons. Try to read
        # the metacity gconf key to determine whether the close button is on the
        # left or the right.
        # In the case that we can't determine the users configuration we default
        # to close buttons being on the right.
        __button_right = True
        try:
            client = gconf.client_get_default()
            order = client.get_string("/apps/metacity/general/button_layout")
            if order and order.endswith(":"):
                __button_right = False
        except NameError:
            pass

        # We need to ensure we're only shown once
        self.shown = False

        # We don't want any WM decorations
        self.set_decorated(False)
        # We don't want to show in the taskbar or window switcher
        self.set_skip_pager_hint(True)
        self.set_skip_taskbar_hint(True)
        # We must be modal to ensure we grab focus when presented from a gtk.Dialog
        self.set_modal(True)

        self.set_border_width(0)
        self.set_position(gtk.WIN_POS_MOUSE)
        self.set_opacity(0.95)

        # Ensure a reasonable minimum size
        self.set_geometry_hints(self, 100, 50)

        # Set this window as a transient window for parent(main window)
        if parent_win:
            self.set_transient_for(parent_win)
            self.set_destroy_with_parent(True)
        # Draw our label and close buttons
        hbox = gtk.HBox(False, 0)
        hbox.show()
        self.add(hbox)

        img = gtk.Image()
        img.set_from_stock(gtk.STOCK_CLOSE, gtk.ICON_SIZE_BUTTON)

        self.button = gtk.Button()
        self.button.set_image(img)
        self.button.connect("clicked", self._dismiss_cb)
        self.button.set_flags(gtk.CAN_DEFAULT)
        self.button.grab_focus()
        self.button.show()
        vbox = gtk.VBox(False, 0)
        vbox.show()
        vbox.pack_start(self.button, False, False, 0)
        if __button_right:
            hbox.pack_end(vbox, True, True, 0)
        else:
            hbox.pack_start(vbox, True, True, 0)

        self.set_default(self.button)

        bin = gtk.HBox(True, 6)
        bin.set_border_width(6)
        bin.show()
        self.label = gtk.Label()
        self.label.set_line_wrap(True)
        # We want to match the colours of the normal tooltips, as dictated by
        # the users gtk+-2.0 theme, wherever possible - on some systems this
        # requires explicitly setting a fg_color for the label which matches the
        # tooltip_fg_color
        settings = gtk.settings_get_default()
        colours = settings.get_property('gtk-color-scheme').split('\n')
        # remove any empty lines, there's likely to be a trailing one after
        # calling split on a dictionary-like string
        colours = filter(None, colours)
        for col in colours:
            item, val = col.split(': ')
            if item == 'tooltip_fg_color':
                style = self.label.get_style()
                style.fg[gtk.STATE_NORMAL] = gtk.gdk.color_parse(val)
                self.label.set_style(style)
                break # we only care for the tooltip_fg_color

        self.label.set_markup(markup)
        self.label.show()
        bin.add(self.label)
        hbox.pack_end(bin, True, True, 6)

        # add the original URL display for user reference
        if 'a href' in markup:
            hbox.set_tooltip_text(self.get_markup_url(markup))
        hbox.show()

        self.connect("key-press-event", self._catch_esc_cb)

    """
    Callback when the PersistentTooltip's close button is clicked.
    Hides the PersistentTooltip.
    """
    def _dismiss_cb(self, button):
        self.hide()
        return True

    """
    Callback when the Esc key is detected. Hides the PersistentTooltip.
    """
    def _catch_esc_cb(self, widget, event):
        keyname = gtk.gdk.keyval_name(event.keyval)
        if keyname == "Escape":
            self.hide()
        return True

    """
    Called to present the PersistentTooltip.
    Overrides the superclasses show() method to include state tracking.
    """
    def show(self):
        if not self.shown:
            self.shown = True
            gtk.Window.show(self)

    """
    Called to hide the PersistentTooltip.
    Overrides the superclasses hide() method to include state tracking.
    """
    def hide(self):
        self.shown = False
        gtk.Window.hide(self)

    """
    Called to get the hyperlink URL from markup text.
    """
    def get_markup_url(self, markup):
        url = "http:"
        if markup and type(markup) == str:
            s = markup
            if 'http:' in s:
                import re
                url = re.search('(http:[^,\\ "]+)', s).group(0)

        return url
