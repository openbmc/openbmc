# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementation for crates.io
"""

# Copyright (C) 2016 Doug Goldstein
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import hashlib
import json
import os
import subprocess
import bb
from   bb.fetch2 import logger, subprocess_setup, UnpackError
from   bb.fetch2.wget import Wget


class Crate(Wget):

    """Class to fetch crates via wget"""

    def _cargo_bitbake_path(self, rootdir):
        return os.path.join(rootdir, "cargo_home", "bitbake")

    def supports(self, ud, d):
        """
        Check to see if a given url is for this fetcher
        """
        return ud.type in ['crate']

    def recommends_checksum(self, urldata):
        return True

    def urldata_init(self, ud, d):
        """
        Sets up to download the respective crate from crates.io
        """

        if ud.type == 'crate':
            self._crate_urldata_init(ud, d)

        super(Crate, self).urldata_init(ud, d)

    def _crate_urldata_init(self, ud, d):
        """
        Sets up the download for a crate
        """

        # URL syntax is: crate://NAME/VERSION
        # break the URL apart by /
        parts = ud.url.split('/')
        if len(parts) < 5:
            raise bb.fetch2.ParameterError("Invalid URL: Must be crate://HOST/NAME/VERSION", ud.url)

        # version is expected to be the last token
        # but ignore possible url parameters which will be used
        # by the top fetcher class
        version = parts[-1].split(";")[0]
        # second to last field is name
        name = parts[-2]
        # host (this is to allow custom crate registries to be specified
        host = '/'.join(parts[2:-2])

        # if using upstream just fix it up nicely
        if host == 'crates.io':
            host = 'crates.io/api/v1/crates'

        ud.url = "https://%s/%s/%s/download" % (host, name, version)
        ud.parm['downloadfilename'] = "%s-%s.crate" % (name, version)
        if 'name' not in ud.parm:
            ud.parm['name'] = '%s-%s' % (name, version)

        logger.debug2("Fetching %s to %s" % (ud.url, ud.parm['downloadfilename']))

    def unpack(self, ud, rootdir, d):
        """
        Uses the crate to build the necessary paths for cargo to utilize it
        """
        if ud.type == 'crate':
            return self._crate_unpack(ud, rootdir, d)
        else:
            super(Crate, self).unpack(ud, rootdir, d)

    def _crate_unpack(self, ud, rootdir, d):
        """
        Unpacks a crate
        """
        thefile = ud.localpath

        # possible metadata we need to write out
        metadata = {}

        # change to the rootdir to unpack but save the old working dir
        save_cwd = os.getcwd()
        os.chdir(rootdir)

        bp = d.getVar('BP')
        if bp == ud.parm.get('name'):
            cmd = "tar -xz --no-same-owner -f %s" % thefile
        else:
            cargo_bitbake = self._cargo_bitbake_path(rootdir)

            cmd = "tar -xz --no-same-owner -f %s -C %s" % (thefile, cargo_bitbake)

            # ensure we've got these paths made
            bb.utils.mkdirhier(cargo_bitbake)

            # generate metadata necessary
            with open(thefile, 'rb') as f:
                # get the SHA256 of the original tarball
                tarhash = hashlib.sha256(f.read()).hexdigest()

            metadata['files'] = {}
            metadata['package'] = tarhash

        path = d.getVar('PATH')
        if path:
            cmd = "PATH=\"%s\" %s" % (path, cmd)
        bb.note("Unpacking %s to %s/" % (thefile, os.getcwd()))

        ret = subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True)

        os.chdir(save_cwd)

        if ret != 0:
            raise UnpackError("Unpack command %s failed with return value %s" % (cmd, ret), ud.url)

        # if we have metadata to write out..
        if len(metadata) > 0:
            cratepath = os.path.splitext(os.path.basename(thefile))[0]
            bbpath = self._cargo_bitbake_path(rootdir)
            mdfile = '.cargo-checksum.json'
            mdpath = os.path.join(bbpath, cratepath, mdfile)
            with open(mdpath, "w") as f:
                json.dump(metadata, f)
