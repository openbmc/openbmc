#
# BitBake Graphical GTK User Interface
#
# Copyright (C) 2011        Intel Corporation
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
from bb.ui.crumbs.hobpages import HobPage

#
# PackageListModel
#
class PackageListModel(gtk.ListStore):
    """
    This class defines an gtk.ListStore subclass which will convert the output
    of the bb.event.TargetsTreeGenerated event into a gtk.ListStore whilst also
    providing convenience functions to access gtk.TreeModel subclasses which
    provide filtered views of the data.
    """

    (COL_NAME, COL_VER, COL_REV, COL_RNM, COL_SEC, COL_SUM, COL_RDEP, COL_RPROV, COL_SIZE, COL_RCP, COL_BINB, COL_INC, COL_FADE_INC, COL_FONT, COL_FLIST) = range(15)

    __gsignals__ = {
        "package-selection-changed" : (gobject.SIGNAL_RUN_LAST,
                                gobject.TYPE_NONE,
                                ()),
    }

    __toolchain_required_packages__ = ["packagegroup-core-standalone-sdk-target", "packagegroup-core-standalone-sdk-target-dbg"]

    def __init__(self):
        self.rprov_pkg = {}
        gtk.ListStore.__init__ (self,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_BOOLEAN,
                                gobject.TYPE_BOOLEAN,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING)
        self.sort_column_id, self.sort_order = PackageListModel.COL_NAME, gtk.SORT_ASCENDING

    """
    Find the model path for the item_name
    Returns the path in the model or None
    """
    def find_path_for_item(self, item_name):
        pkg = item_name
        if item_name not in self.pn_path.keys():
            if item_name not in self.rprov_pkg.keys():
                return None
            pkg = self.rprov_pkg[item_name]
            if pkg not in self.pn_path.keys():
                return None

        return self.pn_path[pkg]

    def find_item_for_path(self, item_path):
        return self[item_path][self.COL_NAME]

    """
    Helper function to determine whether an item is an item specified by filter
    """
    def tree_model_filter(self, model, it, filter):
        name = model.get_value(it, self.COL_NAME)

        for key in filter.keys():
            if key == self.COL_NAME:
                if filter[key] != 'Search packages by name':
                    if name and filter[key] not in name:
                        return False
            else:
                if model.get_value(it, key) not in filter[key]:
                    return False
        self.filtered_nb += 1
        return True

    """
    Create, if required, and return a filtered gtk.TreeModelSort
    containing only the items specified by filter
    """
    def tree_model(self, filter, excluded_items_ahead=False, included_items_ahead=False, search_data=None, initial=False):
        model = self.filter_new()
        self.filtered_nb = 0
        model.set_visible_func(self.tree_model_filter, filter)

        sort = gtk.TreeModelSort(model)
        sort.connect ('sort-column-changed', self.sort_column_changed_cb)
        if initial:
            sort.set_sort_column_id(PackageListModel.COL_NAME, gtk.SORT_ASCENDING)
            sort.set_default_sort_func(None)
        elif excluded_items_ahead:
            sort.set_default_sort_func(self.exclude_item_sort_func, search_data)
        elif included_items_ahead:
            sort.set_default_sort_func(self.include_item_sort_func, search_data)
        else:
            if search_data and search_data!='Search recipes by name' and search_data!='Search package groups by name':
                sort.set_default_sort_func(self.sort_func, search_data)
            else:
                sort.set_sort_column_id(self.sort_column_id, self.sort_order)
                sort.set_default_sort_func(None)

        sort.set_sort_func(PackageListModel.COL_INC, self.sort_column, PackageListModel.COL_INC)
        sort.set_sort_func(PackageListModel.COL_SIZE, self.sort_column, PackageListModel.COL_SIZE)
        sort.set_sort_func(PackageListModel.COL_BINB, self.sort_binb_column)
        sort.set_sort_func(PackageListModel.COL_RCP, self.sort_column, PackageListModel.COL_RCP)
        return sort

    def sort_column_changed_cb (self, data):
        self.sort_column_id, self.sort_order = data.get_sort_column_id ()

    def sort_column(self, model, row1, row2, col):
        value1 = model.get_value(row1, col)
        value2 = model.get_value(row2, col)
        if col==PackageListModel.COL_SIZE:
            value1 = HobPage._string_to_size(value1)
            value2 = HobPage._string_to_size(value2)

        cmp_res = cmp(value1, value2)
        if cmp_res!=0:
            if col==PackageListModel.COL_INC:
                return -cmp_res
            else:
                return cmp_res
        else:
            name1 = model.get_value(row1, PackageListModel.COL_NAME)
            name2 = model.get_value(row2, PackageListModel.COL_NAME)
            return cmp(name1,name2)

    def sort_binb_column(self, model, row1, row2):
        value1 = model.get_value(row1, PackageListModel.COL_BINB)
        value2 = model.get_value(row2, PackageListModel.COL_BINB)
        value1_list = value1.split(', ')
        value2_list = value2.split(', ')

        value1 = value1_list[0]
        value2 = value2_list[0]

        cmp_res = cmp(value1, value2)
        if cmp_res==0:
            cmp_size = cmp(len(value1_list), len(value2_list))
            if cmp_size==0:
                name1 = model.get_value(row1, PackageListModel.COL_NAME)
                name2 = model.get_value(row2, PackageListModel.COL_NAME)
                return cmp(name1,name2)
            else:
                return cmp_size
        else:
            return cmp_res

    def exclude_item_sort_func(self, model, iter1, iter2, user_data=None):
        if user_data:
            val1 = model.get_value(iter1, PackageListModel.COL_NAME)
            val2 = model.get_value(iter2, PackageListModel.COL_NAME)
            return self.cmp_vals(val1, val2, user_data)
        else:
            val1 = model.get_value(iter1, PackageListModel.COL_FADE_INC)
            val2 = model.get_value(iter2, PackageListModel.COL_INC)
            return ((val1 == True) and (val2 == False))

    def include_item_sort_func(self, model, iter1, iter2, user_data=None):
        if user_data:
            val1 = model.get_value(iter1, PackageListModel.COL_NAME)
            val2 = model.get_value(iter2, PackageListModel.COL_NAME)
            return self.cmp_vals(val1, val2, user_data)
        else:
            val1 = model.get_value(iter1, PackageListModel.COL_INC)
            val2 = model.get_value(iter2, PackageListModel.COL_INC)
            return ((val1 == False) and (val2 == True))

    def sort_func(self, model, iter1, iter2, user_data):
        val1 = model.get_value(iter1, PackageListModel.COL_NAME)
        val2 = model.get_value(iter2, PackageListModel.COL_NAME)
        return self.cmp_vals(val1, val2, user_data)

    def cmp_vals(self, val1, val2, user_data):
        if val1 is None or val2 is None:
            return 0
        elif val1.startswith(user_data) and not val2.startswith(user_data):
            return -1
        elif not val1.startswith(user_data) and val2.startswith(user_data):
            return 1
        else:
            return cmp(val1, val2)

    def convert_vpath_to_path(self, view_model, view_path):
        # view_model is the model sorted
        # get the path of the model filtered
        filtered_model_path = view_model.convert_path_to_child_path(view_path)
        # get the model filtered
        filtered_model = view_model.get_model()
        # get the path of the original model
        path = filtered_model.convert_path_to_child_path(filtered_model_path)
        return path

    def convert_path_to_vpath(self, view_model, path):
        it = view_model.get_iter_first()
        while it:
            name = self.find_item_for_path(path)
            view_name = view_model.get_value(it, PackageListModel.COL_NAME)
            if view_name == name:
                view_path = view_model.get_path(it)
                return view_path
            it = view_model.iter_next(it)
        return None

    """
    The populate() function takes as input the data from a
    bb.event.PackageInfo event and populates the package list.
    """
    def populate(self, pkginfolist):
        # First clear the model, in case repopulating
        self.clear()

        def getpkgvalue(pkgdict, key, pkgname, defaultval = None):
            value = pkgdict.get('%s_%s' % (key, pkgname), None)
            if not value:
                value = pkgdict.get(key, defaultval)
            return value

        for pkginfo in pkginfolist:
            pn = pkginfo['PN']
            pv = pkginfo['PV']
            pr = pkginfo['PR']
            pkg = pkginfo['PKG']
            pkgv = getpkgvalue(pkginfo, 'PKGV', pkg)
            pkgr = getpkgvalue(pkginfo, 'PKGR', pkg)
            # PKGSIZE is artificial, will always be overridden with the package name if present
            pkgsize = int(pkginfo.get('PKGSIZE_%s' % pkg, "0"))
            # PKG_%s is the renamed version
            pkg_rename = pkginfo.get('PKG_%s' % pkg, "")
            # The rest may be overridden or not
            section = getpkgvalue(pkginfo, 'SECTION', pkg, "")
            summary = getpkgvalue(pkginfo, 'SUMMARY', pkg, "")
            rdep = getpkgvalue(pkginfo, 'RDEPENDS', pkg, "")
            rrec = getpkgvalue(pkginfo, 'RRECOMMENDS', pkg, "")
            rprov = getpkgvalue(pkginfo, 'RPROVIDES', pkg, "")
            files_list = getpkgvalue(pkginfo, 'FILES_INFO', pkg, "")
            for i in rprov.split():
                self.rprov_pkg[i] = pkg

            recipe = pn + '-' + pv + '-' + pr

            allow_empty = getpkgvalue(pkginfo, 'ALLOW_EMPTY', pkg, "")

            if pkgsize == 0 and not allow_empty:
                continue

            size = HobPage._size_to_string(pkgsize)
            self.set(self.append(), self.COL_NAME, pkg, self.COL_VER, pkgv,
                     self.COL_REV, pkgr, self.COL_RNM, pkg_rename,
                     self.COL_SEC, section, self.COL_SUM, summary,
                     self.COL_RDEP, rdep + ' ' + rrec,
                     self.COL_RPROV, rprov, self.COL_SIZE, size,
                     self.COL_RCP, recipe, self.COL_BINB, "",
                     self.COL_INC, False, self.COL_FONT, '10', self.COL_FLIST, files_list)

        self.pn_path = {}
        it = self.get_iter_first()
        while it:
            pn = self.get_value(it, self.COL_NAME)
            path = self.get_path(it)
            self.pn_path[pn] = path
            it = self.iter_next(it)

    """
    Update the model, send out the notification.
    """
    def selection_change_notification(self):
        self.emit("package-selection-changed")

    """
    Check whether the item at item_path is included or not
    """
    def path_included(self, item_path):
        return self[item_path][self.COL_INC]

    """
    Add this item, and any of its dependencies, to the image contents
    """
    def include_item(self, item_path, binb=""):
        if self.path_included(item_path):
            return

        item_name = self[item_path][self.COL_NAME]
        item_deps = self[item_path][self.COL_RDEP]

        self[item_path][self.COL_INC] = True

        item_bin = self[item_path][self.COL_BINB].split(', ')
        if binb and not binb in item_bin:
            item_bin.append(binb)
            self[item_path][self.COL_BINB] = ', '.join(item_bin).lstrip(', ')

        if item_deps:
            # Ensure all of the items deps are included and, where appropriate,
            # add this item to their COL_BINB
            for dep in item_deps.split(" "):
                if dep.startswith('('):
                    continue
                # If the contents model doesn't already contain dep, add it
                dep_path = self.find_path_for_item(dep)
                if not dep_path:
                    continue
                dep_included = self.path_included(dep_path)

                if dep_included and not dep in item_bin:
                    # don't set the COL_BINB to this item if the target is an
                    # item in our own COL_BINB
                    dep_bin = self[dep_path][self.COL_BINB].split(', ')
                    if not item_name in dep_bin:
                        dep_bin.append(item_name)
                        self[dep_path][self.COL_BINB] = ', '.join(dep_bin).lstrip(', ')
                elif not dep_included:
                    self.include_item(dep_path, binb=item_name)

    def exclude_item(self, item_path):
        if not self.path_included(item_path):
            return

        self[item_path][self.COL_INC] = False

        item_name = self[item_path][self.COL_NAME]
        item_deps = self[item_path][self.COL_RDEP]
        if item_deps:
            for dep in item_deps.split(" "):
                if dep.startswith('('):
                    continue
                dep_path = self.find_path_for_item(dep)
                if not dep_path:
                    continue
                dep_bin = self[dep_path][self.COL_BINB].split(', ')
                if item_name in dep_bin:
                    dep_bin.remove(item_name)
                    self[dep_path][self.COL_BINB] = ', '.join(dep_bin).lstrip(', ')

        item_bin = self[item_path][self.COL_BINB].split(', ')
        if item_bin:
            for binb in item_bin:
                binb_path = self.find_path_for_item(binb)
                if not binb_path:
                    continue
                self.exclude_item(binb_path)

    """
    Empty self.contents by setting the include of each entry to None
    """
    def reset(self):
        it = self.get_iter_first()
        while it:
            self.set(it,
                     self.COL_INC, False,
                     self.COL_BINB, "")
            it = self.iter_next(it)

        self.selection_change_notification()

    def get_selected_packages(self):
        packagelist = []

        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                name = self.get_value(it, self.COL_NAME)
                packagelist.append(name)
            it = self.iter_next(it)

        return packagelist

    def get_user_selected_packages(self):
        packagelist = []

        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                binb = self.get_value(it, self.COL_BINB)
                if binb == "User Selected":
                    name = self.get_value(it, self.COL_NAME)
                    packagelist.append(name)
            it = self.iter_next(it)

        return packagelist

    def get_selected_packages_toolchain(self):
        packagelist = []

        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                name = self.get_value(it, self.COL_NAME)
                if name.endswith("-dev") or name.endswith("-dbg"):
                    packagelist.append(name)
            it = self.iter_next(it)

        return list(set(packagelist + self.__toolchain_required_packages__));

    """
    Package model may be incomplete, therefore when calling the
    set_selected_packages(), some packages will not be set included.
    Return the un-set packages list.
    """
    def set_selected_packages(self, packagelist, user_selected=False):
        left = []
        binb = 'User Selected' if user_selected else ''
        for pn in packagelist:
            if pn in self.pn_path.keys():
                path = self.pn_path[pn]
                self.include_item(item_path=path, binb=binb)
            else:
                left.append(pn)

        self.selection_change_notification()
        return left

    """
    Return the selected package size, unit is B.
    """
    def get_packages_size(self):
        packages_size = 0
        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                str_size = self.get_value(it, self.COL_SIZE)
                if not str_size:
                    continue

                packages_size += HobPage._string_to_size(str_size)

            it = self.iter_next(it)
        return packages_size

    """
    Resync the state of included items to a backup column before performing the fadeout visible effect
    """
    def resync_fadeout_column(self, model_first_iter=None):
        it = model_first_iter
        while it:
            active = self.get_value(it, self.COL_INC)
            self.set(it, self.COL_FADE_INC, active)
            it = self.iter_next(it)

