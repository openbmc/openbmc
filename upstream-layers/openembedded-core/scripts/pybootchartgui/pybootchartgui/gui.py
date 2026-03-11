#  This file is part of pybootchartgui.

#  pybootchartgui is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.

#  pybootchartgui is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.

#  You should have received a copy of the GNU General Public License
#  along with pybootchartgui. If not, see <http://www.gnu.org/licenses/>.

import gi
gi.require_version('Gtk', '3.0') 
from gi.repository import Gtk as gtk
from gi.repository import Gtk
from gi.repository import Gdk
from gi.repository import GObject as gobject
from gi.repository import GObject

from . import draw
from .draw import RenderOptions

class PyBootchartWidget(gtk.DrawingArea, gtk.Scrollable):
    __gsignals__ = {
            'clicked' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE, (gobject.TYPE_STRING, Gdk.Event)),
            'position-changed' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE, (gobject.TYPE_INT, gobject.TYPE_INT)),
            'set-scroll-adjustments' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE, (gtk.Adjustment, gtk.Adjustment))
    }

    hadjustment = GObject.property(type=Gtk.Adjustment,
                                   default=Gtk.Adjustment(),
                                   flags=GObject.PARAM_READWRITE)
    hscroll_policy = GObject.property(type=Gtk.ScrollablePolicy,
                                      default=Gtk.ScrollablePolicy.MINIMUM,
                                      flags=GObject.PARAM_READWRITE)
    vadjustment = GObject.property(type=Gtk.Adjustment,
                                   default=Gtk.Adjustment(),
                                   flags=GObject.PARAM_READWRITE)
    vscroll_policy = GObject.property(type=Gtk.ScrollablePolicy,
                                      default=Gtk.ScrollablePolicy.MINIMUM,
                                      flags=GObject.PARAM_READWRITE)

    def __init__(self, trace, options, xscale):
        gtk.DrawingArea.__init__(self)

        self.trace = trace
        self.options = options

        self.set_can_focus(True)

        self.add_events(Gdk.EventMask.BUTTON_PRESS_MASK | Gdk.EventMask.BUTTON_RELEASE_MASK)
        self.connect("button-press-event", self.on_area_button_press)
        self.connect("button-release-event", self.on_area_button_release)
        self.add_events(Gdk.EventMask.POINTER_MOTION_MASK | Gdk.EventMask.POINTER_MOTION_HINT_MASK | Gdk.EventMask.BUTTON_RELEASE_MASK)
        self.connect("motion-notify-event", self.on_area_motion_notify)
        self.connect("scroll-event", self.on_area_scroll_event)
        self.connect('key-press-event', self.on_key_press_event)

        self.connect("size-allocate", self.on_allocation_size_changed)
        self.connect("position-changed", self.on_position_changed)

        self.connect("draw", self.on_draw)

        self.zoom_ratio = 1.0
        self.xscale = xscale
        self.x, self.y = 0.0, 0.0

        self.chart_width, self.chart_height = draw.extents(self.options, self.xscale, self.trace)
        self.our_width, self.our_height = self.chart_width, self.chart_height

        self.hadj = gtk.Adjustment(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        self.vadj = gtk.Adjustment(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        self.vadj.connect('value-changed', self.on_adjustments_changed)
        self.hadj.connect('value-changed', self.on_adjustments_changed)

    def bound_vals(self):
        self.x = max(0, self.x)
        self.y = max(0, self.y)
        self.x = min(self.chart_width - self.our_width, self.x)
        self.y = min(self.chart_height - self.our_height, self.y)

    def on_draw(self, darea, cr):
        # set a clip region
        #cr.rectangle(
        #        self.x, self.y,
        #        self.chart_width, self.chart_height
        #)
        #cr.clip()
        cr.set_source_rgba(1.0, 1.0, 1.0, 1.0)
        cr.paint()
        cr.scale(self.zoom_ratio, self.zoom_ratio)
        cr.translate(-self.x, -self.y)
        draw.render(cr, self.options, self.xscale, self.trace)

    def position_changed(self):
        self.emit("position-changed", self.x, self.y)

    ZOOM_INCREMENT = 1.25

    def zoom_image (self, zoom_ratio):
        self.zoom_ratio = zoom_ratio
        self._set_scroll_adjustments()
        self.queue_draw()

    def zoom_to_rect (self, rect):
        zoom_ratio = float(rect.width)/float(self.chart_width)
        self.zoom_image(zoom_ratio)
        self.x = 0
        self.position_changed()

    def set_xscale(self, xscale):
        old_mid_x = self.x + self.hadj.page_size / 2
        self.xscale = xscale
        self.chart_width, self.chart_height = draw.extents(self.options, self.xscale, self.trace)
        new_x = old_mid_x
        self.zoom_image (self.zoom_ratio)

    def on_expand(self, action):
        self.set_xscale (int(self.xscale * 1.5 + 0.5))

    def on_contract(self, action):
        self.set_xscale (max(int(self.xscale / 1.5), 1))

    def on_zoom_in(self, action):
        self.zoom_image(self.zoom_ratio * self.ZOOM_INCREMENT)

    def on_zoom_out(self, action):
        self.zoom_image(self.zoom_ratio / self.ZOOM_INCREMENT)

    def on_zoom_fit(self, action):
        self.zoom_to_rect(self.get_allocation())

    def on_zoom_100(self, action):
        self.zoom_image(1.0)
        self.set_xscale(1.0)

    def show_toggled(self, button):
        self.options.app_options.show_all = button.get_property ('active')
        self.chart_width, self.chart_height = draw.extents(self.options, self.xscale, self.trace)
        self._set_scroll_adjustments()
        self.queue_draw()

    POS_INCREMENT = 100

    def on_key_press_event(self, widget, event):
        if event.keyval == Gdk.keyval_from_name("Left"):
            self.x -= self.POS_INCREMENT/self.zoom_ratio
        elif event.keyval == Gdk.keyval_from_name("Right"):
            self.x += self.POS_INCREMENT/self.zoom_ratio
        elif event.keyval == Gdk.keyval_from_name("Up"):
            self.y -= self.POS_INCREMENT/self.zoom_ratio
        elif event.keyval == Gdk.keyval_from_name("Down"):
            self.y += self.POS_INCREMENT/self.zoom_ratio
        else:
            return False
        self.bound_vals()
        self.queue_draw()
        self.position_changed()
        return True

    def on_area_button_press(self, area, event):
        if event.button == 2 or event.button == 1:
            window = self.get_window()
            window.set_cursor(Gdk.Cursor(Gdk.CursorType.FLEUR))
            self.prevmousex = event.x
            self.prevmousey = event.y
        if event.type not in (Gdk.EventType.BUTTON_PRESS, Gdk.EventType.BUTTON_RELEASE):
            return False
        return False

    def on_area_button_release(self, area, event):
        if event.button == 2 or event.button == 1:
            window = self.get_window()
            window.set_cursor(Gdk.Cursor(Gdk.CursorType.ARROW))
            self.prevmousex = None
            self.prevmousey = None
            return True
        return False

    def on_area_scroll_event(self, area, event):
        if event.state & Gdk.CONTROL_MASK:
            if event.direction == Gdk.SCROLL_UP:
                self.zoom_image(self.zoom_ratio * self.ZOOM_INCREMENT)
                return True
            if event.direction == Gdk.SCROLL_DOWN:
                self.zoom_image(self.zoom_ratio / self.ZOOM_INCREMENT)
                return True
            return False

    def on_area_motion_notify(self, area, event):
        state = event.state
        if state & Gdk.ModifierType.BUTTON2_MASK or state & Gdk.ModifierType.BUTTON1_MASK:
            x, y = int(event.x), int(event.y)
            # pan the image
            self.x += (self.prevmousex - x)/self.zoom_ratio
            self.y += (self.prevmousey - y)/self.zoom_ratio
            self.bound_vals()
            self.queue_draw()
            self.prevmousex = x
            self.prevmousey = y
            self.position_changed()
        return True

    def on_allocation_size_changed(self, widget, allocation):
        self.hadj.page_size = allocation.width
        self.hadj.page_increment = allocation.width * 0.9
        self.vadj.page_size = allocation.height
        self.vadj.page_increment = allocation.height * 0.9
        self.our_width = allocation.width
        if self.chart_width < self.our_width:
            self.our_width = self.chart_width
        self.our_height = allocation.height
        if self.chart_height < self.our_height:
            self.our_height = self.chart_height
        self._set_scroll_adjustments()

    def _set_adj_upper(self, adj, upper):

        if adj.get_upper() != upper:
            adj.set_upper(upper)

    def _set_scroll_adjustments(self):
        self._set_adj_upper (self.hadj, self.zoom_ratio * (self.chart_width - self.our_width))
        self._set_adj_upper (self.vadj, self.zoom_ratio * (self.chart_height - self.our_height))

    def on_adjustments_changed(self, adj):
        self.x = self.hadj.get_value() / self.zoom_ratio
        self.y = self.vadj.get_value() / self.zoom_ratio
        self.queue_draw()

    def on_position_changed(self, widget, x, y):
        self.hadj.set_value(x * self.zoom_ratio)
        #self.hadj.value_changed()
        self.vadj.set_value(y * self.zoom_ratio)

class PyBootchartShell(gtk.VBox):
    ui = '''
    <ui>
            <toolbar name="ToolBar">
                    <toolitem action="Expand"/>
                    <toolitem action="Contract"/>
                    <separator/>
                    <toolitem action="ZoomIn"/>
                    <toolitem action="ZoomOut"/>
                    <toolitem action="ZoomFit"/>
                    <toolitem action="Zoom100"/>
            </toolbar>
    </ui>
    '''
    def __init__(self, window, trace, options, xscale):
        gtk.VBox.__init__(self)

        self.widget2 = PyBootchartWidget(trace, options, xscale)

        # Create a UIManager instance
        uimanager = self.uimanager = gtk.UIManager()

        # Add the accelerator group to the toplevel window
        accelgroup = uimanager.get_accel_group()
        window.add_accel_group(accelgroup)

        # Create an ActionGroup
        actiongroup = gtk.ActionGroup('Actions')
        self.actiongroup = actiongroup

        # Create actions
        actiongroup.add_actions((
                ('Expand', gtk.STOCK_ADD, None, None, None, self.widget2.on_expand),
                ('Contract', gtk.STOCK_REMOVE, None, None, None, self.widget2.on_contract),
                ('ZoomIn', gtk.STOCK_ZOOM_IN, None, None, None, self.widget2.on_zoom_in),
                ('ZoomOut', gtk.STOCK_ZOOM_OUT, None, None, None, self.widget2.on_zoom_out),
                ('ZoomFit', gtk.STOCK_ZOOM_FIT, 'Fit Width', None, None, self.widget2.on_zoom_fit),
                ('Zoom100', gtk.STOCK_ZOOM_100, None, None, None, self.widget2.on_zoom_100),
        ))

        # Add the actiongroup to the uimanager
        uimanager.insert_action_group(actiongroup, 0)

        # Add a UI description
        uimanager.add_ui_from_string(self.ui)

        # Scrolled window
        scrolled = gtk.ScrolledWindow(self.widget2.hadj, self.widget2.vadj)
        scrolled.add(self.widget2)

        #scrolled.set_hadjustment()
        #scrolled.set_vadjustment(self.widget2.vadj)
        scrolled.set_policy(gtk.PolicyType.ALWAYS, gtk.PolicyType.ALWAYS)

        # toolbar / h-box
        hbox = gtk.HBox(False, 8)

        # Create a Toolbar
        toolbar = uimanager.get_widget('/ToolBar')
        hbox.pack_start(toolbar, True, True, 0)

        if not options.kernel_only:
            # Misc. options
            button = gtk.CheckButton("Show more")
            button.connect ('toggled', self.widget2.show_toggled)
            button.set_active(options.app_options.show_all)
            hbox.pack_start (button, False, True, 0)

        self.pack_start(hbox, False, True, 0)
        self.pack_start(scrolled, True, True, 0)
        self.show_all()

    def grab_focus(self, window):
        window.set_focus(self.widget2)


class PyBootchartWindow(gtk.Window):

    def __init__(self, trace, app_options):
        gtk.Window.__init__(self)

        window = self
        window.set_title("Bootchart %s" % trace.filename)
        window.set_default_size(750, 550)

        tab_page = gtk.Notebook()
        tab_page.show()
        window.add(tab_page)

        full_opts = RenderOptions(app_options)
        full_tree = PyBootchartShell(window, trace, full_opts, 1.0)
        tab_page.append_page (full_tree, gtk.Label("Full tree"))

        if trace.kernel is not None and len (trace.kernel) > 2:
            kernel_opts = RenderOptions(app_options)
            kernel_opts.cumulative = False
            kernel_opts.charts = False
            kernel_opts.kernel_only = True
            kernel_tree = PyBootchartShell(window, trace, kernel_opts, 5.0)
            tab_page.append_page (kernel_tree, gtk.Label("Kernel boot"))

        full_tree.grab_focus(self)
        self.show()


def show(trace, options):
    win = PyBootchartWindow(trace, options)
    win.connect('destroy', gtk.main_quit)
    gtk.main()
