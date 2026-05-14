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
from functools import cmp_to_key
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

    def _generate_index_path(self, name):
        # https://doc.rust-lang.org/cargo/reference/registry-index.html#index-files
        if len(name) == 1:
            return f"1/{name}"
        elif len(name) == 2:
            return f"2/{name}"
        elif len(name) == 3:
            return f"3/{name[0]}/{name}"
        else:
            return f"{name[0:2]}/{name[2:4]}/{name}"

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

        # If using crates.io use the CDN directly as per https://crates.io/data-access
        if host == 'crates.io':
            ud.url = "https://static.crates.io/crates/%s/%s/download" % (name, version)
            ud.versionsurl = 'https://index.crates.io/' + self._generate_index_path(name)
        else:
            ud.url = "https://%s/%s/%s/download" % (host, name, version)
            ud.versionsurl = "https://%s/%s/versions" % (host, name)

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
            ud.unpack_tracer.unpack("crate-extract", rootdir)
        else:
            cargo_bitbake = self._cargo_bitbake_path(rootdir)
            ud.unpack_tracer.unpack("cargo-extract", cargo_bitbake)

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

    def latest_versionstring(self, ud, d):
        """
        Return the latest upstream version, dispatching to the appropriate
        parser based on the versionsurl format.
        """
        if ud.versionsurl.startswith('https://index.crates.io/'):
            return self._latest_versionstring_from_index(ud, d)
        return self._latest_versionstring_from_api(ud, d)

    def _latest_versionstring_from_api(self, ud, d):
        """
        Parse the latest version from a [name]/versions JSON API response.
        """
        json_data = json.loads(self._fetch_index(ud.versionsurl, ud, d))
        versions = [(0, i["num"], "") for i in json_data["versions"]]
        versions = sorted(versions, key=cmp_to_key(bb.utils.vercmp))

        return (versions[-1][1], "") if versions else ("", "")

    def _latest_versionstring_from_index(self, ud, d):
        """
        Parse the latest version from a Cargo sparse index file (NDJSON).
        https://doc.rust-lang.org/cargo/reference/registry-index.html#index-files
        """
        versions = []
        response = self._fetch_index(ud.versionsurl, ud, d)
        for line in response.splitlines():
            data = json.loads(line)
            if not data.get("yanked", False):
                versions.append((0, data["vers"], ""))

        versions = sorted(versions, key=cmp_to_key(bb.utils.vercmp))
        return (versions[-1][1], "") if versions else ("", "")