#
# RecipeListModel
#
class RecipeListModel(gtk.ListStore):
    """
    This class defines an gtk.ListStore subclass which will convert the output
    of the bb.event.TargetsTreeGenerated event into a gtk.ListStore whilst also
    providing convenience functions to access gtk.TreeModel subclasses which
    provide filtered views of the data.
    """
    (COL_NAME, COL_DESC, COL_LIC, COL_GROUP, COL_DEPS, COL_BINB, COL_TYPE, COL_INC, COL_IMG, COL_INSTALL, COL_PN, COL_FADE_INC, COL_SUMMARY, COL_VERSION,
     COL_REVISION, COL_HOMEPAGE, COL_BUGTRACKER, COL_FILE) = range(18)

    __custom_image__ = "Start with an empty image recipe"

    __gsignals__ = {
        "recipe-selection-changed" : (gobject.SIGNAL_RUN_LAST,
                                  gobject.TYPE_NONE,
                                 ()),
        }

    """
    """
    def __init__(self):
        gtk.ListStore.__init__ (self,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_BOOLEAN,
                                gobject.TYPE_BOOLEAN,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_BOOLEAN,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING,
                                gobject.TYPE_STRING)
        self.sort_column_id, self.sort_order = RecipeListModel.COL_NAME, gtk.SORT_ASCENDING

    """
    Find the model path for the item_name
    Returns the path in the model or None
    """
    def find_path_for_item(self, item_name):
        if self.non_target_name(item_name) or item_name not in self.pn_path.keys():
            return None
        else:
            return self.pn_path[item_name]

    def find_item_for_path(self, item_path):
        return self[item_path][self.COL_NAME]

    """
    Helper method to determine whether name is a target pn
    """
    def non_target_name(self, name):
        if name and ('-native' in name):
            return True
        return False

    """
    Helper function to determine whether an item is an item specified by filter
    """
    def tree_model_filter(self, model, it, filter):
        name = model.get_value(it, self.COL_NAME)
        if self.non_target_name(name):
            return False

        for key in filter.keys():
            if key == self.COL_NAME:
                if filter[key] != 'Search recipes by name' and filter[key] != 'Search package groups by name':
                    if filter[key] not in name:
                        return False
            else:
                if model.get_value(it, key) not in filter[key]:
                    return False
        self.filtered_nb += 1

        return True

    def exclude_item_sort_func(self, model, iter1, iter2, user_data=None):
        if user_data:
            val1 = model.get_value(iter1, RecipeListModel.COL_NAME)
            val2 = model.get_value(iter2, RecipeListModel.COL_NAME)
            return self.cmp_vals(val1, val2, user_data)
        else:
            val1 = model.get_value(iter1, RecipeListModel.COL_FADE_INC)
            val2 = model.get_value(iter2, RecipeListModel.COL_INC)
            return ((val1 == True) and (val2 == False))

    def include_item_sort_func(self, model, iter1, iter2, user_data=None):
        if user_data:
            val1 = model.get_value(iter1, RecipeListModel.COL_NAME)
            val2 = model.get_value(iter2, RecipeListModel.COL_NAME)
            return self.cmp_vals(val1, val2, user_data)
        else:
            val1 = model.get_value(iter1, RecipeListModel.COL_INC)
            val2 = model.get_value(iter2, RecipeListModel.COL_INC)
            return ((val1 == False) and (val2 == True))

    def sort_func(self, model, iter1, iter2, user_data):
        val1 = model.get_value(iter1, RecipeListModel.COL_NAME)
        val2 = model.get_value(iter2, RecipeListModel.COL_NAME)
        return self.cmp_vals(val1, val2, user_data)

    def cmp_vals(self, val1, val2, user_data):
        if val1 is None or val2 is None:
            return 0
        elif val1.startswith(user_data) and not val2.startswith(user_data):
            return -1
        elif not val1.startswith(user_data) and val2.startswith(user_data):
            return 1
        else:
            return cmp(val1, val2)

    """
    Create, if required, and return a filtered gtk.TreeModelSort
    containing only the items specified by filter
    """
    def tree_model(self, filter, excluded_items_ahead=False, included_items_ahead=False, search_data=None, initial=False):
        model = self.filter_new()
        self.filtered_nb = 0
        model.set_visible_func(self.tree_model_filter, filter)

        sort = gtk.TreeModelSort(model)
        sort.connect ('sort-column-changed', self.sort_column_changed_cb)
        if initial:
            sort.set_sort_column_id(RecipeListModel.COL_NAME, gtk.SORT_ASCENDING)
            sort.set_default_sort_func(None)
        elif excluded_items_ahead:
            sort.set_default_sort_func(self.exclude_item_sort_func, search_data)
        elif included_items_ahead:
            sort.set_default_sort_func(self.include_item_sort_func, search_data)
        else:
            if search_data and search_data!='Search recipes by name' and search_data!='Search package groups by name':
                sort.set_default_sort_func(self.sort_func, search_data)
            else:
                sort.set_sort_column_id(self.sort_column_id, self.sort_order)
                sort.set_default_sort_func(None)

        sort.set_sort_func(RecipeListModel.COL_INC, self.sort_column, RecipeListModel.COL_INC)
        sort.set_sort_func(RecipeListModel.COL_GROUP, self.sort_column, RecipeListModel.COL_GROUP)
        sort.set_sort_func(RecipeListModel.COL_BINB, self.sort_binb_column)
        sort.set_sort_func(RecipeListModel.COL_LIC, self.sort_column, RecipeListModel.COL_LIC)
        return sort

    def sort_column_changed_cb (self, data):
        self.sort_column_id, self.sort_order = data.get_sort_column_id ()

    def sort_column(self, model, row1, row2, col):
        value1 = model.get_value(row1, col)
        value2 = model.get_value(row2, col)
        cmp_res = cmp(value1, value2)
        if cmp_res!=0:
            if col==RecipeListModel.COL_INC:
                return -cmp_res
            else:
                return cmp_res
        else:
            name1 = model.get_value(row1, RecipeListModel.COL_NAME)
            name2 = model.get_value(row2, RecipeListModel.COL_NAME)
            return cmp(name1,name2)

    def sort_binb_column(self, model, row1, row2):
        value1 = model.get_value(row1, RecipeListModel.COL_BINB)
        value2 = model.get_value(row2, RecipeListModel.COL_BINB)
        value1_list = value1.split(', ')
        value2_list = value2.split(', ')

        value1 = value1_list[0]
        value2 = value2_list[0]

        cmp_res = cmp(value1, value2)
        if cmp_res==0:
            cmp_size = cmp(len(value1_list), len(value2_list))
            if cmp_size==0:
                name1 = model.get_value(row1, RecipeListModel.COL_NAME)
                name2 = model.get_value(row2, RecipeListModel.COL_NAME)
                return cmp(name1,name2)
            else:
                return cmp_size
        else:
            return cmp_res

    def convert_vpath_to_path(self, view_model, view_path):
        filtered_model_path = view_model.convert_path_to_child_path(view_path)
        filtered_model = view_model.get_model()

        # get the path of the original model
        path = filtered_model.convert_path_to_child_path(filtered_model_path)
        return path

    def convert_path_to_vpath(self, view_model, path):
        it = view_model.get_iter_first()
        while it:
            name = self.find_item_for_path(path)
            view_name = view_model.get_value(it, RecipeListModel.COL_NAME)
            if view_name == name:
                view_path = view_model.get_path(it)
                return view_path
            it = view_model.iter_next(it)
        return None

    """
    The populate() function takes as input the data from a
    bb.event.TargetsTreeGenerated event and populates the RecipeList.
    """
    def populate(self, event_model):
        # First clear the model, in case repopulating
        self.clear()

        # dummy image for prompt
        self.set_in_list(self.__custom_image__,  "Use 'Edit image recipe' to customize recipes and packages " \
                                "to be included in your image ")

        for item in event_model["pn"]:
            name = item
            desc = event_model["pn"][item]["description"]
            lic = event_model["pn"][item]["license"]
            group = event_model["pn"][item]["section"]
            inherits = event_model["pn"][item]["inherits"]
            summary = event_model["pn"][item]["summary"]
            version = event_model["pn"][item]["version"]
            revision = event_model["pn"][item]["prevision"]
            homepage = event_model["pn"][item]["homepage"]
            bugtracker = event_model["pn"][item]["bugtracker"]
            filename = event_model["pn"][item]["filename"]
            install = []

            depends = event_model["depends"].get(item, []) + event_model["rdepends-pn"].get(item, [])

            if ('packagegroup.bbclass' in " ".join(inherits)):
                atype = 'packagegroup'
            elif ('/image.bbclass' in " ".join(inherits)):
                if "edited" not in name:
                    atype = 'image'
                    install = event_model["rdepends-pkg"].get(item, []) + event_model["rrecs-pkg"].get(item, [])
            elif ('meta-' in name):
                atype = 'toolchain'
            elif (name == 'dummy-image' or name == 'dummy-toolchain'):
                atype = 'dummy'
            else:
                atype = 'recipe'

            self.set(self.append(), self.COL_NAME, item, self.COL_DESC, desc,
                     self.COL_LIC, lic, self.COL_GROUP, group,
                     self.COL_DEPS, " ".join(depends), self.COL_BINB, "",
                     self.COL_TYPE, atype, self.COL_INC, False,
                     self.COL_IMG, False, self.COL_INSTALL, " ".join(install), self.COL_PN, item,
                     self.COL_SUMMARY, summary, self.COL_VERSION, version, self.COL_REVISION, revision,
                     self.COL_HOMEPAGE, homepage, self.COL_BUGTRACKER, bugtracker,
                     self.COL_FILE, filename)

        self.pn_path = {}
        it = self.get_iter_first()
        while it:
            pn = self.get_value(it, self.COL_NAME)
            path = self.get_path(it)
            self.pn_path[pn] = path
            it = self.iter_next(it)

    def set_in_list(self, item, desc):
        self.set(self.append(), self.COL_NAME, item,
                 self.COL_DESC, desc,
                 self.COL_LIC, "", self.COL_GROUP, "",
                 self.COL_DEPS, "", self.COL_BINB, "",
                 self.COL_TYPE, "image", self.COL_INC, False,
                 self.COL_IMG, False, self.COL_INSTALL, "", self.COL_PN, item,
                 self.COL_SUMMARY, "", self.COL_VERSION, "", self.COL_REVISION, "",
                 self.COL_HOMEPAGE, "", self.COL_BUGTRACKER, "")
        self.pn_path = {}
        it = self.get_iter_first()
        while it:
            pn = self.get_value(it, self.COL_NAME)
            path = self.get_path(it)
            self.pn_path[pn] = path
            it = self.iter_next(it)

    """
    Update the model, send out the notification.
    """
    def selection_change_notification(self):
        self.emit("recipe-selection-changed")

    def path_included(self, item_path):
        return self[item_path][self.COL_INC]

    """
    Add this item, and any of its dependencies, to the image contents
    """
    def include_item(self, item_path, binb="", image_contents=False):
        if self.path_included(item_path):
            return

        item_name = self[item_path][self.COL_NAME]
        item_deps = self[item_path][self.COL_DEPS]

        self[item_path][self.COL_INC] = True

        item_bin = self[item_path][self.COL_BINB].split(', ')
        if binb and not binb in item_bin:
            item_bin.append(binb)
            self[item_path][self.COL_BINB] = ', '.join(item_bin).lstrip(', ')

        # We want to do some magic with things which are brought in by the
        # base image so tag them as so
        if image_contents:
            self[item_path][self.COL_IMG] = True

        if item_deps:
            # Ensure all of the items deps are included and, where appropriate,
            # add this item to their COL_BINB
            for dep in item_deps.split(" "):
                # If the contents model doesn't already contain dep, add it
                dep_path = self.find_path_for_item(dep)
                if not dep_path:
                    continue
                dep_included = self.path_included(dep_path)

                if dep_included and not dep in item_bin:
                    # don't set the COL_BINB to this item if the target is an
                    # item in our own COL_BINB
                    dep_bin = self[dep_path][self.COL_BINB].split(', ')
                    if not item_name in dep_bin:
                        dep_bin.append(item_name)
                        self[dep_path][self.COL_BINB] = ', '.join(dep_bin).lstrip(', ')
                elif not dep_included:
                    self.include_item(dep_path, binb=item_name, image_contents=image_contents)
        dep_bin = self[item_path][self.COL_BINB].split(', ')
        if self[item_path][self.COL_NAME] in dep_bin:
            dep_bin.remove(self[item_path][self.COL_NAME])
        self[item_path][self.COL_BINB] = ', '.join(dep_bin).lstrip(', ')

    def exclude_item(self, item_path):
        if not self.path_included(item_path):
            return

        self[item_path][self.COL_INC] = False

        item_name = self[item_path][self.COL_NAME]
        item_deps = self[item_path][self.COL_DEPS]
        if item_deps:
            for dep in item_deps.split(" "):
                dep_path = self.find_path_for_item(dep)
                if not dep_path:
                    continue
                dep_bin = self[dep_path][self.COL_BINB].split(', ')
                if item_name in dep_bin:
                    dep_bin.remove(item_name)
                    self[dep_path][self.COL_BINB] = ', '.join(dep_bin).lstrip(', ')

        item_bin = self[item_path][self.COL_BINB].split(', ')
        if item_bin:
            for binb in item_bin:
                binb_path = self.find_path_for_item(binb)
                if not binb_path:
                    continue
                self.exclude_item(binb_path)

    def reset(self):
        it = self.get_iter_first()
        while it:
            self.set(it,
                     self.COL_INC, False,
                     self.COL_BINB, "",
                     self.COL_IMG, False)
            it = self.iter_next(it)

        self.selection_change_notification()

    """
    Returns two lists. One of user selected recipes and the other containing
    all selected recipes
    """
    def get_selected_recipes(self):
        allrecipes = []
        userrecipes = []

        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                name = self.get_value(it, self.COL_PN)
                type = self.get_value(it, self.COL_TYPE)
                if type != "image":
                    allrecipes.append(name)
                    sel = "User Selected" in self.get_value(it, self.COL_BINB)
                    if sel:
                        userrecipes.append(name)
            it = self.iter_next(it)

        return list(set(userrecipes)), list(set(allrecipes))

    def set_selected_recipes(self, recipelist):
        for pn in recipelist:
            if pn in self.pn_path.keys():
                path = self.pn_path[pn]
                self.include_item(item_path=path,
                                  binb="User Selected")
        self.selection_change_notification()

    def get_selected_image(self):
        it = self.get_iter_first()
        while it:
            if self.get_value(it, self.COL_INC):
                name = self.get_value(it, self.COL_PN)
                type = self.get_value(it, self.COL_TYPE)
                if type == "image":
                    sel = "User Selected" in self.get_value(it, self.COL_BINB)
                    if sel:
                        return name
            it = self.iter_next(it)
        return None

    def set_selected_image(self, img):
        if not img:
            return
        self.reset()
        path = self.find_path_for_item(img)
        self.include_item(item_path=path,
                          binb="User Selected",
                          image_contents=True)
        self.selection_change_notification()

    def set_custom_image_version(self, version):
        self.custom_image_version = version

    def get_custom_image_version(self):
        return self.custom_image_version

    def is_custom_image(self):
        return self.get_selected_image() == self.__custom_image__
