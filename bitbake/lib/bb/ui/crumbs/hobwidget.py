# BitBake Graphical GTK User Interface
#
# Copyright (C) 2011-2012   Intel Corporation
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
import gobject
import os
import os.path
import sys
import pango, pangocairo
import cairo
import math

from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.persistenttooltip import PersistentTooltip

class hwc:

    MAIN_WIN_WIDTH   = 1024
    MAIN_WIN_HEIGHT  = 700

class hic:

    HOB_ICON_BASE_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ("ui/icons/"))

    ICON_RCIPE_DISPLAY_FILE       = os.path.join(HOB_ICON_BASE_DIR, ('recipe/recipe_display.png'))
    ICON_RCIPE_HOVER_FILE         = os.path.join(HOB_ICON_BASE_DIR, ('recipe/recipe_hover.png'))
    ICON_PACKAGES_DISPLAY_FILE    = os.path.join(HOB_ICON_BASE_DIR, ('packages/packages_display.png'))
    ICON_PACKAGES_HOVER_FILE      = os.path.join(HOB_ICON_BASE_DIR, ('packages/packages_hover.png'))
    ICON_LAYERS_DISPLAY_FILE      = os.path.join(HOB_ICON_BASE_DIR, ('layers/layers_display.png'))
    ICON_LAYERS_HOVER_FILE        = os.path.join(HOB_ICON_BASE_DIR, ('layers/layers_hover.png'))
    ICON_IMAGES_DISPLAY_FILE      = os.path.join(HOB_ICON_BASE_DIR, ('images/images_display.png'))
    ICON_IMAGES_HOVER_FILE        = os.path.join(HOB_ICON_BASE_DIR, ('images/images_hover.png'))
    ICON_SETTINGS_DISPLAY_FILE    = os.path.join(HOB_ICON_BASE_DIR, ('settings/settings_display.png'))
    ICON_SETTINGS_HOVER_FILE      = os.path.join(HOB_ICON_BASE_DIR, ('settings/settings_hover.png'))
    ICON_INFO_DISPLAY_FILE        = os.path.join(HOB_ICON_BASE_DIR, ('info/info_display.png'))
    ICON_INFO_HOVER_FILE          = os.path.join(HOB_ICON_BASE_DIR, ('info/info_hover.png'))
    ICON_INDI_CONFIRM_FILE        = os.path.join(HOB_ICON_BASE_DIR, ('indicators/confirmation.png'))
    ICON_INDI_ERROR_FILE          = os.path.join(HOB_ICON_BASE_DIR, ('indicators/denied.png'))
    ICON_INDI_REMOVE_FILE         = os.path.join(HOB_ICON_BASE_DIR, ('indicators/remove.png'))
    ICON_INDI_REMOVE_HOVER_FILE   = os.path.join(HOB_ICON_BASE_DIR, ('indicators/remove-hover.png'))
    ICON_INDI_ADD_FILE            = os.path.join(HOB_ICON_BASE_DIR, ('indicators/add.png'))
    ICON_INDI_ADD_HOVER_FILE      = os.path.join(HOB_ICON_BASE_DIR, ('indicators/add-hover.png'))
    ICON_INDI_REFRESH_FILE        = os.path.join(HOB_ICON_BASE_DIR, ('indicators/refresh.png'))
    ICON_INDI_ALERT_FILE          = os.path.join(HOB_ICON_BASE_DIR, ('indicators/alert.png'))
    ICON_INDI_TICK_FILE           = os.path.join(HOB_ICON_BASE_DIR, ('indicators/tick.png'))
    ICON_INDI_INFO_FILE           = os.path.join(HOB_ICON_BASE_DIR, ('indicators/info.png'))

