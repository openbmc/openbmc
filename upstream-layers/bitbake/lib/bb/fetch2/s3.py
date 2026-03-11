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
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import bb
import urllib.request, urllib.parse, urllib.error
import re
from bb.fetch2 import FetchMethod
from bb.fetch2 import FetchError
from bb.fetch2 import runfetchcmd

def convertToBytes(value, unit):
    value = float(value)
    if (unit == "KiB"):
        value = value*1024.0;
    elif (unit == "MiB"):
        value = value*1024.0*1024.0;
    elif (unit == "GiB"):
        value = value*1024.0*1024.0*1024.0;
    return value

class S3ProgressHandler(bb.progress.LineFilterProgressHandler):
    """
    Extract progress information from s3 cp output, e.g.:
    Completed 5.1 KiB/8.8 GiB (12.0 MiB/s) with 1 file(s) remaining
    """
    def __init__(self, d):
        super(S3ProgressHandler, self).__init__(d)
        # Send an initial progress event so the bar gets shown
        self._fire_progress(0)

    def writeline(self, line):
        percs = re.findall(r'^Completed (\d+.{0,1}\d*) (\w+)\/(\d+.{0,1}\d*) (\w+) (\(.+\)) with\s+', line)
        if percs:
            completed = (percs[-1][0])
            completedUnit = (percs[-1][1])
            total = (percs[-1][2])
            totalUnit = (percs[-1][3])
            completed = convertToBytes(completed, completedUnit)
            total = convertToBytes(total, totalUnit)
            progress = (completed/total)*100.0
            rate = percs[-1][4]
            self.update(progress, rate)
            return False
        return True


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

        ud.localfile = ud.basename

        ud.basecmd = d.getVar("FETCHCMD_s3") or "/usr/bin/env aws s3"

    def download(self, ud, d):
        """
        Fetch urls
        Assumes localpath was called first
        """

        cmd = '%s cp s3://%s%s %s' % (ud.basecmd, ud.host, ud.path, ud.localpath)
        bb.fetch2.check_network_access(d, cmd, ud.url)

        progresshandler = S3ProgressHandler(d)
        runfetchcmd(cmd, d, False, log=progresshandler)

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
