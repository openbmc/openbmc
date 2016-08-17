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
import gobject
import os
import tempfile
from bb.ui.crumbs.hobwidget import hic, HobButton, HobAltButton
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.crumbsmessagedialog import CrumbsMessageDialog

"""
The following are convenience classes for implementing GNOME HIG compliant
BitBake GUI's
In summary: spacing = 12px, border-width = 6px
"""

class CellRendererPixbufActivatable(gtk.CellRendererPixbuf):
    """
    A custom CellRenderer implementation which is activatable
    so that we can handle user clicks
    """
    __gsignals__    = { 'clicked' : (gobject.SIGNAL_RUN_LAST,
                                     gobject.TYPE_NONE,
                                     (gobject.TYPE_STRING,)), }

    def __init__(self):
        gtk.CellRendererPixbuf.__init__(self)
        self.set_property('mode', gtk.CELL_RENDERER_MODE_ACTIVATABLE)
        self.set_property('follow-state', True)

    """
    Respond to a user click on a cell
    """
    def do_activate(self, even, widget, path, background_area, cell_area, flags):
        self.emit('clicked', path)

#
# LayerSelectionDialog
#
class LayerSelectionDialog (CrumbsDialog):

    TARGETS = [
        ("MY_TREE_MODEL_ROW", gtk.TARGET_SAME_WIDGET, 0),
        ("text/plain", 0, 1),
        ("TEXT", 0, 2),
        ("STRING", 0, 3),
        ]

    def gen_label_widget(self, content):
        label = gtk.Label()
        label.set_alignment(0, 0)
        label.set_markup(content)
        label.show()
        return label

    def layer_widget_toggled_cb(self, cell, path, layer_store):
        name = layer_store[path][0]
        toggle = not layer_store[path][1]
        layer_store[path][1] = toggle

    def layer_widget_add_clicked_cb(self, action, layer_store, parent):
        dialog = gtk.FileChooserDialog("Add new layer", parent,
                                       gtk.FILE_CHOOSER_ACTION_SELECT_FOLDER)
        button = dialog.add_button("Cancel", gtk.RESPONSE_NO)
        HobAltButton.style_button(button)
        button = dialog.add_button("Open", gtk.RESPONSE_YES)
        HobButton.style_button(button)
        label = gtk.Label("Select the layer you wish to add")
        label.show()
        dialog.set_extra_widget(label)
        response = dialog.run()
        path = dialog.get_filename()
        dialog.destroy()

        lbl = "<b>Error</b>"
        msg = "Unable to load layer <i>%s</i> because " % path
        if response == gtk.RESPONSE_YES:
            import os
            import os.path
            layers = []
            it = layer_store.get_iter_first()
            while it:
                layers.append(layer_store.get_value(it, 0))
                it = layer_store.iter_next(it)

            if not path:
                msg += "it is an invalid path."
            elif not os.path.exists(path+"/conf/layer.conf"):
                msg += "there is no layer.conf inside the directory."
            elif path in layers:
                msg += "it is already in loaded layers."
            else:
                layer_store.append([path])
                return
            dialog = CrumbsMessageDialog(parent, lbl, gtk.MESSAGE_ERROR, msg)
            dialog.add_button(gtk.STOCK_CLOSE, gtk.RESPONSE_OK)
            response = dialog.run()
            dialog.destroy()

    def layer_widget_del_clicked_cb(self, action, tree_selection, layer_store):
        model, iter = tree_selection.get_selected()
        if iter:
            layer_store.remove(iter)


    def gen_layer_widget(self, layers, layers_avail, window, tooltip=""):
        hbox = gtk.HBox(False, 6)

        layer_tv = gtk.TreeView()
        layer_tv.set_rules_hint(True)
        layer_tv.set_headers_visible(False)
        tree_selection = layer_tv.get_selection()
        tree_selection.set_mode(gtk.SELECTION_SINGLE)

        # Allow enable drag and drop of rows including row move
        dnd_internal_target = ''
        dnd_targets = [(dnd_internal_target, gtk.TARGET_SAME_WIDGET, 0)]
        layer_tv.enable_model_drag_source( gtk.gdk.BUTTON1_MASK,
            dnd_targets,
            gtk.gdk.ACTION_MOVE)
        layer_tv.enable_model_drag_dest(dnd_targets,
            gtk.gdk.ACTION_MOVE)
        layer_tv.connect("drag_data_get", self.drag_data_get_cb)
        layer_tv.connect("drag_data_received", self.drag_data_received_cb)

        col0= gtk.TreeViewColumn('Path')
        cell0 = gtk.CellRendererText()
        cell0.set_padding(5,2)
        col0.pack_start(cell0, True)
        col0.set_cell_data_func(cell0, self.draw_layer_path_cb)
        layer_tv.append_column(col0)

        scroll = gtk.ScrolledWindow()
        scroll.set_policy(gtk.POLICY_NEVER, gtk.POLICY_AUTOMATIC)
        scroll.set_shadow_type(gtk.SHADOW_IN)
        scroll.add(layer_tv)

        table_layer = gtk.Table(2, 10, False)
        hbox.pack_start(table_layer, expand=True, fill=True)

        table_layer.attach(scroll, 0, 10, 0, 1)

        layer_store = gtk.ListStore(gobject.TYPE_STRING)
        for layer in layers:
            layer_store.append([layer])

        col1 = gtk.TreeViewColumn('Enabled')
        layer_tv.append_column(col1)

        cell1 = CellRendererPixbufActivatable()
        cell1.set_fixed_size(-1,35)
        cell1.connect("clicked", self.del_cell_clicked_cb, layer_store)
        col1.pack_start(cell1, True)
        col1.set_cell_data_func(cell1, self.draw_delete_button_cb, layer_tv)

        add_button = gtk.Button()
        add_button.set_relief(gtk.RELIEF_NONE)
        box = gtk.HBox(False, 6)
        box.show()
        add_button.add(box)
        add_button.connect("enter-notify-event", self.add_hover_cb)
        add_button.connect("leave-notify-event", self.add_leave_cb)
        self.im = gtk.Image()
        self.im.set_from_file(hic.ICON_INDI_ADD_FILE)
        self.im.show()
        box.pack_start(self.im, expand=False, fill=False, padding=6)
        lbl = gtk.Label("Add layer")
        lbl.set_alignment(0.0, 0.5)
        lbl.show()
        box.pack_start(lbl, expand=True, fill=True, padding=6)
        add_button.connect("clicked", self.layer_widget_add_clicked_cb, layer_store, window)
        table_layer.attach(add_button, 0, 10, 1, 2, gtk.EXPAND | gtk.FILL, 0, 0, 6)
        layer_tv.set_model(layer_store)

        hbox.show_all()

        return hbox, layer_store

    def drag_data_get_cb(self, treeview, context, selection, target_id, etime):
        treeselection = treeview.get_selection()
        model, iter = treeselection.get_selected()
        data = model.get_value(iter, 0)
        selection.set(selection.target, 8, data)

    def drag_data_received_cb(self, treeview, context, x, y, selection, info, etime):
        model = treeview.get_model()
        data = selection.data
        drop_info = treeview.get_dest_row_at_pos(x, y)
        if drop_info:
            path, position = drop_info
            iter = model.get_iter(path)
            if (position == gtk.TREE_VIEW_DROP_BEFORE or position == gtk.TREE_VIEW_DROP_INTO_OR_BEFORE):
                model.insert_before(iter, [data])
            else:
                model.insert_after(iter, [data])
        else:
            model.append([data])
        if context.action == gtk.gdk.ACTION_MOVE:
            context.finish(True, True, etime)
        return

    def add_hover_cb(self, button, event):
        self.im.set_from_file(hic.ICON_INDI_ADD_HOVER_FILE)

    def add_leave_cb(self, button, event):
        self.im.set_from_file(hic.ICON_INDI_ADD_FILE)

    def __init__(self, title, layers, layers_non_removable, all_layers, parent, flags, buttons=None):
        super(LayerSelectionDialog, self).__init__(title, parent, flags, buttons)

        # class members from other objects
        self.layers = layers
        self.layers_non_removable = layers_non_removable
        self.all_layers = all_layers
        self.layers_changed = False

        # icon for remove button in TreeView
        im = gtk.Image()
        im.set_from_file(hic.ICON_INDI_REMOVE_FILE)
        self.rem_icon = im.get_pixbuf()

        # class members for internal use
        self.layer_store = None

        # create visual elements on the dialog
        self.create_visual_elements()
        self.connect("response", self.response_cb)

    def create_visual_elements(self):
        layer_widget, self.layer_store = self.gen_layer_widget(self.layers, self.all_layers, self, None)
        layer_widget.set_size_request(450, 250)
        self.vbox.pack_start(layer_widget, expand=True, fill=True)
        self.show_all()

    def response_cb(self, dialog, response_id):
        model = self.layer_store
        it = model.get_iter_first()
        layers = []
        while it:
            layers.append(model.get_value(it, 0))
            it = model.iter_next(it)

        self.layers_changed = (self.layers != layers)
        self.layers = layers

    """
    A custom cell_data_func to draw a delete 'button' in the TreeView for layers
    other than the meta layer. The deletion of which is prevented so that the
    user can't shoot themselves in the foot too badly.
    """
    def draw_delete_button_cb(self, col, cell, model, it, tv):
        path =  model.get_value(it, 0)
        if path in self.layers_non_removable:
            cell.set_sensitive(False)
            cell.set_property('pixbuf', None)
            cell.set_property('mode', gtk.CELL_RENDERER_MODE_INERT)
        else:
            cell.set_property('pixbuf', self.rem_icon)
            cell.set_sensitive(True)
            cell.set_property('mode', gtk.CELL_RENDERER_MODE_ACTIVATABLE)

        return True

    """
    A custom cell_data_func to write an extra message into the layer path cell
    for the meta layer. We should inform the user that they can't remove it for
    their own safety.
    """
    def draw_layer_path_cb(self, col, cell, model, it):
        path = model.get_value(it, 0)
        if path in self.layers_non_removable:
            cell.set_property('markup', "<b>It cannot be removed</b>\n%s" % path)
        else:
            cell.set_property('text', path)

    def del_cell_clicked_cb(self, cell, path, model):
        it = model.get_iter_from_string(path)
        model.remove(it)