class HobViewTable (gtk.VBox):
    """
    A VBox to contain the table for different recipe views and package view
    """
    __gsignals__ = {
         "toggled"       : (gobject.SIGNAL_RUN_LAST,
                            gobject.TYPE_NONE,
                           (gobject.TYPE_PYOBJECT,
                            gobject.TYPE_STRING,
                            gobject.TYPE_INT,
                            gobject.TYPE_PYOBJECT,)),
         "row-activated" : (gobject.SIGNAL_RUN_LAST,
                            gobject.TYPE_NONE,
                           (gobject.TYPE_PYOBJECT,
                            gobject.TYPE_PYOBJECT,)),
         "cell-fadeinout-stopped" : (gobject.SIGNAL_RUN_LAST,
                            gobject.TYPE_NONE,
                           (gobject.TYPE_PYOBJECT,
                            gobject.TYPE_PYOBJECT,
                            gobject.TYPE_PYOBJECT,)),
    }

    def __init__(self, columns, name):
        gtk.VBox.__init__(self, False, 6)
        self.table_tree = gtk.TreeView()
        self.table_tree.set_headers_visible(True)
        self.table_tree.set_headers_clickable(True)
        self.table_tree.set_rules_hint(True)
        self.table_tree.set_enable_tree_lines(True)
        self.table_tree.get_selection().set_mode(gtk.SELECTION_SINGLE)
        self.toggle_columns = []
        self.table_tree.connect("row-activated", self.row_activated_cb)
        self.top_bar = None
        self.tab_name = name

        for i, column in enumerate(columns):
            col_name = column['col_name']
            col = gtk.TreeViewColumn(col_name)
            col.set_clickable(True)
            col.set_resizable(True)
            if self.tab_name.startswith('Included'):
                if col_name!='Included':
                    col.set_sort_column_id(column['col_id'])
            else:
                col.set_sort_column_id(column['col_id'])
            if 'col_min' in column.keys():
                col.set_min_width(column['col_min'])
            if 'col_max' in column.keys():
                col.set_max_width(column['col_max'])
            if 'expand' in column.keys():
                col.set_expand(True)
            self.table_tree.append_column(col)

            if (not 'col_style' in column.keys()) or column['col_style'] == 'text':
                cell = gtk.CellRendererText()
                col.pack_start(cell, True)
                col.set_attributes(cell, text=column['col_id'])
                if 'col_t_id' in column.keys():
                    col.add_attribute(cell, 'font', column['col_t_id'])
            elif column['col_style'] == 'check toggle':
                cell = HobCellRendererToggle()
                cell.set_property('activatable', True)
                cell.connect("toggled", self.toggled_cb, i, self.table_tree)
                cell.connect_render_state_changed(self.stop_cell_fadeinout_cb, self.table_tree)
                self.toggle_id = i
                col.pack_end(cell, True)
                col.set_attributes(cell, active=column['col_id'])
                self.toggle_columns.append(col_name)
                if 'col_group' in column.keys():
                    col.set_cell_data_func(cell, self.set_group_number_cb)
            elif column['col_style'] == 'radio toggle':
                cell = gtk.CellRendererToggle()
                cell.set_property('activatable', True)
                cell.set_radio(True)
                cell.connect("toggled", self.toggled_cb, i, self.table_tree)
                self.toggle_id = i
                col.pack_end(cell, True)
                col.set_attributes(cell, active=column['col_id'])
                self.toggle_columns.append(col_name)
            elif column['col_style'] == 'binb':
                cell = gtk.CellRendererText()
                col.pack_start(cell, True)
                col.set_cell_data_func(cell, self.display_binb_cb, column['col_id'])
                if 'col_t_id' in column.keys():
                    col.add_attribute(cell, 'font', column['col_t_id'])

        self.scroll = gtk.ScrolledWindow()
        self.scroll.set_policy(gtk.POLICY_NEVER, gtk.POLICY_AUTOMATIC)
        self.scroll.add(self.table_tree)

        self.pack_end(self.scroll, True, True, 0)

    def add_no_result_bar(self, entry):
        color = HobColors.KHAKI
        self.top_bar = gtk.EventBox()
        self.top_bar.set_size_request(-1, 70)
        self.top_bar.modify_bg(gtk.STATE_NORMAL, gtk.gdk.color_parse(color))
        self.top_bar.set_flags(gtk.CAN_DEFAULT)
        self.top_bar.grab_default()

        no_result_tab = gtk.Table(5, 20, True)
        self.top_bar.add(no_result_tab)

        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        title = "No results matching your search"
        label.set_markup("<span size='x-large'><b>%s</b></span>" % title)
        no_result_tab.attach(label, 1, 14, 1, 4)

        clear_button = HobButton("Clear search")
        clear_button.set_tooltip_text("Clear search query")
        clear_button.connect('clicked', self.set_search_entry_clear_cb, entry)
        no_result_tab.attach(clear_button, 16, 19, 1, 4)

        self.pack_start(self.top_bar, False, True, 12)
        self.top_bar.show_all()

    def set_search_entry_clear_cb(self, button, search):
        if search.get_editable() == True:
            search.set_text("")
        search.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, False)
        search.grab_focus()

    def display_binb_cb(self, col, cell, model, it, col_id):
        binb =  model.get_value(it, col_id)
        # Just display the first item
        if binb:
            bin = binb.split(', ')
            total_no = len(bin)
            if total_no > 1 and bin[0] == "User Selected":
                if total_no > 2:
                    present_binb = bin[1] + ' (+' + str(total_no - 1) + ')'
                else:
                    present_binb = bin[1]
            else:
                if total_no > 1:
                    present_binb = bin[0] + ' (+' + str(total_no - 1) + ')'
                else:
                    present_binb = bin[0]
            cell.set_property('text', present_binb)
        else:
            cell.set_property('text', "")
        return True

    def set_model(self, tree_model):
        self.table_tree.set_model(tree_model)

    def toggle_default(self):
        model = self.table_tree.get_model()
        if not model:
            return
        iter = model.get_iter_first()
        if iter:
            rowpath = model.get_path(iter)
            model[rowpath][self.toggle_id] = True

    def toggled_cb(self, cell, path, columnid, tree):
        self.emit("toggled", cell, path, columnid, tree)

    def row_activated_cb(self, tree, path, view_column):
        if not view_column.get_title() in self.toggle_columns:
            self.emit("row-activated", tree.get_model(), path)

    def stop_cell_fadeinout_cb(self, ctrl, cell, tree):
        self.emit("cell-fadeinout-stopped", ctrl, cell, tree)

    def set_group_number_cb(self, col, cell, model, iter):
        if model and (model.iter_parent(iter) == None):
            cell.cell_attr["number_of_children"] = model.iter_n_children(iter)
        else:
            cell.cell_attr["number_of_children"] = 0

    def connect_group_selection(self, cb_func):
        self.table_tree.get_selection().connect("changed", cb_func)

