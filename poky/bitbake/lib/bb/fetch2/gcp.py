"""
BitBake 'Fetch' implementation for Google Cloup Platform Storage.

Class for fetching files from Google Cloud Storage using the
Google Cloud Storage Python Client. The GCS Python Client must
be correctly installed, configured and authenticated prior to use.
Additionally, gsutil must also be installed.

"""

# Copyright (C) 2023, Snap Inc.
#
# Based in part on bb.fetch2.s3:
#    Copyright (C) 2017 Andre McCurdy
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import bb
import urllib.parse, urllib.error
from bb.fetch2 import FetchMethod
from bb.fetch2 import FetchError
from bb.fetch2 import logger
from bb.fetch2 import runfetchcmd

class GCP(FetchMethod):
    """
    Class to fetch urls via GCP's Python API.
    """
    def __init__(self):
        self.gcp_client = None

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with GCP.
        """
        return ud.type in ['gs']

    def recommends_checksum(self, urldata):
        return True

    def urldata_init(self, ud, d):
        if 'downloadfilename' in ud.parm:
            ud.basename = ud.parm['downloadfilename']
        else:
            ud.basename = os.path.basename(ud.path)

        ud.localfile = d.expand(urllib.parse.unquote(ud.basename))
        ud.basecmd = "gsutil stat"

    def get_gcp_client(self):
        from google.cloud import storage
        self.gcp_client = storage.Client(project=None)

    def download(self, ud, d):
        """
        Fetch urls using the GCP API.
        Assumes localpath was called first.
        """
        logger.debug2(f"Trying to download gs://{ud.host}{ud.path} to {ud.localpath}")
        if self.gcp_client is None:
            self.get_gcp_client()

        bb.fetch2.check_network_access(d, ud.basecmd, f"gs://{ud.host}{ud.path}")
        runfetchcmd("%s %s" % (ud.basecmd, f"gs://{ud.host}{ud.path}"), d)

        # Path sometimes has leading slash, so strip it
        path = ud.path.lstrip("/")
        blob = self.gcp_client.bucket(ud.host).blob(path)
        blob.download_to_filename(ud.localpath)

        # Additional sanity checks copied from the wget class (although there
        # are no known issues which mean these are required, treat the GCP API
        # tool with a little healthy suspicion).
        if not os.path.exists(ud.localpath):
            raise FetchError(f"The GCP API returned success for gs://{ud.host}{ud.path} but {ud.localpath} doesn't exist?!")

        if os.path.getsize(ud.localpath) == 0:
            os.remove(ud.localpath)
            raise FetchError(f"The downloaded file for gs://{ud.host}{ud.path} resulted in a zero size file?! Deleting and failing since this isn't right.")

        return True

    def checkstatus(self, fetch, ud, d):
        """
        Check the status of a URL.
        """
        logger.debug2(f"Checking status of gs://{ud.host}{ud.path}")
        if self.gcp_client is None:
            self.get_gcp_client()

        bb.fetch2.check_network_access(d, ud.basecmd, f"gs://{ud.host}{ud.path}")
        runfetchcmd("%s %s" % (ud.basecmd, f"gs://{ud.host}{ud.path}"), d)

        # Path sometimes has leading slash, so strip it
        path = ud.path.lstrip("/")
        if self.gcp_client.bucket(ud.host).blob(path).exists() == False:
            raise FetchError(f"The GCP API reported that gs://{ud.host}{ud.path} does not exist")
        else:
            return True
