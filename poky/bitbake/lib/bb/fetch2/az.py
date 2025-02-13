"""
BitBake 'Fetch' Azure Storage implementation

"""

# Copyright (C) 2021 Alejandro Hernandez Samaniego
#
# Based on bb.fetch2.wget:
# Copyright (C) 2003, 2004  Chris Larson
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import shlex
import os
import bb
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2.wget import Wget


class Az(Wget):

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched from Azure Storage
        """
        return ud.type in ['az']


    def checkstatus(self, fetch, ud, d, try_again=True):

        # checkstatus discards parameters either way, we need to do this before adding the SAS
        ud.url = ud.url.replace('az://','https://').split(';')[0]

        az_sas = d.getVar('AZ_SAS')
        if az_sas and az_sas not in ud.url:
            ud.url += az_sas

        return Wget.checkstatus(self, fetch, ud, d, try_again)

    # Override download method, include retries
    def download(self, ud, d, retries=3):
        """Fetch urls"""

        # If were reaching the account transaction limit we might be refused a connection,
        # retrying allows us to avoid false negatives since the limit changes over time
        fetchcmd = self.basecmd + ' --retry-connrefused --waitretry=5'

        # We need to provide a localpath to avoid wget using the SAS
        # ud.localfile either has the downloadfilename or ud.path
        localpath = os.path.join(d.getVar("DL_DIR"), ud.localfile)
        bb.utils.mkdirhier(os.path.dirname(localpath))
        fetchcmd += " -O %s" % shlex.quote(localpath)


        if ud.user and ud.pswd:
            fetchcmd += " --user=%s --password=%s --auth-no-challenge" % (ud.user, ud.pswd)

        # Check if a Shared Access Signature was given and use it
        az_sas = d.getVar('AZ_SAS')

        if az_sas:
            azuri = '%s%s%s%s' % ('https://', ud.host, ud.path, az_sas)
        else:
            azuri = '%s%s%s' % ('https://', ud.host, ud.path)

        dldir = d.getVar("DL_DIR")
        if os.path.exists(ud.localpath):
            # file exists, but we didnt complete it.. trying again.
            fetchcmd += " -c -P %s '%s'" % (dldir, azuri)
        else:
            fetchcmd += " -P %s '%s'" % (dldir, azuri)

        try:
            self._runwget(ud, d, fetchcmd, False)
        except FetchError as e:
            # Azure fails on handshake sometimes when using wget after some stress, producing a
            # FetchError from the fetcher, if the artifact exists retyring should succeed
            if 'Unable to establish SSL connection' in str(e):
                logger.debug2('Unable to establish SSL connection: Retries remaining: %s, Retrying...' % retries)
                self.download(ud, d, retries -1)

        # Sanity check since wget can pretend it succeed when it didn't
        # Also, this used to happen if sourceforge sent us to the mirror page
        if not os.path.exists(ud.localpath):
            raise FetchError("The fetch command returned success for url %s but %s doesn't exist?!" % (azuri, ud.localpath), azuri)

        if os.path.getsize(ud.localpath) == 0:
            os.remove(ud.localpath)
            raise FetchError("The fetch of %s resulted in a zero size file?! Deleting and failing since this isn't right." % (azuri), azuri)

        return True