"""
A method to calculate a softened value for the colour of widget when in the
provided state.

widget: the widget whose style to use
state: the state of the widget to use the style for

Returns a string value representing the softened colour
"""
def soften_color(widget, state=gtk.STATE_NORMAL):
    # this colour munging routine is heavily inspired bu gdu_util_get_mix_color()
    # from gnome-disk-utility:
    # http://git.gnome.org/browse/gnome-disk-utility/tree/src/gdu-gtk/gdu-gtk.c?h=gnome-3-0
    blend = 0.7
    style = widget.get_style()
    color = style.text[state]
    color.red = color.red * blend + style.base[state].red * (1.0 - blend)
    color.green = color.green * blend + style.base[state].green * (1.0 - blend)
    color.blue = color.blue * blend + style.base[state].blue * (1.0 - blend)
    return color.to_string()

class BaseHobButton(gtk.Button):
    """
    A gtk.Button subclass which follows the visual design of Hob for primary
    action buttons

    label: the text to display as the button's label
    """
    def __init__(self, label):
        gtk.Button.__init__(self, label)
        HobButton.style_button(self)

    @staticmethod
    def style_button(button):
        style = button.get_style()
        style = gtk.rc_get_style_by_paths(gtk.settings_get_default(), 'gtk-button', 'gtk-button', gobject.TYPE_NONE)

        button.set_flags(gtk.CAN_DEFAULT)
        button.grab_default()

#        label = "<span size='x-large'><b>%s</b></span>" % gobject.markup_escape_text(button.get_label())
        label = button.get_label()
        button.set_label(label)
        button.child.set_use_markup(True)

class HobButton(BaseHobButton):
    """
    A gtk.Button subclass which follows the visual design of Hob for primary
    action buttons

    label: the text to display as the button's label
    """
    def __init__(self, label):
        BaseHobButton.__init__(self, label)
        HobButton.style_button(self)

class HobAltButton(BaseHobButton):
    """
    A gtk.Button subclass which has no relief, and so is more discrete
    """
    def __init__(self, label):
        BaseHobButton.__init__(self, label)
        HobAltButton.style_button(self)

    """
    A callback for the state-changed event to ensure the text is displayed
    differently when the widget is not sensitive
    """
    @staticmethod
    def desensitise_on_state_change_cb(button, state):
        if not button.get_property("sensitive"):
            HobAltButton.set_text(button, False)
        else:
            HobAltButton.set_text(button, True)

    """
    Set the button label with an appropriate colour for the current widget state
    """
    @staticmethod
    def set_text(button, sensitive=True):
        if sensitive:
            colour = HobColors.PALE_BLUE
        else:
            colour = HobColors.LIGHT_GRAY
        button.set_label("<span size='large' color='%s'><b>%s</b></span>" % (colour, gobject.markup_escape_text(button.text)))
        button.child.set_use_markup(True)

