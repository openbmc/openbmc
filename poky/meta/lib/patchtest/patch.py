# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# patchtestpatch: PatchTestPatch class which abstracts a patch file
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import utils

logger = logging.getLogger('patchtest')

class PatchTestPatch(object):
    def __init__(self, path, forcereload=False):
        self._path = path
        self._forcereload = forcereload

        self._contents = None
        self._branch = None

    @property
    def contents(self):
        if self._forcereload or (not self._contents):
            logger.debug('Reading %s contents' % self._path)
            try:
                with open(self._path, newline='') as _f:
                    self._contents = _f.read()
            except IOError:
                logger.warn("Reading the mbox %s failed" % self.resource)
        return self._contents

    @property
    def path(self):
        return self._path

    @property
    def branch(self):
        if not self._branch:
            self._branch = utils.get_branch(self._path)
        return self._branch
