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
import glib
from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.hobwidget import HobViewTable, HobNotebook, HobAltButton, HobButton
from bb.ui.crumbs.hoblistmodel import RecipeListModel
from bb.ui.crumbs.hobpages import HobPage

#
# RecipeSelectionPage
#
class RecipeSelectionPage (HobPage):
    pages = [
        {
         'name'      : 'Included recipes',
         'tooltip'   : 'The recipes currently included for your image',
         'filter'    : { RecipeListModel.COL_INC  : [True],
                       RecipeListModel.COL_TYPE : ['recipe', 'packagegroup'] },
         'search'    : 'Search recipes by name',
         'searchtip' : 'Enter a recipe name to find it',
         'columns'   : [{
                       'col_name' : 'Recipe name',
                       'col_id'   : RecipeListModel.COL_NAME,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 400,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Group',
                       'col_id'   : RecipeListModel.COL_GROUP,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 300,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Brought in by (+others)',
                       'col_id'   : RecipeListModel.COL_BINB,
                       'col_style': 'binb',
                       'col_min'  : 100,
                       'col_max'  : 500,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Included',
                       'col_id'   : RecipeListModel.COL_INC,
                       'col_style': 'check toggle',
                       'col_min'  : 100,
                       'col_max'  : 100
                      }]
        }, {
         'name'      : 'All recipes',
         'tooltip'   : 'All recipes in your configured layers',
         'filter'    : { RecipeListModel.COL_TYPE : ['recipe'] },
         'search'    : 'Search recipes by name',
         'searchtip' : 'Enter a recipe name to find it',
         'columns'   : [{
                       'col_name' : 'Recipe name',
                       'col_id'   : RecipeListModel.COL_NAME,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 400,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Group',
                       'col_id'   : RecipeListModel.COL_GROUP,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 400,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'License',
                       'col_id'   : RecipeListModel.COL_LIC,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 400,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Included',
                       'col_id'   : RecipeListModel.COL_INC,
                       'col_style': 'check toggle',
                       'col_min'  : 100,
                       'col_max'  : 100
                      }]
        }, {
         'name'      : 'Package Groups',
         'tooltip'   : 'All package groups in your configured layers',
         'filter'    : { RecipeListModel.COL_TYPE : ['packagegroup'] },
         'search'    : 'Search package groups by name',
         'searchtip' : 'Enter a package group name to find it',
         'columns'   : [{
                       'col_name' : 'Package group name',
                       'col_id'   : RecipeListModel.COL_NAME,
                       'col_style': 'text',
                       'col_min'  : 100,
                       'col_max'  : 400,
                       'expand'   : 'True'
                      }, {
                       'col_name' : 'Included',
                       'col_id'   : RecipeListModel.COL_INC,
                       'col_style': 'check toggle',
                       'col_min'  : 100,
                       'col_max'  : 100
                      }]
        }
    ]
    
    (INCLUDED,
     ALL,
     TASKS) = range(3)

    def __init__(self, builder = None):
        super(RecipeSelectionPage, self).__init__(builder, "Step 1 of 2: Edit recipes")

        # set invisible members
        self.recipe_model = self.builder.recipe_model

        # create visual elements
        self.create_visual_elements()

    def included_clicked_cb(self, button):
        self.ins.set_current_page(self.INCLUDED)

    def create_visual_elements(self):
        self.eventbox = self.add_onto_top_bar(None, 73)
        self.pack_start(self.eventbox, expand=False, fill=False)
        self.pack_start(self.group_align, expand=True, fill=True)

        # set visible members
        self.ins = HobNotebook()
        self.tables = [] # we need modify table when the dialog is shown

        search_names = []
        search_tips = []
        # append the tabs in order
        for page in self.pages:
            columns = page['columns']
            name = page['name']
            tab = HobViewTable(columns, name)
            search_names.append(page['search'])
            search_tips.append(page['searchtip'])
            filter = page['filter']
            sort_model = self.recipe_model.tree_model(filter, initial=True)
            tab.set_model(sort_model)
            tab.connect("toggled", self.table_toggled_cb, name)
            tab.connect("button-release-event", self.button_click_cb)
            tab.connect("cell-fadeinout-stopped", self.after_fadeout_checkin_include, filter)
            self.ins.append_page(tab, page['name'], page['tooltip'])
            self.tables.append(tab)

        self.ins.set_entry(search_names, search_tips)
        self.ins.search.connect("changed", self.search_entry_changed)

        # add all into the window
        self.box_group_area.pack_start(self.ins, expand=True, fill=True)

        button_box = gtk.HBox(False, 6)
        self.box_group_area.pack_end(button_box, expand=False, fill=False)

        self.build_packages_button = HobButton('Build packages')
        #self.build_packages_button.set_size_request(205, 49)
        self.build_packages_button.set_tooltip_text("Build selected recipes into packages")
        self.build_packages_button.set_flags(gtk.CAN_DEFAULT)
        self.build_packages_button.grab_default()
        self.build_packages_button.connect("clicked", self.build_packages_clicked_cb)
        button_box.pack_end(self.build_packages_button, expand=False, fill=False)

        self.back_button = HobAltButton('Cancel')
        self.back_button.connect("clicked", self.back_button_clicked_cb)
        button_box.pack_end(self.back_button, expand=False, fill=False)

    def search_entry_changed(self, entry):
        text = entry.get_text()
        if self.ins.search_focus:
            self.ins.search_focus = False
        elif self.ins.page_changed:
            self.ins.page_change = False
            self.filter_search(entry)
        elif text not in self.ins.search_names:
            self.filter_search(entry)

    def filter_search(self, entry):
        text = entry.get_text()
        current_tab = self.ins.get_current_page()
        filter = self.pages[current_tab]['filter']
        filter[RecipeListModel.COL_NAME] = text
        self.tables[current_tab].set_model(self.recipe_model.tree_model(filter, search_data=text))
        if self.recipe_model.filtered_nb == 0:
            if not self.ins.get_nth_page(current_tab).top_bar:
                self.ins.get_nth_page(current_tab).add_no_result_bar(entry)
                self.ins.get_nth_page(current_tab).top_bar.set_no_show_all(True)
            self.ins.get_nth_page(current_tab).top_bar.show()
            self.ins.get_nth_page(current_tab).scroll.hide()
        else:
            if self.ins.get_nth_page(current_tab).top_bar:
                self.ins.get_nth_page(current_tab).top_bar.hide()
            self.ins.get_nth_page(current_tab).scroll.show()
        if entry.get_text() == '':
            entry.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, False)
        else:
            entry.set_icon_sensitive(gtk.ENTRY_ICON_SECONDARY, True)

    def button_click_cb(self, widget, event):
        path, col = widget.table_tree.get_cursor()
        tree_model = widget.table_tree.get_model()
        if path and col.get_title() != 'Included': # else activation is likely a removal
            properties = {'summary': '', 'name': '', 'version': '', 'revision': '', 'binb': '', 'group': '', 'license': '', 'homepage': '', 'bugtracker': '', 'description': ''}
            properties['summary'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_SUMMARY)
            properties['name'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_NAME)
            properties['version'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_VERSION)
            properties['revision'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_REVISION)
            properties['binb'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_BINB)
            properties['group'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_GROUP)
            properties['license'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_LIC)
            properties['homepage'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_HOMEPAGE)
            properties['bugtracker'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_BUGTRACKER)
            properties['description'] = tree_model.get_value(tree_model.get_iter(path), RecipeListModel.COL_DESC)
            self.builder.show_recipe_property_dialog(properties)

    def build_packages_clicked_cb(self, button):
        self.refresh_tables()
        self.builder.build_packages()

    def refresh_tables(self):
        self.ins.reset_entry(self.ins.search, 0)
        for tab in self.tables:
            index = self.tables.index(tab)
            filter = self.pages[index]['filter']
            tab.set_model(self.recipe_model.tree_model(filter, search_data="", initial=True))

    def back_button_clicked_cb(self, button):
        self.builder.recipe_model.set_selected_image(self.builder.configuration.initial_selected_image)
        self.builder.image_configuration_page.update_image_combo(self.builder.recipe_model, self.builder.configuration.initial_selected_image)
        self.builder.image_configuration_page.update_image_desc()
        self.builder.show_configuration()
        self.refresh_tables()

    def refresh_selection(self):
        self.builder.configuration.selected_image = self.recipe_model.get_selected_image()
        _, self.builder.configuration.selected_recipes = self.recipe_model.get_selected_recipes()
        self.ins.show_indicator_icon("Included recipes", len(self.builder.configuration.selected_recipes))

    def toggle_item_idle_cb(self, path, view_tree, cell, pagename):
        if not self.recipe_model.path_included(path):
            self.recipe_model.include_item(item_path=path, binb="User Selected", image_contents=False)
        else:
            self.pre_fadeout_checkout_include(view_tree, pagename)
            self.recipe_model.exclude_item(item_path=path)
            self.render_fadeout(view_tree, cell)

        self.refresh_selection()
        if not self.builder.customized:
            self.builder.customized = True
            self.builder.configuration.selected_image = self.recipe_model.__custom_image__
            self.builder.rcppkglist_populated()

        self.builder.window_sensitive(True)

        view_model = view_tree.get_model()
        vpath = self.recipe_model.convert_path_to_vpath(view_model, path)
        view_tree.set_cursor(vpath)

    def table_toggled_cb(self, table, cell, view_path, toggled_columnid, view_tree, pagename):
        # Click to include a recipe
        self.builder.window_sensitive(False)
        view_model = view_tree.get_model()
        path = self.recipe_model.convert_vpath_to_path(view_model, view_path)
        glib.idle_add(self.toggle_item_idle_cb, path, view_tree, cell, pagename)

    def pre_fadeout_checkout_include(self, tree, pagename):
        #after the fadeout the table will be sorted as before
        self.sort_column_id = self.recipe_model.sort_column_id
        self.sort_order = self.recipe_model.sort_order

        #resync the included items to a backup fade include column
        it = self.recipe_model.get_iter_first()
        while it:
            active = self.recipe_model.get_value(it, self.recipe_model.COL_INC)
            self.recipe_model.set(it, self.recipe_model.COL_FADE_INC, active)
            it = self.recipe_model.iter_next(it)
        # Check out a model which base on the column COL_FADE_INC,
        # it's save the prev state of column COL_INC before do exclude_item
        filter = { RecipeListModel.COL_FADE_INC:[True] }
        if pagename == "Included recipes":
            filter[RecipeListModel.COL_TYPE] = ['recipe', 'packagegroup']
        elif pagename == "All recipes":
            filter[RecipeListModel.COL_TYPE] = ['recipe']
        else:
            filter[RecipeListModel.COL_TYPE] = ['packagegroup']

        new_model = self.recipe_model.tree_model(filter, excluded_items_ahead=True)
        tree.set_model(new_model)

    def render_fadeout(self, tree, cell):
        if (not cell) or (not tree):
            return
        to_render_cells = []
        model = tree.get_model()
        it = model.get_iter_first()
        while it:
            path = model.get_path(it)
            prev_cell_is_active = model.get_value(it, RecipeListModel.COL_FADE_INC)
            curr_cell_is_active = model.get_value(it, RecipeListModel.COL_INC)
            if (prev_cell_is_active == True) and (curr_cell_is_active == False):
                to_render_cells.append(path)
            it = model.iter_next(it)

        cell.fadeout(tree, 1000, to_render_cells)

    def after_fadeout_checkin_include(self, table, ctrl, cell, tree, filter):
        self.recipe_model.sort_column_id = self.sort_column_id
        self.recipe_model.sort_order = self.sort_order
        tree.set_model(self.recipe_model.tree_model(filter))

    def set_recipe_curr_tab(self, curr_page):
        self.ins.set_current_page(curr_page)