class HobImageButton(gtk.Button):
    """
    A gtk.Button with an icon and two rows of text, the second of which is
    displayed in a blended colour.

    primary_text: the main button label
    secondary_text: optional second line of text
    icon_path: path to the icon file to display on the button
    """
    def __init__(self, primary_text, secondary_text="", icon_path="", hover_icon_path=""):
        gtk.Button.__init__(self)
        self.set_relief(gtk.RELIEF_NONE)

        self.icon_path = icon_path
        self.hover_icon_path = hover_icon_path

        hbox = gtk.HBox(False, 10)
        hbox.show()
        self.add(hbox)
        self.icon = gtk.Image()
        self.icon.set_from_file(self.icon_path)
        self.icon.set_alignment(0.5, 0.0)
        self.icon.show()
        if self.hover_icon_path and len(self.hover_icon_path):
            self.connect("enter-notify-event", self.set_hover_icon_cb)
            self.connect("leave-notify-event", self.set_icon_cb)
        hbox.pack_start(self.icon, False, False, 0)
        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        colour = soften_color(label)
        mark = "<span size='x-large'>%s</span>\n<span size='medium' fgcolor='%s' weight='ultralight'>%s</span>" % (primary_text, colour, secondary_text)
        label.set_markup(mark)
        label.show()
        hbox.pack_start(label, True, True, 0)

    def set_hover_icon_cb(self, widget, event):
        self.icon.set_from_file(self.hover_icon_path)

    def set_icon_cb(self, widget, event):
        self.icon.set_from_file(self.icon_path)

class HobInfoButton(gtk.EventBox):
    """
    This class implements a button-like widget per the Hob visual and UX designs
    which will display a persistent tooltip, with the contents of tip_markup, when
    clicked.

    tip_markup: the Pango Markup to be displayed in the persistent tooltip
    """
    def __init__(self, tip_markup, parent=None):
        gtk.EventBox.__init__(self)
        self.image = gtk.Image()
        self.image.set_from_file(
        hic.ICON_INFO_DISPLAY_FILE)
        self.image.show()
        self.add(self.image)
        self.tip_markup = tip_markup
        self.my_parent = parent

        self.set_events(gtk.gdk.BUTTON_RELEASE |
                        gtk.gdk.ENTER_NOTIFY_MASK |
                        gtk.gdk.LEAVE_NOTIFY_MASK)

        self.connect("button-release-event", self.button_release_cb)
        self.connect("enter-notify-event", self.mouse_in_cb)
        self.connect("leave-notify-event", self.mouse_out_cb)

    """
    When the mouse click is released emulate a button-click and show the associated
    PersistentTooltip
    """
    def button_release_cb(self, widget, event):
        from bb.ui.crumbs.hig.propertydialog import PropertyDialog
        self.dialog = PropertyDialog(title = '',
                    parent = self.my_parent,
                    information = self.tip_markup,
                    flags = gtk.DIALOG_DESTROY_WITH_PARENT
                        | gtk.DIALOG_NO_SEPARATOR)

        button = self.dialog.add_button("Close", gtk.RESPONSE_CANCEL)
        HobAltButton.style_button(button)
        button.connect("clicked", lambda w: self.dialog.destroy())
        self.dialog.show_all()
        self.dialog.run()

    """
    Change to the prelight image when the mouse enters the widget
    """
    def mouse_in_cb(self, widget, event):
        self.image.set_from_file(hic.ICON_INFO_HOVER_FILE)

    """
    Change to the stock image when the mouse enters the widget
    """
    def mouse_out_cb(self, widget, event):
        self.image.set_from_file(hic.ICON_INFO_DISPLAY_FILE)

class HobIndicator(gtk.DrawingArea):
    def __init__(self, count):
        gtk.DrawingArea.__init__(self)
        # Set no window for transparent background
        self.set_has_window(False)
        self.set_size_request(38,38)
        # We need to pass through button clicks
        self.add_events(gtk.gdk.BUTTON_PRESS_MASK | gtk.gdk.BUTTON_RELEASE_MASK)

        self.connect('expose-event', self.expose)

        self.count = count
        self.color = HobColors.GRAY

    def expose(self, widget, event):
        if self.count and self.count > 0:
            ctx = widget.window.cairo_create()

            x, y, w, h = self.allocation

            ctx.set_operator(cairo.OPERATOR_OVER)
            ctx.set_source_color(gtk.gdk.color_parse(self.color))
            ctx.translate(w/2, h/2)
            ctx.arc(x, y, min(w,h)/2 - 2, 0, 2*math.pi)
            ctx.fill_preserve()

            layout = self.create_pango_layout(str(self.count))
            textw, texth = layout.get_pixel_size()
            x = (w/2)-(textw/2) + x
            y = (h/2) - (texth/2) + y
            ctx.move_to(x, y)
            self.window.draw_layout(self.style.light_gc[gtk.STATE_NORMAL], int(x), int(y), layout)

    def set_count(self, count):
        self.count = count

    def set_active(self, active):
        if active:
            self.color = HobColors.DEEP_RED
        else:
            self.color = HobColors.GRAY

