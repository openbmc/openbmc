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

import gobject
import gtk
from bb.ui.crumbs.hobcolor import HobColors
from bb.ui.crumbs.hobwidget import hic, HobViewTable, HobAltButton, HobButton
from bb.ui.crumbs.hobpages import HobPage
import subprocess
from bb.ui.crumbs.hig.crumbsdialog import CrumbsDialog
from bb.ui.crumbs.hig.saveimagedialog import SaveImageDialog

#
# ImageDetailsPage
#
class ImageDetailsPage (HobPage):

    class DetailBox (gtk.EventBox):
        def __init__(self, widget = None, varlist = None, vallist = None, icon = None, button = None, button2=None, color = HobColors.LIGHT_GRAY):
            gtk.EventBox.__init__(self)

            # set color
            style = self.get_style().copy()
            style.bg[gtk.STATE_NORMAL] = self.get_colormap().alloc_color(color, False, False)
            self.set_style(style)

            self.row = gtk.Table(1, 2, False)
            self.row.set_border_width(10)
            self.add(self.row)
        
            total_rows = 0
            if widget:
                total_rows = 10
            if varlist and vallist:
                # pack the icon and the text on the left
                total_rows += len(varlist)
            self.table = gtk.Table(total_rows, 20, True)
            self.table.set_row_spacings(6)
            self.table.set_size_request(100, -1)
            self.row.attach(self.table, 0, 1, 0, 1, xoptions=gtk.FILL|gtk.EXPAND, yoptions=gtk.FILL)
            
            colid = 0
            rowid = 0
            self.line_widgets = {}
            if icon:
                self.table.attach(icon, colid, colid + 2, 0, 1)
                colid = colid + 2
            if widget:
                self.table.attach(widget, colid, 20, 0, 10)
                rowid = 10
            if varlist and vallist:
                for row in range(rowid, total_rows):
                    index = row - rowid
                    self.line_widgets[varlist[index]] = self.text2label(varlist[index], vallist[index])
                    self.table.attach(self.line_widgets[varlist[index]], colid, 20, row, row + 1)
            # pack the button on the right
            if button:
                self.bbox = gtk.VBox()
                self.bbox.pack_start(button, expand=True, fill=False)
                if button2:
                    self.bbox.pack_start(button2, expand=True, fill=False)
                self.bbox.set_size_request(150,-1)
                self.row.attach(self.bbox, 1, 2, 0, 1, xoptions=gtk.FILL, yoptions=gtk.EXPAND)
                
        def update_line_widgets(self, variable, value):
            if len(self.line_widgets) == 0:
                return
            if not isinstance(self.line_widgets[variable], gtk.Label):
                return
            self.line_widgets[variable].set_markup(self.format_line(variable, value))

        def wrap_line(self, inputs):
            # wrap the long text of inputs
            wrap_width_chars = 75
            outputs = ""
            tmps = inputs
            less_chars = len(inputs)
            while (less_chars - wrap_width_chars) > 0:
                less_chars -= wrap_width_chars
                outputs += tmps[:wrap_width_chars] + "\n               "
                tmps = inputs[less_chars:]
            outputs += tmps
            return outputs

        def format_line(self, variable, value):
            wraped_value = self.wrap_line(value)
            markup = "<span weight=\'bold\'>%s</span>" % variable
            markup += "<span weight=\'normal\' foreground=\'#1c1c1c\' font_desc=\'14px\'>%s</span>" % wraped_value
            return markup

        def text2label(self, variable, value):
            # append the name:value to the left box
            # such as "Name: hob-core-minimal-variant-2011-12-15-beagleboard"
            label = gtk.Label()
            label.set_alignment(0.0, 0.5)
            label.set_markup(self.format_line(variable, value))
            return label

    class BuildDetailBox (gtk.EventBox):
        def __init__(self, varlist = None, vallist = None, icon = None, color = HobColors.LIGHT_GRAY):
            gtk.EventBox.__init__(self)

            # set color
            style = self.get_style().copy()
            style.bg[gtk.STATE_NORMAL] = self.get_colormap().alloc_color(color, False, False)
            self.set_style(style)

            self.hbox = gtk.HBox()
            self.hbox.set_border_width(10)
            self.add(self.hbox)

            total_rows = 0
            if varlist and vallist:
                # pack the icon and the text on the left
                total_rows += len(varlist)
            self.table = gtk.Table(total_rows, 20, True)
            self.table.set_row_spacings(6)
            self.table.set_size_request(100, -1)
            self.hbox.pack_start(self.table, expand=True, fill=True, padding=15)

            colid = 0
            rowid = 0
            self.line_widgets = {}
            if icon:
                self.table.attach(icon, colid, colid + 2, 0, 1)
                colid = colid + 2
            if varlist and vallist:
                for row in range(rowid, total_rows):
                    index = row - rowid
                    self.line_widgets[varlist[index]] = self.text2label(varlist[index], vallist[index])
                    self.table.attach(self.line_widgets[varlist[index]], colid, 20, row, row + 1)
                
        def update_line_widgets(self, variable, value):
            if len(self.line_widgets) == 0:
                return
            if not isinstance(self.line_widgets[variable], gtk.Label):
                return
            self.line_widgets[variable].set_markup(self.format_line(variable, value))

        def wrap_line(self, inputs):
            # wrap the long text of inputs
            wrap_width_chars = 75
            outputs = ""
            tmps = inputs
            less_chars = len(inputs)
            while (less_chars - wrap_width_chars) > 0:
                less_chars -= wrap_width_chars
                outputs += tmps[:wrap_width_chars] + "\n               "
                tmps = inputs[less_chars:]
            outputs += tmps
            return outputs

        def format_line(self, variable, value):
            wraped_value = self.wrap_line(value)
            markup = "<span weight=\'bold\'>%s</span>" % variable
            markup += "<span weight=\'normal\' foreground=\'#1c1c1c\' font_desc=\'14px\'>%s</span>" % wraped_value
            return markup

        def text2label(self, variable, value):
            # append the name:value to the left box
            # such as "Name: hob-core-minimal-variant-2011-12-15-beagleboard"
            label = gtk.Label()
            label.set_alignment(0.0, 0.5)
            label.set_markup(self.format_line(variable, value))
            return label

    def __init__(self, builder):
        super(ImageDetailsPage, self).__init__(builder, "Image details")

        self.image_store = []
        self.button_ids = {}
        self.details_bottom_buttons = gtk.HBox(False, 6)
        self.image_saved = False
        self.create_visual_elements()
        self.name_field_template = ""
        self.description_field_template = ""

    def create_visual_elements(self):
        # create visual elements
        # create the toolbar
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

        self.details_top_buttons = self.add_onto_top_bar(self.toolbar)

    def _remove_all_widget(self):
        children = self.get_children() or []
        for child in children:
            self.remove(child)
        children = self.box_group_area.get_children() or []
        for child in children:
            self.box_group_area.remove(child)
        children = self.details_bottom_buttons.get_children() or []
        for child in children:
            self.details_bottom_buttons.remove(child)

    def show_page(self, step):
        self.build_succeeded = (step == self.builder.IMAGE_GENERATED)
        image_addr = self.builder.parameters.image_addr
        image_names = self.builder.parameters.image_names
        if self.build_succeeded:
            machine = self.builder.configuration.curr_mach
            base_image = self.builder.recipe_model.get_selected_image()
            layers = self.builder.configuration.layers
            pkg_num = "%s" % len(self.builder.package_model.get_selected_packages())
            log_file = self.builder.current_logfile
        else:
            pkg_num = "N/A"
            log_file = None

        # remove
        for button_id, button in self.button_ids.items():
            button.disconnect(button_id)
        self._remove_all_widget()

        # repack
        self.pack_start(self.details_top_buttons, expand=False, fill=False)
        self.pack_start(self.group_align, expand=True, fill=True)

        self.build_result = None
        if self.image_saved or (self.build_succeeded and self.builder.current_step == self.builder.IMAGE_GENERATING):
            # building is the previous step
            icon = gtk.Image()
            pixmap_path = hic.ICON_INDI_CONFIRM_FILE
            color = HobColors.RUNNING
            pix_buffer = gtk.gdk.pixbuf_new_from_file(pixmap_path)
            icon.set_from_pixbuf(pix_buffer)
            varlist = [""]
            if self.image_saved:
                vallist = ["Your image recipe has been saved"]
            else:
                vallist = ["Your image is ready"]
            self.build_result = self.BuildDetailBox(varlist=varlist, vallist=vallist, icon=icon, color=color)
            self.box_group_area.pack_start(self.build_result, expand=False, fill=False)

        self.buttonlist = ["Build new image", "Save image recipe", "Run image", "Deploy image"]

        # Name
        self.image_store = []
        self.toggled_image = ""
        default_image_size = 0
        self.num_toggled = 0
        i = 0
        for image_name in image_names:
            image_size = HobPage._size_to_string(os.stat(os.path.join(image_addr, image_name)).st_size)

            image_attr = ("run" if (self.test_type_runnable(image_name) and self.test_mach_runnable(image_name)) else \
                          ("deploy" if self.test_deployable(image_name) else ""))
            is_toggled = (image_attr != "")

            if not self.toggled_image:
                if i == (len(image_names) - 1):
                    is_toggled = True
                if is_toggled:
                    default_image_size = image_size
                    self.toggled_image = image_name

            split_stuff = image_name.split('.')
            if "rootfs" in split_stuff:
                image_type = image_name[(len(split_stuff[0]) + len(".rootfs") + 1):]
            else:
                image_type = image_name[(len(split_stuff[0]) + 1):]

            self.image_store.append({'name': image_name,
                                    'type': image_type,
                                    'size': image_size,
                                    'is_toggled': is_toggled,
                                    'action_attr': image_attr,})

            i = i + 1
            self.num_toggled += is_toggled

        is_runnable = self.create_bottom_buttons(self.buttonlist, self.toggled_image)

        # Generated image files info
        varlist = ["Name: ", "Files created: ", "Directory: "]
        vallist = []

        vallist.append(image_name.split('.')[0])
        vallist.append(',  '.join(fileitem['type'] for fileitem in self.image_store))
        vallist.append(image_addr)

        view_files_button = HobAltButton("View files")
        view_files_button.connect("clicked", self.view_files_clicked_cb, image_addr)
        view_files_button.set_tooltip_text("Open the directory containing the image files")
        open_log_button = None
        if log_file:
            open_log_button = HobAltButton("Open log")
            open_log_button.connect("clicked", self.open_log_clicked_cb, log_file)
            open_log_button.set_tooltip_text("Open the build's log file")
        self.image_detail = self.DetailBox(varlist=varlist, vallist=vallist, button=view_files_button, button2=open_log_button)
        self.box_group_area.pack_start(self.image_detail, expand=False, fill=True)

        # The default kernel box for the qemu images
        self.sel_kernel = ""
        self.kernel_detail = None
        if 'qemu' in image_name:
            self.sel_kernel = self.get_kernel_file_name()

        #    varlist = ["Kernel: "]
        #    vallist = []
        #    vallist.append(self.sel_kernel)

        #    change_kernel_button = HobAltButton("Change")
        #    change_kernel_button.connect("clicked", self.change_kernel_cb)
        #    change_kernel_button.set_tooltip_text("Change qemu kernel file")
        #    self.kernel_detail = self.DetailBox(varlist=varlist, vallist=vallist, button=change_kernel_button)
        #    self.box_group_area.pack_start(self.kernel_detail, expand=True, fill=True)

        # Machine, Image recipe and Layers
        layer_num_limit = 15
        varlist = ["Machine: ", "Image recipe: ", "Layers: "]
        vallist = []
        self.setting_detail = None
        if self.build_succeeded:
            vallist.append(machine)
            if self.builder.recipe_model.is_custom_image():
                if self.builder.configuration.initial_selected_image == self.builder.recipe_model.__custom_image__:
                    base_image ="New image recipe"
                else:
                    base_image = self.builder.configuration.initial_selected_image + " (edited)"
            vallist.append(base_image)
            i = 0
            for layer in layers:
                if i > layer_num_limit:
                    break
                varlist.append(" - ")
                i += 1
            vallist.append("")
            i = 0
            for layer in layers:
                if i > layer_num_limit:
                    break
                elif i == layer_num_limit:
                    vallist.append("and more...")
                else:
                    vallist.append(layer)
                i += 1

            edit_config_button = HobAltButton("Edit configuration")
            edit_config_button.set_tooltip_text("Edit machine and image recipe")
            edit_config_button.connect("clicked", self.edit_config_button_clicked_cb)
            self.setting_detail = self.DetailBox(varlist=varlist, vallist=vallist, button=edit_config_button)
            self.box_group_area.pack_start(self.setting_detail, expand=True, fill=True)

        # Packages included, and Total image size
        varlist = ["Packages included: ", "Total image size: "]
        vallist = []
        vallist.append(pkg_num)
        vallist.append(default_image_size)
        self.builder.configuration.image_size = default_image_size
        self.builder.configuration.image_packages = self.builder.configuration.selected_packages
        if self.build_succeeded:
            edit_packages_button = HobAltButton("Edit packages")
            edit_packages_button.set_tooltip_text("Edit the packages included in your image")
            edit_packages_button.connect("clicked", self.edit_packages_button_clicked_cb)
        else: # get to this page from "My images"
            edit_packages_button = None
        self.package_detail = self.DetailBox(varlist=varlist, vallist=vallist, button=edit_packages_button)
        self.box_group_area.pack_start(self.package_detail, expand=True, fill=True)

        # pack the buttons at the bottom, at this time they are already created.
        if self.build_succeeded:
            self.box_group_area.pack_end(self.details_bottom_buttons, expand=False, fill=False)
        else: # for "My images" page
            self.details_separator = gtk.HSeparator()
            self.box_group_area.pack_start(self.details_separator, expand=False, fill=False)
            self.box_group_area.pack_start(self.details_bottom_buttons, expand=False, fill=False)

        self.show_all()
        if self.kernel_detail and (not is_runnable):
            self.kernel_detail.hide()
        self.image_saved = False

    def view_files_clicked_cb(self, button, image_addr):
        subprocess.call("xdg-open /%s" % image_addr, shell=True)

    def open_log_clicked_cb(self, button, log_file):
        if log_file:
            log_file = "file:///" + log_file
            gtk.show_uri(screen=button.get_screen(), uri=log_file, timestamp=0)

    def refresh_package_detail_box(self, image_size):
        self.package_detail.update_line_widgets("Total image size: ", image_size)

    def test_type_runnable(self, image_name):
        type_runnable = False
        for t in self.builder.parameters.runnable_image_types:
            if image_name.endswith(t):
                type_runnable = True
                break
        return type_runnable

    def test_mach_runnable(self, image_name):
        mach_runnable = False
        for t in self.builder.parameters.runnable_machine_patterns:
            if t in image_name:
                mach_runnable = True
                break
        return mach_runnable

    def test_deployable(self, image_name):
        if self.builder.configuration.curr_mach.startswith("qemu"):
            return False
        deployable = False
        for t in self.builder.parameters.deployable_image_types:
            if image_name.endswith(t):
                deployable = True
                break
        return deployable

    def get_kernel_file_name(self, kernel_addr=""):
        kernel_name = ""

        if not kernel_addr:
            kernel_addr = self.builder.parameters.image_addr

        files = [f for f in os.listdir(kernel_addr) if f[0] <> '.']
        for check_file in files:
            if check_file.endswith(".bin"):
                name_splits = check_file.split(".")[0]
                if self.builder.parameters.kernel_image_type in name_splits.split("-"):
                    kernel_name = check_file
                    break

        return kernel_name

    def show_builded_images_dialog(self, widget, primary_action=""):
        title = primary_action if primary_action else "Your builded images"
        dialog = CrumbsDialog(title, self.builder,
                              gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)
        dialog.set_border_width(12)

        label = gtk.Label()
        label.set_use_markup(True)
        label.set_alignment(0.0, 0.5)
        label.set_padding(12,0)
        if primary_action == "Run image":
            label.set_markup("<span font_desc='12'>Select the image file you want to run:</span>")
        elif primary_action == "Deploy image":
            label.set_markup("<span font_desc='12'>Select the image file you want to deploy:</span>")
        else:
            label.set_markup("<span font_desc='12'>Select the image file you want to %s</span>" % primary_action)
        dialog.vbox.pack_start(label, expand=False, fill=False)

        # filter created images as action attribution (deploy or run)
        action_attr = ""
        action_images = []
        for fileitem in self.image_store:
            action_attr = fileitem['action_attr']
            if  (action_attr == 'run' and primary_action == "Run image") \
             or (action_attr == 'deploy' and primary_action == "Deploy image"):
                action_images.append(fileitem)

        # pack the corresponding 'runnable' or 'deploy' radio_buttons, if there has no more than one file.
        # assume that there does not both have 'deploy' and 'runnable' files in the same building result
        # in possible as design.
        curr_row = 0
        rows = (len(action_images)) if len(action_images) < 10 else 10
        table = gtk.Table(rows, 10, True)
        table.set_row_spacings(6)
        table.set_col_spacing(0, 12)
        table.set_col_spacing(5, 12)

        sel_parent_btn = None
        for fileitem in action_images:
            sel_btn = gtk.RadioButton(sel_parent_btn, fileitem['type'])
            sel_parent_btn = sel_btn if not sel_parent_btn else sel_parent_btn
            sel_btn.set_active(fileitem['is_toggled'])
            sel_btn.connect('toggled', self.table_selected_cb, fileitem)
            if curr_row < 10:
                table.attach(sel_btn, 0, 4, curr_row, curr_row + 1, xpadding=24)
            else:
                table.attach(sel_btn, 5, 9, curr_row - 10, curr_row - 9, xpadding=24)
            curr_row += 1

        dialog.vbox.pack_start(table, expand=False, fill=False, padding=6)

        button = dialog.add_button("Cancel", gtk.RESPONSE_CANCEL)
        HobAltButton.style_button(button)

        if primary_action:
            button = dialog.add_button(primary_action, gtk.RESPONSE_YES)
            HobButton.style_button(button)

        dialog.show_all()

        response = dialog.run()
        dialog.destroy()

        if response != gtk.RESPONSE_YES:
            return

        for fileitem in self.image_store:
            if fileitem['is_toggled']:
                if fileitem['action_attr'] == 'run':
                    self.builder.runqemu_image(fileitem['name'], self.sel_kernel)
                elif fileitem['action_attr'] == 'deploy':
                    self.builder.deploy_image(fileitem['name'])

    def table_selected_cb(self, tbutton, image):
        image['is_toggled'] = tbutton.get_active()
        if image['is_toggled']:
            self.toggled_image = image['name']

    def change_kernel_cb(self, widget):
        kernel_path = self.builder.show_load_kernel_dialog()
        if kernel_path and self.kernel_detail:
            import os.path
            self.sel_kernel = os.path.basename(kernel_path)
            markup = self.kernel_detail.format_line("Kernel: ", self.sel_kernel)
            label = ((self.kernel_detail.get_children()[0]).get_children()[0]).get_children()[0]
            label.set_markup(markup)

    def create_bottom_buttons(self, buttonlist, image_name):
        # Create the buttons at the bottom
        created = False
        packed = False
        self.button_ids = {}
        is_runnable = False

        # create button "Deploy image"
        name = "Deploy image"
        if name in buttonlist and self.test_deployable(image_name):
            deploy_button = HobButton('Deploy image')
            #deploy_button.set_size_request(205, 49)
            deploy_button.set_tooltip_text("Burn a live image to a USB drive or flash memory")
            deploy_button.set_flags(gtk.CAN_DEFAULT)
            button_id = deploy_button.connect("clicked", self.deploy_button_clicked_cb)
            self.button_ids[button_id] = deploy_button
            self.details_bottom_buttons.pack_end(deploy_button, expand=False, fill=False)
            created = True
            packed = True

        name = "Run image"
        if name in buttonlist and self.test_type_runnable(image_name) and self.test_mach_runnable(image_name):
            if created == True:
                # separator
                #label = gtk.Label(" or ")
                #self.details_bottom_buttons.pack_end(label, expand=False, fill=False)

                # create button "Run image"
                run_button = HobAltButton("Run image")
            else:
                # create button "Run image" as the primary button
                run_button = HobButton("Run image")
                #run_button.set_size_request(205, 49)
                run_button.set_flags(gtk.CAN_DEFAULT)
                packed = True
            run_button.set_tooltip_text("Start up an image with qemu emulator")
            button_id = run_button.connect("clicked", self.run_button_clicked_cb)
            self.button_ids[button_id] = run_button
            self.details_bottom_buttons.pack_end(run_button, expand=False, fill=False)
            created = True
            is_runnable = True

        name = "Save image recipe"
        if name in buttonlist and self.builder.recipe_model.is_custom_image():
            save_button = HobAltButton("Save image recipe")
            save_button.set_tooltip_text("Keep your changes saving them as an image recipe")
            save_button.set_sensitive(not self.image_saved)
            button_id = save_button.connect("clicked", self.save_button_clicked_cb)
            self.button_ids[button_id] = save_button
            self.details_bottom_buttons.pack_end(save_button, expand=False, fill=False)

        name = "Build new image"
        if name in buttonlist:
            # create button "Build new image"
            if packed:
                build_new_button = HobAltButton("Build new image")
            else:
                build_new_button = HobButton("Build new image")
                build_new_button.set_flags(gtk.CAN_DEFAULT)
            #build_new_button.set_size_request(205, 49)
            self.details_bottom_buttons.pack_end(build_new_button, expand=False, fill=False)
            build_new_button.set_tooltip_text("Create a new image from scratch")
            button_id = build_new_button.connect("clicked", self.build_new_button_clicked_cb)
            self.button_ids[button_id] = build_new_button

        return is_runnable

    def deploy_button_clicked_cb(self, button):
        if self.toggled_image:
            if self.num_toggled > 1:
                self.set_sensitive(False)
                self.show_builded_images_dialog(None, "Deploy image")
                self.set_sensitive(True)
            else:
                self.builder.deploy_image(self.toggled_image)

    def run_button_clicked_cb(self, button):
        if self.toggled_image:
            if self.num_toggled > 1:
                self.set_sensitive(False)
                self.show_builded_images_dialog(None, "Run image")
                self.set_sensitive(True)
            else:
                self.builder.runqemu_image(self.toggled_image, self.sel_kernel)

    def save_button_clicked_cb(self, button):
        topdir = self.builder.get_topdir()
        images_dir = topdir + "/recipes/images/custom/"
        self.builder.ensure_dir(images_dir)

        self.name_field_template = self.builder.image_configuration_page.custom_image_selected
        if self.name_field_template:
            image_path = self.builder.recipe_model.pn_path[self.name_field_template]
            image_iter = self.builder.recipe_model.get_iter(image_path)
            self.description_field_template = self.builder.recipe_model.get_value(image_iter, self.builder.recipe_model.COL_DESC)
        else:
            self.name_field_template = ""

        dialog = SaveImageDialog(images_dir, self.name_field_template, self.description_field_template,
                 "Save image recipe", self.builder, gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)
        response = dialog.run()
        dialog.destroy()

    def build_new_button_clicked_cb(self, button):
        self.builder.initiate_new_build_async()

    def edit_config_button_clicked_cb(self, button):
        self.builder.show_configuration()

    def edit_packages_button_clicked_cb(self, button):
        self.builder.show_packages()

    def my_images_button_clicked_cb(self, button):
        self.builder.show_load_my_images_dialog()

    def settings_button_clicked_cb(self, button):
        # Create an advanced settings dialog
        response, settings_changed = self.builder.show_simple_settings_dialog()
        if not response:
            return
        if settings_changed:
            self.builder.reparse_post_adv_settings()
