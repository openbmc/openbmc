# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementation for Amazon AWS S3.

Class for fetching files from Amazon S3 using the AWS Command Line Interface.
The aws tool must be correctly installed and configured prior to use.

"""

# Copyright (C) 2017, Andre McCurdy <armccurdy@gmail.com>
#
# Based in part on bb.fetch2.wget:
#    Copyright (C) 2003, 2004  Chris Larson
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import bb
import urllib.request, urllib.parse, urllib.error
from bb.fetch2 import FetchMethod
from bb.fetch2 import FetchError
from bb.fetch2 import runfetchcmd

class S3(FetchMethod):
    """Class to fetch urls via 'aws s3'"""

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with s3.
        """
        return ud.type in ['s3']

    def recommends_checksum(self, urldata):
        return True

    def urldata_init(self, ud, d):
        if 'downloadfilename' in ud.parm:
            ud.basename = ud.parm['downloadfilename']
        else:
            ud.basename = os.path.basename(ud.path)

        ud.localfile = d.expand(urllib.parse.unquote(ud.basename))

        ud.basecmd = d.getVar("FETCHCMD_s3") or "/usr/bin/env aws s3"

    def download(self, ud, d):
        """
        Fetch urls
        Assumes localpath was called first
        """

        cmd = '%s cp s3://%s%s %s' % (ud.basecmd, ud.host, ud.path, ud.localpath)
        bb.fetch2.check_network_access(d, cmd, ud.url)
        runfetchcmd(cmd, d)

        # Additional sanity checks copied from the wget class (although there
        # are no known issues which mean these are required, treat the aws cli
        # tool with a little healthy suspicion).

        if not os.path.exists(ud.localpath):
            raise FetchError("The aws cp command returned success for s3://%s%s but %s doesn't exist?!" % (ud.host, ud.path, ud.localpath))

        if os.path.getsize(ud.localpath) == 0:
            os.remove(ud.localpath)
            raise FetchError("The aws cp command for s3://%s%s resulted in a zero size file?! Deleting and failing since this isn't right." % (ud.host, ud.path))

        return True

    def checkstatus(self, fetch, ud, d):
        """
        Check the status of a URL
        """

        cmd = '%s ls s3://%s%s' % (ud.basecmd, ud.host, ud.path)
        bb.fetch2.check_network_access(d, cmd, ud.url)
        output = runfetchcmd(cmd, d)

        # "aws s3 ls s3://mybucket/foo" will exit with success even if the file
        # is not found, so check output of the command to confirm success.

        if not output:
            raise FetchError("The aws ls command for s3://%s%s gave empty output" % (ud.host, ud.path))

        return True