class HobTabLabel(gtk.HBox):
    def __init__(self, text, count=0):
        gtk.HBox.__init__(self, False, 0)
        self.indicator = HobIndicator(count)
        self.indicator.show()
        self.pack_end(self.indicator, False, False)
        self.lbl = gtk.Label(text)
        self.lbl.set_alignment(0.0, 0.5)
        self.lbl.show()
        self.pack_end(self.lbl, True, True, 6)

    def set_count(self, count):
        self.indicator.set_count(count)

    def set_active(self, active=True):
        self.indicator.set_active(active)

class HobNotebook(gtk.Notebook):
    def __init__(self):
        gtk.Notebook.__init__(self)
        self.set_property('homogeneous', True)

        self.pages = []

        self.search = None
        self.search_focus = False
        self.page_changed = False

        self.connect("switch-page", self.page_changed_cb)

        self.show_all()

    def page_changed_cb(self, nb, page, page_num):
        for p, lbl in enumerate(self.pages):
            if p == page_num:
                lbl.set_active()
            else:
                lbl.set_active(False)

        if self.search:
            self.page_changed = True
            self.reset_entry(self.search, page_num)

    def append_page(self, child, tab_label, tab_tooltip=None):
        label = HobTabLabel(tab_label)
        if tab_tooltip:
            label.set_tooltip_text(tab_tooltip)
        label.set_active(False)
        self.pages.append(label)
        gtk.Notebook.append_page(self, child, label)

    def set_entry(self, names, tips):
        self.search = gtk.Entry()
        self.search_names = names
        self.search_tips = tips
        style = self.search.get_style()
        style.text[gtk.STATE_NORMAL] = self.get_colormap().alloc_color(HobColors.GRAY, False, False)
        self.search.set_style(style)
        self.search.set_text(names[0])
        self.search.set_tooltip_text(self.search_tips[0])
        self.search.props.has_tooltip = True

        self.search.set_editable(False)
        self.search.set_icon_from_stock(gtk.ENTRY_ICON_SECONDARY, gtk.STOCK_CLEAR)
        self.search.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, False)
        self.search.connect("icon-release", self.set_search_entry_clear_cb)
        self.search.set_width_chars(30)
        self.search.show()

        self.search.connect("focus-in-event", self.set_search_entry_editable_cb)
        self.search.connect("focus-out-event", self.set_search_entry_reset_cb)
        self.set_action_widget(self.search, gtk.PACK_END)

    def show_indicator_icon(self, title, number):
        for child in self.pages:
            if child.lbl.get_label() == title:
                child.set_count(number)

    def hide_indicator_icon(self, title):
        for child in self.pages:
            if child.lbl.get_label() == title:
                child.set_count(0)

    def set_search_entry_editable_cb(self, search, event):
        self.search_focus = True
        search.set_editable(True)
        text = search.get_text()
        if text in self.search_names:
            search.set_text("")
        style = self.search.get_style()
        style.text[gtk.STATE_NORMAL] = self.get_colormap().alloc_color(HobColors.BLACK, False, False)
        search.set_style(style)

    def set_search_entry_reset_cb(self, search, event):
        page_num = self.get_current_page()
        text = search.get_text()
        if not text:
            self.reset_entry(search, page_num)

    def reset_entry(self, entry, page_num):
        style = entry.get_style()
        style.text[gtk.STATE_NORMAL] = self.get_colormap().alloc_color(HobColors.GRAY, False, False)
        entry.set_style(style)
        entry.set_text(self.search_names[page_num])
        entry.set_tooltip_text(self.search_tips[page_num])
        entry.set_editable(False)
        entry.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, False)

    def set_search_entry_clear_cb(self, search, icon_pos, event):
        if search.get_editable() == True:
            search.set_text("")
        search.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, False)
        search.grab_focus()

    def set_page(self, title):
        for child in self.pages:
            if child.lbl.get_label() == title:
                child.grab_focus()
                self.set_current_page(self.pages.index(child))
                return

