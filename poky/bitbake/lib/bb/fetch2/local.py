"""
BitBake 'Fetch' implementations

Classes for obtaining upstream sources for the
BitBake build tools.

"""

# Copyright (C) 2003, 2004  Chris Larson
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig
#

import os
import urllib.request, urllib.parse, urllib.error
import bb
import bb.utils
from   bb.fetch2 import FetchMethod, FetchError, ParameterError
from   bb.fetch2 import logger

class Local(FetchMethod):
    def supports(self, urldata, d):
        """
        Check to see if a given url represents a local fetch.
        """
        return urldata.type in ['file']

    def urldata_init(self, ud, d):
        # We don't set localfile as for this fetcher the file is already local!
        ud.decodedurl = urllib.parse.unquote(ud.url.split("://")[1].split(";")[0])
        ud.basename = os.path.basename(ud.decodedurl)
        ud.basepath = ud.decodedurl
        ud.needdonestamp = False
        if "*" in ud.decodedurl:
            raise bb.fetch2.ParameterError("file:// urls using globbing are no longer supported. Please place the files in a directory and reference that instead.", ud.url)
        return

    def localpath(self, urldata, d):
        """
        Return the local filename of a given url assuming a successful fetch.
        """
        return self.localpaths(urldata, d)[-1]

    def localpaths(self, urldata, d):
        """
        Return the local filename of a given url assuming a successful fetch.
        """
        searched = []
        path = urldata.decodedurl
        newpath = path
        if path[0] == "/":
            return [path]
        filespath = d.getVar('FILESPATH')
        if filespath:
            logger.debug2("Searching for %s in paths:\n    %s" % (path, "\n    ".join(filespath.split(":"))))
            newpath, hist = bb.utils.which(filespath, path, history=True)
            searched.extend(hist)
        return searched

    def need_update(self, ud, d):
        if os.path.exists(ud.localpath):
            return False
        return True

    def download(self, urldata, d):
        """Fetch urls (no-op for Local method)"""
        # no need to fetch local files, we'll deal with them in place.
        if self.supports_checksum(urldata) and not os.path.exists(urldata.localpath):
            locations = []
            filespath = d.getVar('FILESPATH')
            if filespath:
                locations = filespath.split(":")
            msg = "Unable to find file " + urldata.url + " anywhere. The paths that were searched were:\n    " + "\n    ".join(locations)
            raise FetchError(msg)

        return True

    def checkstatus(self, fetch, urldata, d):
        """
        Check the status of the url
        """
        if os.path.exists(urldata.localpath):
            return True
        return False

    def clean(self, urldata, d):
        return

