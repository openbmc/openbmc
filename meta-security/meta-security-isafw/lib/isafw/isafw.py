#
# isafw.py - Main classes for ISA FW
#
# Copyright (c) 2015 - 2016, Intel Corporation
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
#    * Redistributions of source code must retain the above copyright notice,
#      this list of conditions and the following disclaimer.
#    * Redistributions in binary form must reproduce the above copyright
#      notice, this list of conditions and the following disclaimer in the
#      documentation and/or other materials provided with the distribution.
#    * Neither the name of Intel Corporation nor the names of its contributors
#      may be used to endorse or promote products derived from this software
#      without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

from __future__ import absolute_import, print_function

import sys
import traceback
try:
    # absolute import
    import isafw.isaplugins as isaplugins
except ImportError:
    # relative import when installing as separate modules
    import isaplugins
try:
    from bb import error
except ImportError:
    error = print

__all__ = [
    'ISA_package',
    'ISA_pkg_list',
    'ISA_kernel',
    'ISA_filesystem',
    'ISA_config',
    'ISA',
]

# classes for representing objects for ISA plugins

# source package


class ISA_package:
    # pkg name                            (mandatory argument)
    name = ""
    # full version                        (mandatory argument)
    version = ""
    licenses = []                 # list of licences for all subpackages
    aliases = []                  # list of alias names for packages if exist
    source_files = []             # list of strings of source files
    patch_files = []              # list of patch files to be applied
    path_to_sources = ""          # path to the source files

# package list


class ISA_pkg_list:
    # image name                            (mandatory argument)
    img_name = ""
    # path to the pkg list file             (mandatory argument)
    path_to_list = ""

# kernel


class ISA_kernel:
    # image name                          (mandatory argument)
    img_name = ""
    # path to the kernel config file      (mandatory argument)
    path_to_config = ""

# filesystem


class ISA_filesystem:
    # image name                          (mandatory argument)
    img_name = ""
    type = ""                     # filesystem type
    # path to the fs location             (mandatory argument)
    path_to_fs = ""

# configuration of ISAFW
# if both whitelist and blacklist is empty, all avaliable plugins will be used
# if whitelist has entries, then only whitelisted plugins will be used from a set of avaliable plugins
# if blacklist has entries, then the specified plugins won't be used even
# if avaliable and even if specified in whitelist


class ISA_config:
    plugin_whitelist = ""         # comma separated list of plugins to whitelist
    plugin_blacklist = ""         # comma separated list of plugins to blacklist
    cacert = None                 # If set, a CA certificate file that replaces the system default one
    reportdir = ""                # location of produced reports
    logdir = ""                   # location of produced logs
    timestamp = ""                # timestamp of the build provided by build system
    full_reports = False          # produce full reports for plugins, False by default
    machine = ""                  # name of machine build is produced for
    la_plugin_image_whitelist = ""# whitelist of images for violating license checks
    la_plugin_image_blacklist = ""# blacklist of images for violating license checks
    arch = ""                     # target architecture

class ISA:
    def call_plugins(self, methodname, *parameters, **keywords):
        for name in isaplugins.__all__:
            plugin = getattr(isaplugins, name)
            method = getattr(plugin, methodname, None)
            if not method:
                # Not having init() is an error, everything else is optional.
                if methodname == "init":
                    error("No init() defined for plugin %s.\n"
                          "Skipping this plugin." %
                          (methodname, plugin.getPluginName()))
                continue
            if self.ISA_config.plugin_whitelist and plugin.getPluginName() not in self.ISA_config.plugin_whitelist:
                continue
            if self.ISA_config.plugin_blacklist and plugin.getPluginName() in self.ISA_config.plugin_blacklist:
                continue
            try:
                method(*parameters, **keywords)
            except:
                error("Exception in plugin %s %s():\n%s" %
                      (plugin.getPluginName(),
                       methodname,
                       traceback.format_exc()))

    def __init__(self, ISA_config):
        self.ISA_config = ISA_config
        self.call_plugins("init", ISA_config)

    def process_package(self, ISA_package):
        self.call_plugins("process_package", ISA_package)

    def process_pkg_list(self, ISA_pkg_list):
        self.call_plugins("process_pkg_list", ISA_pkg_list)

    def process_kernel(self, ISA_kernel):
        self.call_plugins("process_kernel", ISA_kernel)

    def process_filesystem(self, ISA_filesystem):
        self.call_plugins("process_filesystem", ISA_filesystem)

    def process_report(self):
        self.call_plugins("process_report")