class HobWarpCellRendererText(gtk.CellRendererText):
    def __init__(self, col_number):
        gtk.CellRendererText.__init__(self)
        self.set_property("wrap-mode", pango.WRAP_WORD_CHAR)
        self.set_property("wrap-width", 300) # default value wrap width is 300
        self.col_n = col_number

    def do_render(self, window, widget, background_area, cell_area, expose_area, flags):
        if widget:
            self.props.wrap_width = self.get_resized_wrap_width(widget, widget.get_column(self.col_n))
        return gtk.CellRendererText.do_render(self, window, widget, background_area, cell_area, expose_area, flags)

    def get_resized_wrap_width(self, treeview, column):
        otherCols = []
        for col in treeview.get_columns():
            if col != column:
                otherCols.append(col)
        adjwidth = treeview.allocation.width - sum(c.get_width() for c in otherCols)
        adjwidth -= treeview.style_get_property("horizontal-separator") * 4
        if self.props.wrap_width == adjwidth or adjwidth <= 0:
                adjwidth = self.props.wrap_width
        return adjwidth

gobject.type_register(HobWarpCellRendererText)

class HobIconChecker(hic):
    def set_hob_icon_to_stock_icon(self, file_path, stock_id=""):
        try:
            pixbuf = gtk.gdk.pixbuf_new_from_file(file_path)
        except Exception, e:
            return None

        if stock_id and (gtk.icon_factory_lookup_default(stock_id) == None):
            icon_factory = gtk.IconFactory()
            icon_factory.add_default()
            icon_factory.add(stock_id, gtk.IconSet(pixbuf))
            gtk.stock_add([(stock_id, '_label', 0, 0, '')])

            return icon_factory.lookup(stock_id)

        return None

    """
    For make hob icon consistently by request, and avoid icon view diff by system or gtk version, we use some 'hob icon' to replace the 'gtk icon'.
    this function check the stock_id and make hob_id to replaced the gtk_id then return it or ""
    """
    def check_stock_icon(self, stock_name=""):
        HOB_CHECK_STOCK_NAME = {
            ('hic-dialog-info', 'gtk-dialog-info', 'dialog-info')           : self.ICON_INDI_INFO_FILE,
            ('hic-ok',          'gtk-ok',           'ok')                   : self.ICON_INDI_TICK_FILE,
            ('hic-dialog-error', 'gtk-dialog-error', 'dialog-error')        : self.ICON_INDI_ERROR_FILE,
            ('hic-dialog-warning', 'gtk-dialog-warning', 'dialog-warning')  : self.ICON_INDI_ALERT_FILE,
            ('hic-task-refresh', 'gtk-execute', 'execute')                  : self.ICON_INDI_REFRESH_FILE,
        }
        valid_stock_id = stock_name
        if stock_name:
            for names, path in HOB_CHECK_STOCK_NAME.iteritems():
                if stock_name in names:
                    valid_stock_id = names[0]
                    if not gtk.icon_factory_lookup_default(valid_stock_id):
                        self.set_hob_icon_to_stock_icon(path, valid_stock_id)

        return valid_stock_id

