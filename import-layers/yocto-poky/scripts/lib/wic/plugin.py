#!/usr/bin/env python -tt
#
# Copyright (c) 2011 Intel, Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.

import os, sys

from wic import msger
from wic import pluginbase
from wic.utils import errors
from wic.utils.oe.misc import get_bitbake_var

__ALL__ = ['PluginMgr', 'pluginmgr']

PLUGIN_TYPES = ["imager", "source"]

PLUGIN_DIR = "/lib/wic/plugins" # relative to scripts
SCRIPTS_PLUGIN_DIR = "scripts" + PLUGIN_DIR

class PluginMgr():
    plugin_dirs = {}

    # make the manager class as singleton
    _instance = None
    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(PluginMgr, cls).__new__(cls, *args, **kwargs)

        return cls._instance

    def __init__(self):
        wic_path = os.path.dirname(__file__)
        eos = wic_path.rfind('scripts') + len('scripts')
        scripts_path = wic_path[:eos]
        self.scripts_path = scripts_path
        self.plugin_dir = scripts_path + PLUGIN_DIR
        self.layers_path = None

    def _build_plugin_dir_list(self, plugin_dir, ptype):
        if self.layers_path is None:
            self.layers_path = get_bitbake_var("BBLAYERS")
        layer_dirs = []

        if self.layers_path is not None:
            for layer_path in self.layers_path.split():
                path = os.path.join(layer_path, SCRIPTS_PLUGIN_DIR, ptype)
                layer_dirs.append(path)

        path = os.path.join(plugin_dir, ptype)
        layer_dirs.append(path)

        return layer_dirs

    def append_dirs(self, dirs):
        for path in dirs:
            self._add_plugindir(path)

        # load all the plugins AGAIN
        self._load_all()

    def _add_plugindir(self, path):
        path = os.path.abspath(os.path.expanduser(path))

        if not os.path.isdir(path):
            return

        if path not in self.plugin_dirs:
            self.plugin_dirs[path] = False
            # the value True/False means "loaded"

    def _load_all(self):
        for (pdir, loaded) in self.plugin_dirs.items():
            if loaded:
                continue

            sys.path.insert(0, pdir)
            for mod in [x[:-3] for x in os.listdir(pdir) if x.endswith(".py")]:
                if mod and mod != '__init__':
                    if mod in sys.modules:
                        #self.plugin_dirs[pdir] = True
                        msger.warning("Module %s already exists, skip" % mod)
                    else:
                        try:
                            pymod = __import__(mod)
                            self.plugin_dirs[pdir] = True
                            msger.debug("Plugin module %s:%s imported"\
                                        % (mod, pymod.__file__))
                        except ImportError as err:
                            msg = 'Failed to load plugin %s/%s: %s' \
                                % (os.path.basename(pdir), mod, err)
                            msger.warning(msg)

            del sys.path[0]

    def get_plugins(self, ptype):
        """ the return value is dict of name:class pairs """

        if ptype not in PLUGIN_TYPES:
            raise errors.CreatorError('%s is not valid plugin type' % ptype)

        plugins_dir = self._build_plugin_dir_list(self.plugin_dir, ptype)

        self.append_dirs(plugins_dir)

        return pluginbase.get_plugins(ptype)

    def get_source_plugins(self):
        """
        Return list of available source plugins.
        """
        plugins_dir = self._build_plugin_dir_list(self.plugin_dir, 'source')

        self.append_dirs(plugins_dir)

        return self.get_plugins('source')


    def get_source_plugin_methods(self, source_name, methods):
        """
        The methods param is a dict with the method names to find.  On
        return, the dict values will be filled in with pointers to the
        corresponding methods.  If one or more methods are not found,
        None is returned.
        """
        return_methods = None
        for _source_name, klass in self.get_plugins('source').items():
            if _source_name == source_name:
                for _method_name in methods:
                    if not hasattr(klass, _method_name):
                        msger.warning("Unimplemented %s source interface for: %s"\
                                      % (_method_name, _source_name))
                        return None
                    func = getattr(klass, _method_name)
                    methods[_method_name] = func
                    return_methods = methods
        return return_methods

pluginmgr = PluginMgr()
