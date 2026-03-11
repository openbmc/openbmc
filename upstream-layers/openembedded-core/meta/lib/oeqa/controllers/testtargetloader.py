#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import types
import bb
import os

# This class is responsible for loading a test target controller
class TestTargetLoader:

    # Search oeqa.controllers module directory for and return a controller  
    # corresponding to the given target name. 
    # AttributeError raised if not found.
    # ImportError raised if a provided module can not be imported.
    def get_controller_module(self, target, bbpath):
        controllerslist = self.get_controller_modulenames(bbpath)
        bb.note("Available controller modules: %s" % str(controllerslist))
        controller = self.load_controller_from_name(target, controllerslist)
        return controller

    # Return a list of all python modules in lib/oeqa/controllers for each
    # layer in bbpath
    def get_controller_modulenames(self, bbpath):

        controllerslist = []

        def add_controller_list(path):
            if not os.path.exists(os.path.join(path, '__init__.py')):
                bb.fatal('Controllers directory %s exists but is missing __init__.py' % path)
            files = sorted([f for f in os.listdir(path) if f.endswith('.py') and not f.startswith('_')])
            for f in files:
                module = 'oeqa.controllers.' + f[:-3]
                if module not in controllerslist:
                    controllerslist.append(module)
                else:
                    bb.warn("Duplicate controller module found for %s, only one added. Layers should create unique controller module names" % module)

        for p in bbpath:
            controllerpath = os.path.join(p, 'lib', 'oeqa', 'controllers')
            bb.debug(2, 'Searching for target controllers in %s' % controllerpath)
            if os.path.exists(controllerpath):
                add_controller_list(controllerpath)
        return controllerslist

    # Search for and return a controller from given target name and
    # set of module names. 
    # Raise AttributeError if not found.
    # Raise ImportError if a provided module can not be imported
    def load_controller_from_name(self, target, modulenames):
        for name in modulenames:
            obj = self.load_controller_from_module(target, name)
            if obj:
                return obj
        raise AttributeError("Unable to load {0} from available modules: {1}".format(target, str(modulenames)))

    # Search for and return a controller or None from given module name
    def load_controller_from_module(self, target, modulename):
        obj = None
        # import module, allowing it to raise import exception
        module = __import__(modulename, globals(), locals(), [target])
        # look for target class in the module, catching any exceptions as it
        # is valid that a module may not have the target class.
        try:
            obj = getattr(module, target)
            if obj: 
                from oeqa.targetcontrol import BaseTarget
                if( not issubclass(obj, BaseTarget)):
                    bb.warn("Target {0} found, but subclass is not BaseTarget".format(target))
        except:
            obj = None
        return obj