class HobCellRendererController(gobject.GObject):
    (MODE_CYCLE_RUNNING, MODE_ONE_SHORT) = range(2)
    __gsignals__ = {
        "run-timer-stopped" : (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                                ()),
    }
    def __init__(self, runningmode=MODE_CYCLE_RUNNING, is_draw_row=False):
        gobject.GObject.__init__(self)
        self.timeout_id = None
        self.current_angle_pos = 0.0
        self.step_angle = 0.0
        self.tree_headers_height = 0
        self.running_cell_areas = []
        self.running_mode = runningmode
        self.is_queue_draw_row_area = is_draw_row
        self.force_stop_enable = False

    def is_active(self):
        if self.timeout_id:
            return True
        else:
            return False

    def reset_run(self):
        self.force_stop()
        self.running_cell_areas = []
        self.current_angle_pos = 0.0
        self.step_angle = 0.0

    ''' time_iterval: (1~1000)ms, which will be as the basic interval count for timer
        init_usrdata: the current data which related the progress-bar will be at
        min_usrdata: the range of min of user data
        max_usrdata: the range of max of user data
        step: each step which you want to progress
        Note: the init_usrdata should in the range of from min to max, and max should > min
             step should < (max - min)
    '''
    def start_run(self, time_iterval, init_usrdata, min_usrdata, max_usrdata, step, tree):
        if (not time_iterval) or (not max_usrdata):
            return
        usr_range = (max_usrdata - min_usrdata) * 1.0
        self.current_angle_pos = (init_usrdata * 1.0) / usr_range
        self.step_angle = (step * 1) / usr_range
        self.timeout_id = gobject.timeout_add(int(time_iterval),
        self.make_image_on_progressing_cb, tree)
        self.tree_headers_height = self.get_treeview_headers_height(tree)
        self.force_stop_enable = False

    def force_stop(self):
        self.emit("run-timer-stopped")
        self.force_stop_enable = True
        if self.timeout_id:
            if gobject.source_remove(self.timeout_id):
                self.timeout_id = None

    def on_draw_pixbuf_cb(self, pixbuf, cr, x, y, img_width, img_height, do_refresh=True):
        if pixbuf:
            r = max(img_width/2, img_height/2)
            cr.translate(x + r, y + r)
            if do_refresh:
                cr.rotate(2 * math.pi * self.current_angle_pos)

            cr.set_source_pixbuf(pixbuf, -img_width/2, -img_height/2)
            cr.paint()

    def on_draw_fadeinout_cb(self, cr, color, x, y, width, height, do_fadeout=True):
        if do_fadeout:
            alpha = self.current_angle_pos * 0.8
        else:
            alpha = (1.0 - self.current_angle_pos) * 0.8

        cr.set_source_rgba(color.red, color.green, color.blue, alpha)
        cr.rectangle(x, y, width, height)
        cr.fill()

    def get_treeview_headers_height(self, tree):
        if tree and (tree.get_property("headers-visible") == True):
            height = tree.get_allocation().height - tree.get_bin_window().get_size()[1]
            return height

        return 0

    def make_image_on_progressing_cb(self, tree):
        self.current_angle_pos += self.step_angle
        if self.running_mode == self.MODE_CYCLE_RUNNING:
            if (self.current_angle_pos >= 1):
                self.current_angle_pos = 0
        else:
            if self.current_angle_pos > 1:
                self.force_stop()
                return False

        if self.is_queue_draw_row_area:
            for path in self.running_cell_areas:
                rect = tree.get_cell_area(path, tree.get_column(0))
                row_x, _, row_width, _ = tree.get_visible_rect()
                tree.queue_draw_area(row_x, rect.y + self.tree_headers_height, row_width, rect.height)
        else:
            for rect in self.running_cell_areas:
                tree.queue_draw_area(rect.x, rect.y + self.tree_headers_height, rect.width, rect.height)

        return (not self.force_stop_enable)

    def append_running_cell_area(self, cell_area):
        if cell_area and (cell_area not in self.running_cell_areas):
            self.running_cell_areas.append(cell_area)

    def remove_running_cell_area(self, cell_area):
        if cell_area in self.running_cell_areas:
            self.running_cell_areas.remove(cell_area)
        if not self.running_cell_areas:
            self.reset_run()

gobject.type_register(HobCellRendererController)

