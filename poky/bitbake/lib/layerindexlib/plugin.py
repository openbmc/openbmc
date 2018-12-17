# Copyright (C) 2016-2018 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

# The file contains:
#   LayerIndex exceptions
#   Plugin base class
#   Utility Functions for working on layerindex data

import argparse
import logging
import os
import bb.msg

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

