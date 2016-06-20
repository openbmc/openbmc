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

import os

from wic.ksparser import KickStart, KickStartError
from wic import msger
from wic.utils import misc


def get_siteconf():
    wic_path = os.path.dirname(__file__)
    eos = wic_path.find('scripts') + len('scripts')
    scripts_path = wic_path[:eos]

    return scripts_path + "/lib/image/config/wic.conf"

class ConfigMgr(object):
    DEFAULTS = {
        'common': {
            "distro_name": "Default Distribution",
            "plugin_dir": "/usr/lib/wic/plugins"}, # TODO use prefix also?
        'create': {
            "tmpdir": '/var/tmp/wic',
            "outdir": './wic-output',
            "release": None,
            "logfile": None,
            "name_prefix": None,
            "name_suffix": None}
        }

    # make the manager class as singleton
    _instance = None
    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(ConfigMgr, cls).__new__(cls, *args, **kwargs)

        return cls._instance

    def __init__(self, ksconf=None, siteconf=None):
        # reset config options
        self.reset()

        if not siteconf:
            siteconf = get_siteconf()

        # initial options from siteconf
        self._siteconf = siteconf

        if ksconf:
            self._ksconf = ksconf

    def reset(self):
        self.__ksconf = None
        self.__siteconf = None
        self.create = {}

        # initialize the values with defaults
        for sec, vals in self.DEFAULTS.iteritems():
            setattr(self, sec, vals)

    def __set_ksconf(self, ksconf):
        if not os.path.isfile(ksconf):
            msger.error('Cannot find ks file: %s' % ksconf)

        self.__ksconf = ksconf
        self._parse_kickstart(ksconf)
    def __get_ksconf(self):
        return self.__ksconf
    _ksconf = property(__get_ksconf, __set_ksconf)

    def _parse_kickstart(self, ksconf=None):
        if not ksconf:
            return

        try:
            ksobj = KickStart(ksconf)
        except KickStartError as err:
            msger.error(str(err))

        self.create['ks'] = ksobj
        self.create['name'] = os.path.splitext(os.path.basename(ksconf))[0]

        self.create['name'] = misc.build_name(ksconf,
                                              self.create['release'],
                                              self.create['name_prefix'],
                                              self.create['name_suffix'])

configmgr = ConfigMgr()
