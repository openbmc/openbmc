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
import re
from bb.ui.crumbs.progressbar import HobProgressBar
from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.hobwidget import hic, HobImageButton, HobInfoButton, HobAltButton, HobButton
from bb.ui.crumbs.hoblistmodel import RecipeListModel
from bb.ui.crumbs.hobpages import HobPage
from bb.ui.crumbs.hig.retrieveimagedialog import RetrieveImageDialog

#
# ImageConfigurationPage
#
class ImageConfigurationPage (HobPage):

    __dummy_machine__ = "--select a machine--"
    __dummy_image__   = "--select an image recipe--"
    __custom_image__  = "Select from my image recipes"

    def __init__(self, builder):
        super(ImageConfigurationPage, self).__init__(builder, "Image configuration")

        self.image_combo_id = None
        # we use machine_combo_changed_by_manual to identify the machine is changed by code
        # or by manual. If by manual, all user's recipe selection and package selection are
        # cleared.
        self.machine_combo_changed_by_manual = True
        self.stopping = False
        self.warning_shift = 0
        self.custom_image_selected = None
        self.create_visual_elements()

    def create_visual_elements(self):
        # create visual elements
        self.toolbar = gtk.Toolbar()
        self.toolbar.set_orientation(gtk.ORIENTATION_HORIZONTAL)
        self.toolbar.set_style(gtk.TOOLBAR_BOTH)

        my_images_button = self.append_toolbar_button(self.toolbar,
            "Images",
            hic.ICON_IMAGES_DISPLAY_FILE,
            hic.ICON_IMAGES_HOVER_FILE,
            "Open previously built images",
            self.my_images_button_clicked_cb)
        settings_button = self.append_toolbar_button(self.toolbar,
            "Settings",
            hic.ICON_SETTINGS_DISPLAY_FILE,
            hic.ICON_SETTINGS_HOVER_FILE,
            "View additional build settings",
            self.settings_button_clicked_cb)

        self.config_top_button = self.add_onto_top_bar(self.toolbar)

        self.gtable = gtk.Table(40, 40, True)
        self.create_config_machine()
        self.create_config_baseimg()
        self.config_build_button = self.create_config_build_button()

    def _remove_all_widget(self):
        children = self.gtable.get_children() or []
        for child in children:
            self.gtable.remove(child)
        children = self.box_group_area.get_children() or []
        for child in children:
            self.box_group_area.remove(child)
        children = self.get_children() or []
        for child in children:
            self.remove(child)

    def _pack_components(self, pack_config_build_button = False):
        self._remove_all_widget()
        self.pack_start(self.config_top_button, expand=False, fill=False)
        self.pack_start(self.group_align, expand=True, fill=True)

        self.box_group_area.pack_start(self.gtable, expand=True, fill=True)
        if pack_config_build_button:
            self.box_group_area.pack_end(self.config_build_button, expand=False, fill=False)
        else:
            box = gtk.HBox(False, 6)
            box.show()
            subbox = gtk.HBox(False, 0)
            subbox.set_size_request(205, 49)
            subbox.show()
            box.add(subbox)
            self.box_group_area.pack_end(box, False, False)

    def show_machine(self):
        self.progress_bar.reset()
        self._pack_components(pack_config_build_button = False)
        self.set_config_machine_layout(show_progress_bar = False)
        self.show_all()

    def update_progress_bar(self, title, fraction, status=None):
        if self.stopping == False:
            self.progress_bar.update(fraction)
            self.progress_bar.set_text(title)
            self.progress_bar.set_rcstyle(status)

    def show_info_populating(self):
        self._pack_components(pack_config_build_button = False)
        self.set_config_machine_layout(show_progress_bar = True)
        self.show_all()

    def show_info_populated(self):
        self.progress_bar.reset()
        self._pack_components(pack_config_build_button = False)
        self.set_config_machine_layout(show_progress_bar = False)
        self.set_config_baseimg_layout()
        self.show_all()

    def show_baseimg_selected(self):
        self.progress_bar.reset()
        self._pack_components(pack_config_build_button = True)
        self.set_config_machine_layout(show_progress_bar = False)
        self.set_config_baseimg_layout()
        self.show_all()
        if self.builder.recipe_model.get_selected_image() == self.builder.recipe_model.__custom_image__:
            self.just_bake_button.hide()

    def add_warnings_bar(self):
        #create the warnings bar shown when recipes parsing generates warnings
        color = HobColors.KHAKI
        warnings_bar = gtk.EventBox()
        warnings_bar.modify_bg(gtk.STATE_NORMAL, gtk.gdk.color_parse(color))
        warnings_bar.set_flags(gtk.CAN_DEFAULT)
        warnings_bar.grab_default()

        build_stop_tab = gtk.Table(10, 20, True)
        warnings_bar.add(build_stop_tab)

        icon = gtk.Image()
        icon_pix_buffer = gtk.gdk.pixbuf_new_from_file(hic.ICON_INDI_ALERT_FILE)
        icon.set_from_pixbuf(icon_pix_buffer)
        build_stop_tab.attach(icon, 0, 2, 0, 10)

        label = gtk.Label()
        label.set_alignment(0.0, 0.5)
        warnings_nb = len(self.builder.parsing_warnings)
        if warnings_nb == 1:
            label.set_markup("<span size='x-large'><b>1 recipe parsing warning</b></span>")
        else:
            label.set_markup("<span size='x-large'><b>%s recipe parsing warnings</b></span>" % warnings_nb)
        build_stop_tab.attach(label, 2, 12, 0, 10)

        view_warnings_button = HobButton("View warnings")
        view_warnings_button.connect('clicked', self.view_warnings_button_clicked_cb)
        build_stop_tab.attach(view_warnings_button, 15, 19, 1, 9)

        return warnings_bar

    def disable_warnings_bar(self):
        if self.builder.parsing_warnings:
            if hasattr(self, 'warnings_bar'):
                self.warnings_bar.hide_all()
            self.builder.parsing_warnings = []

    def create_config_machine(self):
        self.machine_title = gtk.Label()
        self.machine_title.set_alignment(0.0, 0.5)
        mark = "<span %s>Select a machine</span>" % self.span_tag('x-large', 'bold')
        self.machine_title.set_markup(mark)

        self.machine_title_desc = gtk.Label()
        self.machine_title_desc.set_alignment(0.0, 0.5)
        mark = ("<span %s>Your selection is the profile of the target machine for which you"
        " are building the image.\n</span>") % (self.span_tag('medium'))
        self.machine_title_desc.set_markup(mark)

        self.machine_combo = gtk.combo_box_new_text()
        self.machine_combo.connect("changed", self.machine_combo_changed_cb)

        icon_file = hic.ICON_LAYERS_DISPLAY_FILE
        hover_file = hic.ICON_LAYERS_HOVER_FILE
        self.layer_button = HobImageButton("Layers", "Add support for machines, software, etc.",
                                icon_file, hover_file)
        self.layer_button.connect("clicked", self.layer_button_clicked_cb)

        markup = "Layers are a powerful mechanism to extend the Yocto Project "
        markup += "with your own functionality.\n"
        markup += "For more on layers, check the <a href=\""
        markup += "http://www.yoctoproject.org/docs/current/dev-manual/"
        markup += "dev-manual.html#understanding-and-using-layers\">reference manual</a>."
        self.layer_info_icon = HobInfoButton("<b>Layers</b>" + "*" + markup, self.get_parent())
        self.progress_bar = HobProgressBar()
        self.stop_button = HobAltButton("Stop")
        self.stop_button.connect("clicked", self.stop_button_clicked_cb)
        self.machine_separator = gtk.HSeparator()

    def set_config_machine_layout(self, show_progress_bar = False):
        self.gtable.attach(self.machine_title, 0, 40, 0, 4)
        self.gtable.attach(self.machine_title_desc, 0, 40, 4, 6)
        self.gtable.attach(self.machine_combo, 0, 12, 7, 10)
        self.gtable.attach(self.layer_button, 14, 36, 7, 12)
        self.gtable.attach(self.layer_info_icon, 36, 40, 7, 11)
        if show_progress_bar:
            #self.gtable.attach(self.progress_box, 0, 40, 15, 18)
            self.gtable.attach(self.progress_bar, 0, 37, 15, 18)
            self.gtable.attach(self.stop_button, 37, 40, 15, 18, 0, 0)
        if self.builder.parsing_warnings:
            self.warnings_bar = self.add_warnings_bar()
            self.gtable.attach(self.warnings_bar, 0, 40, 14, 18)
            self.warning_shift = 4
        else:
            self.warning_shift = 0
        self.gtable.attach(self.machine_separator, 0, 40, 13, 14)

    def create_config_baseimg(self):
        self.image_title = gtk.Label()
        self.image_title.set_alignment(0, 1.0)
        mark = "<span %s>Select an image recipe</span>" % self.span_tag('x-large', 'bold')
        self.image_title.set_markup(mark)

        self.image_title_desc = gtk.Label()
        self.image_title_desc.set_alignment(0, 0.5)

        mark = ("<span %s>Image recipes are a starting point for the type of image you want. "
                "You can build them as \n"
                "they are or edit them to suit your needs.\n</span>") % self.span_tag('medium')
        self.image_title_desc.set_markup(mark)

        self.image_combo = gtk.combo_box_new_text()
        self.image_combo.set_row_separator_func(self.combo_separator_func, None)
        self.image_combo_id = self.image_combo.connect("changed", self.image_combo_changed_cb)

        self.image_desc = gtk.Label()
        self.image_desc.set_alignment(0.0, 0.5)
        self.image_desc.set_size_request(256, -1)
        self.image_desc.set_justify(gtk.JUSTIFY_LEFT)
        self.image_desc.set_line_wrap(True)

        # button to view recipes
        icon_file = hic.ICON_RCIPE_DISPLAY_FILE
        hover_file = hic.ICON_RCIPE_HOVER_FILE
        self.view_adv_configuration_button = HobImageButton("Advanced configuration",
                                                                 "Select image types, package formats, etc",
                                                                 icon_file, hover_file)        
        self.view_adv_configuration_button.connect("clicked", self.view_adv_configuration_button_clicked_cb)

        self.image_separator = gtk.HSeparator()

    def combo_separator_func(self, model, iter, user_data):
        name = model.get_value(iter, 0)
        if name == "--Separator--":
            return True

    def set_config_baseimg_layout(self):
        self.gtable.attach(self.image_title, 0, 40, 15+self.warning_shift, 17+self.warning_shift)
        self.gtable.attach(self.image_title_desc, 0, 40, 18+self.warning_shift, 22+self.warning_shift)
        self.gtable.attach(self.image_combo, 0, 12, 23+self.warning_shift, 26+self.warning_shift)
        self.gtable.attach(self.image_desc, 0, 12, 27+self.warning_shift, 33+self.warning_shift)
        self.gtable.attach(self.view_adv_configuration_button, 14, 36, 23+self.warning_shift, 28+self.warning_shift)
        self.gtable.attach(self.image_separator, 0, 40, 35+self.warning_shift, 36+self.warning_shift)

    def create_config_build_button(self):
        # Create the "Build packages" and "Build image" buttons at the bottom
        button_box = gtk.HBox(False, 6)

        # create button "Build image"
        self.just_bake_button = HobButton("Build image")
        self.just_bake_button.set_tooltip_text("Build the image recipe as it is")
        self.just_bake_button.connect("clicked", self.just_bake_button_clicked_cb)
        button_box.pack_end(self.just_bake_button, expand=False, fill=False)

        # create button "Edit image recipe"
        self.edit_image_button = HobAltButton("Edit image recipe")
        self.edit_image_button.set_tooltip_text("Customize the recipes and packages to be included in your image")
        self.edit_image_button.connect("clicked", self.edit_image_button_clicked_cb)
        button_box.pack_end(self.edit_image_button, expand=False, fill=False)

        return button_box

    def stop_button_clicked_cb(self, button):
        self.stopping = True
        self.progress_bar.set_text("Stopping recipe parsing")
        self.progress_bar.set_rcstyle("stop")
        self.builder.cancel_parse_sync()

    def view_warnings_button_clicked_cb(self, button):
        self.builder.show_warning_dialog()

    def machine_combo_changed_idle_cb(self):
        self.builder.window.set_cursor(None)

    def machine_combo_changed_cb(self, machine_combo):
        self.stopping = False
        self.builder.parsing_warnings = []
        combo_item = machine_combo.get_active_text()
        if not combo_item or combo_item == self.__dummy_machine__:
            return

        self.builder.window.set_cursor(gtk.gdk.Cursor(gtk.gdk.WATCH))
        self.builder.wait(0.1) #wait for combo and cursor to update

        # remove __dummy_machine__ item from the store list after first user selection
        # because it is no longer valid
        combo_store = machine_combo.get_model()
        if len(combo_store) and (combo_store[0][0] == self.__dummy_machine__):
            machine_combo.remove_text(0)

        self.builder.configuration.curr_mach = combo_item
        if self.machine_combo_changed_by_manual:
            self.builder.configuration.clear_selection()
        # reset machine_combo_changed_by_manual
        self.machine_combo_changed_by_manual = True

        self.builder.configuration.selected_image = None

        # Do reparse recipes
        self.builder.populate_recipe_package_info_async()

        glib.idle_add(self.machine_combo_changed_idle_cb)

    def update_machine_combo(self):
        self.disable_warnings_bar()
        all_machines = [self.__dummy_machine__] + self.builder.parameters.all_machines

        model = self.machine_combo.get_model()
        model.clear()
        for machine in all_machines:
            self.machine_combo.append_text(machine)
        self.machine_combo.set_active(0)

    def switch_machine_combo(self):
        self.disable_warnings_bar()
        self.machine_combo_changed_by_manual = False
        model = self.machine_combo.get_model()
        active = 0
        while active < len(model):
            if model[active][0] == self.builder.configuration.curr_mach:
                self.machine_combo.set_active(active)
                return
            active += 1

        if model[0][0] != self.__dummy_machine__:
            self.machine_combo.insert_text(0, self.__dummy_machine__)

        self.machine_combo.set_active(0)

    def update_image_desc(self):
        desc = ""
        selected_image = self.image_combo.get_active_text()
        if selected_image and selected_image in self.builder.recipe_model.pn_path.keys():
            image_path = self.builder.recipe_model.pn_path[selected_image]
            image_iter = self.builder.recipe_model.get_iter(image_path)
            desc = self.builder.recipe_model.get_value(image_iter, self.builder.recipe_model.COL_DESC)

        mark = ("<span %s>%s</span>\n") % (self.span_tag('small'), desc)
        self.image_desc.set_markup(mark)

    def image_combo_changed_idle_cb(self, selected_image, selected_recipes, selected_packages):
        self.builder.update_recipe_model(selected_image, selected_recipes)
        self.builder.update_package_model(selected_packages)
        self.builder.window_sensitive(True)

    def image_combo_changed_cb(self, combo):
        self.builder.window_sensitive(False)
        selected_image = self.image_combo.get_active_text()
        if selected_image == self.__custom_image__:
            topdir = self.builder.get_topdir()
            images_dir = topdir + "/recipes/images/custom/"
            self.builder.ensure_dir(images_dir)

            dialog = RetrieveImageDialog(images_dir, "Select from my image recipes",
                            self.builder, gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)
            response = dialog.run()
            if response == gtk.RESPONSE_OK:
                image_name = dialog.get_filename()
                head, tail = os.path.split(image_name)
                selected_image = os.path.splitext(tail)[0]
                self.custom_image_selected = selected_image
                self.update_image_combo(self.builder.recipe_model, selected_image)
            else:
                selected_image = self.__dummy_image__
                self.update_image_combo(self.builder.recipe_model, None)
            dialog.destroy()
        else:
            if self.custom_image_selected:
                self.custom_image_selected = None
                self.update_image_combo(self.builder.recipe_model, selected_image)

        if not selected_image or (selected_image == self.__dummy_image__):
            self.builder.window_sensitive(True)
            self.just_bake_button.hide()
            self.edit_image_button.hide()
            return

        # remove __dummy_image__ item from the store list after first user selection
        # because it is no longer valid
        combo_store = combo.get_model()
        if len(combo_store) and (combo_store[0][0] == self.__dummy_image__):
            combo.remove_text(0)

        self.builder.customized = False

        selected_recipes = []

        image_path = self.builder.recipe_model.pn_path[selected_image]
        image_iter = self.builder.recipe_model.get_iter(image_path)
        selected_packages = self.builder.recipe_model.get_value(image_iter, self.builder.recipe_model.COL_INSTALL).split()
        self.update_image_desc()

        self.builder.recipe_model.reset()
        self.builder.package_model.reset()

        self.show_baseimg_selected()

        if selected_image == self.builder.recipe_model.__custom_image__:
            self.just_bake_button.hide()

        glib.idle_add(self.image_combo_changed_idle_cb, selected_image, selected_recipes, selected_packages)

    def _image_combo_connect_signal(self):
        if not self.image_combo_id:
            self.image_combo_id = self.image_combo.connect("changed", self.image_combo_changed_cb)

    def _image_combo_disconnect_signal(self):
        if self.image_combo_id:
            self.image_combo.disconnect(self.image_combo_id)
            self.image_combo_id = None

    def update_image_combo(self, recipe_model, selected_image):
        # Update the image combo according to the images in the recipe_model
        # populate image combo
        filter = {RecipeListModel.COL_TYPE : ['image']}
        image_model = recipe_model.tree_model(filter)
        image_model.set_sort_column_id(recipe_model.COL_NAME, gtk.SORT_ASCENDING)
        active = 0
        cnt = 0

        white_pattern = []
        if self.builder.parameters.image_white_pattern:
            for i in self.builder.parameters.image_white_pattern.split():
                white_pattern.append(re.compile(i))

        black_pattern = []
        if self.builder.parameters.image_black_pattern:
            for i in self.builder.parameters.image_black_pattern.split():
                black_pattern.append(re.compile(i))
        black_pattern.append(re.compile("hob-image"))
        black_pattern.append(re.compile("edited(-[0-9]*)*.bb$"))

        it = image_model.get_iter_first()
        self._image_combo_disconnect_signal()
        model = self.image_combo.get_model()
        model.clear()
        # Set a indicator text to combo store when first open
        if not selected_image:
            self.image_combo.append_text(self.__dummy_image__)
            cnt = cnt + 1

        self.image_combo.append_text(self.__custom_image__)
        self.image_combo.append_text("--Separator--")
        cnt = cnt + 2

        topdir = self.builder.get_topdir()
        # append and set active
        while it:
            path = image_model.get_path(it)
            it = image_model.iter_next(it)
            image_name = image_model[path][recipe_model.COL_NAME]
            if image_name == self.builder.recipe_model.__custom_image__:
                continue

            if black_pattern:
                allow = True
                for pattern in black_pattern:
                    if pattern.search(image_name):
                        allow = False
                        break
            elif white_pattern:
                allow = False
                for pattern in white_pattern:
                    if pattern.search(image_name):
                        allow = True
                        break
            else:
                allow = True

            file_name = image_model[path][recipe_model.COL_FILE]
            if file_name and topdir in file_name:
                allow = False

            if allow:
                self.image_combo.append_text(image_name)
                if image_name == selected_image:
                    active = cnt
                cnt = cnt + 1
        self.image_combo.append_text(self.builder.recipe_model.__custom_image__)

        if selected_image == self.builder.recipe_model.__custom_image__:
            active = cnt

        if self.custom_image_selected:
            self.image_combo.append_text("--Separator--")
            self.image_combo.append_text(self.custom_image_selected)
            cnt = cnt + 2
            if self.custom_image_selected == selected_image:
                active = cnt

        self.image_combo.set_active(active)

        if active != 0:
            self.show_baseimg_selected()

        self._image_combo_connect_signal()

    def layer_button_clicked_cb(self, button):
        # Create a layer selection dialog
        self.builder.show_layer_selection_dialog()

    def view_adv_configuration_button_clicked_cb(self, button):
        # Create an advanced settings dialog
        response, settings_changed = self.builder.show_adv_settings_dialog()
        if not response:
            return
        if settings_changed:
            self.builder.window.set_cursor(gtk.gdk.Cursor(gtk.gdk.WATCH))
            self.builder.wait(0.1) #wait for adv_settings_dialog to terminate
            self.builder.reparse_post_adv_settings()
            self.builder.window.set_cursor(None)

    def just_bake_button_clicked_cb(self, button):
        self.builder.parsing_warnings = []
        self.builder.just_bake()

    def edit_image_button_clicked_cb(self, button):
        self.builder.set_base_image()
        self.builder.show_recipes()

    def my_images_button_clicked_cb(self, button):
        self.builder.show_load_my_images_dialog()

    def settings_button_clicked_cb(self, button):
        # Create an advanced settings dialog
        response, settings_changed = self.builder.show_simple_settings_dialog()
        if not response:
            return
        if settings_changed:
            self.builder.reparse_post_adv_settings()
