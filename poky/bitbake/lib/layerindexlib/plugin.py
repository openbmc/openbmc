# Copyright (C) 2016-2018 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# The file contains:
#   LayerIndex exceptions
#   Plugin base class
#   Utility Functions for working on layerindex data

import logging

logger = logging.getLogger('BitBake.layerindexlib.plugin')

class LayerIndexPluginException(Exception):
    """LayerIndex Generic Exception"""
    def __init__(self, message):
         self.msg = message
         Exception.__init__(self, message)

    def __str__(self):
         return self.msg

class LayerIndexPluginUrlError(LayerIndexPluginException):
    """Exception raised when a plugin does not support a given URL type"""
    def __init__(self, plugin, url):
        msg = "%s does not support %s:" % (plugin, url)
        self.plugin = plugin
        self.url = url
        LayerIndexPluginException.__init__(self, msg)

class IndexPlugin():
    def __init__(self):
        self.type = None

    def init(self, layerindex):
        self.layerindex = layerindex

    def plugin_type(self):
        return self.type

    def load_index(self, uri):
        raise NotImplementedError('load_index is not implemented')

    def store_index(self, uri, index):
        raise NotImplementedError('store_index is not implemented')