class HobCellRendererPixbuf(gtk.CellRendererPixbuf):
    def __init__(self):
        gtk.CellRendererPixbuf.__init__(self)
        self.control = HobCellRendererController()
        # add icon checker for make the gtk-icon transfer to hob-icon
        self.checker = HobIconChecker()
        self.set_property("stock-size", gtk.ICON_SIZE_DND)

    def get_pixbuf_from_stock_icon(self, widget, stock_id="", size=gtk.ICON_SIZE_DIALOG):
        if widget and stock_id and gtk.icon_factory_lookup_default(stock_id):
            return widget.render_icon(stock_id, size)

        return None

    def set_icon_name_to_id(self, new_name):
        if new_name and type(new_name) == str:
            # check the name is need to transfer to hob icon or not
            name = self.checker.check_stock_icon(new_name)
            if name.startswith("hic") or name.startswith("gtk"):
                stock_id = name
            else:
                stock_id = 'gtk-' + name

        return stock_id

    ''' render cell exactly, "icon-name" is priority
        if use the 'hic-task-refresh' will make the pix animation
        if 'pix' will change the pixbuf for it from the pixbuf or image.
    '''
    def do_render(self, window, tree, background_area,cell_area, expose_area, flags):
        if (not self.control) or (not tree):
            return

        x, y, w, h = self.on_get_size(tree, cell_area)
        x += cell_area.x
        y += cell_area.y
        w -= 2 * self.get_property("xpad")
        h -= 2 * self.get_property("ypad")

        stock_id = ""
        if self.props.icon_name:
            stock_id = self.set_icon_name_to_id(self.props.icon_name)
        elif self.props.stock_id:
            stock_id = self.props.stock_id
        elif self.props.pixbuf:
            pix = self.props.pixbuf
        else:
            return

        if stock_id:
            pix = self.get_pixbuf_from_stock_icon(tree, stock_id, self.props.stock_size)
        if stock_id == 'hic-task-refresh':
            self.control.append_running_cell_area(cell_area)
            if self.control.is_active():
                self.control.on_draw_pixbuf_cb(pix, window.cairo_create(), x, y, w, h, True)
            else:
                self.control.start_run(200, 0, 0, 1000, 150, tree)
        else:
            self.control.remove_running_cell_area(cell_area)
            self.control.on_draw_pixbuf_cb(pix, window.cairo_create(), x, y, w, h, False)

    def on_get_size(self, widget, cell_area):
        if self.props.icon_name or self.props.pixbuf or self.props.stock_id:
            w, h = gtk.icon_size_lookup(self.props.stock_size)
            calc_width = self.get_property("xpad") * 2 + w
            calc_height = self.get_property("ypad") * 2 + h
            x_offset = 0
            y_offset = 0
            if cell_area and w > 0 and h > 0:
                x_offset = self.get_property("xalign") * (cell_area.width - calc_width - self.get_property("xpad"))
                y_offset = self.get_property("yalign") * (cell_area.height - calc_height - self.get_property("ypad"))

            return x_offset, y_offset, w, h

        return 0, 0, 0, 0

gobject.type_register(HobCellRendererPixbuf)

class HobCellRendererToggle(gtk.CellRendererToggle):
    def __init__(self):
        gtk.CellRendererToggle.__init__(self)
        self.ctrl = HobCellRendererController(is_draw_row=True)
        self.ctrl.running_mode = self.ctrl.MODE_ONE_SHORT
        self.cell_attr = {"fadeout": False, "number_of_children": 0}

    def do_render(self, window, widget, background_area, cell_area, expose_area, flags):
        if (not self.ctrl) or (not widget):
            return

        if flags & gtk.CELL_RENDERER_SELECTED:
            state = gtk.STATE_SELECTED
        else:
            state = gtk.STATE_NORMAL

        if self.ctrl.is_active():
            path = widget.get_path_at_pos(cell_area.x + cell_area.width/2, cell_area.y + cell_area.height/2)
            # sometimes the parameters of cell_area will be a negative number,such as pull up down the scroll bar
            # it's over the tree container range, so the path will be bad
            if not path: return
            path = path[0]
            if path in self.ctrl.running_cell_areas:
                cr = window.cairo_create()
                color = widget.get_style().base[state]

                row_x, _, row_width, _ = widget.get_visible_rect()
                border_y = self.get_property("ypad")
                self.ctrl.on_draw_fadeinout_cb(cr, color, row_x, cell_area.y - border_y, row_width, \
                                               cell_area.height + border_y * 2, self.cell_attr["fadeout"])
        # draw number of a group
        if self.cell_attr["number_of_children"]:
            text = "%d pkg" % self.cell_attr["number_of_children"]
            pangolayout = widget.create_pango_layout(text)
            textw, texth = pangolayout.get_pixel_size()
            x = cell_area.x + (cell_area.width/2) - (textw/2)
            y = cell_area.y + (cell_area.height/2) - (texth/2)

            widget.style.paint_layout(window, state, True, cell_area, widget, "checkbox", x, y, pangolayout)
        else:
            return gtk.CellRendererToggle.do_render(self, window, widget, background_area, cell_area, expose_area, flags)

    '''delay: normally delay time is 1000ms
       cell_list: whilch cells need to be render
    '''
    def fadeout(self, tree, delay, cell_list=None):
        if (delay < 200) or (not tree):
            return
        self.cell_attr["fadeout"] = True
        self.ctrl.running_cell_areas = cell_list
        self.ctrl.start_run(200, 0, 0, delay, (delay * 200 / 1000), tree)

    def connect_render_state_changed(self, func, usrdata=None):
        if not func:
            return
        if usrdata:
            self.ctrl.connect("run-timer-stopped", func, self, usrdata)
        else:
            self.ctrl.connect("run-timer-stopped", func, self)

gobject.type_register(HobCellRendererToggle)
