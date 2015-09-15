# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake SFTP Fetch implementation

Class for fetching files via SFTP. It tries to adhere to the (now
expired) IETF Internet Draft for "Uniform Resource Identifier (URI)
Scheme for Secure File Transfer Protocol (SFTP) and Secure Shell
(SSH)" (SECSH URI).

It uses SFTP (as to adhere to the SECSH URI specification). It only
supports key based authentication, not password. This class, unlike
the SSH fetcher, does not support fetching a directory tree from the
remote.

  http://tools.ietf.org/html/draft-ietf-secsh-scp-sftp-ssh-uri-04
  https://www.iana.org/assignments/uri-schemes/prov/sftp
  https://tools.ietf.org/html/draft-ietf-secsh-filexfer-13

Please note that '/' is used as host path seperator, and not ":"
as you may be used to from the scp/sftp commands. You can use a
~ (tilde) to specify a path relative to your home directory.
(The /~user/ syntax, for specyfing a path relative to another
user's home directory is not supported.) Note that the tilde must
still follow the host path seperator ("/"). See exampels below.

Example SRC_URIs:

SRC_URI = "sftp://host.example.com/dir/path.file.txt"

A path relative to your home directory.

SRC_URI = "sftp://host.example.com/~/dir/path.file.txt"

You can also specify a username (specyfing password in the
URI is not supported, use SSH keys to authenticate):

SRC_URI = "sftp://user@host.example.com/dir/path.file.txt"

"""

# Copyright (C) 2013, Olof Johansson <olof.johansson@axis.com>
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
import urllib
import commands
from bb import data
from bb.fetch2 import URI
from bb.fetch2 import FetchMethod
from bb.fetch2 import runfetchcmd


class SFTP(FetchMethod):
    """Class to fetch urls via 'sftp'"""

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with sftp.
        """
        return ud.type in ['sftp']

    def recommends_checksum(self, urldata):
        return True

    def urldata_init(self, ud, d):
        if 'protocol' in ud.parm and ud.parm['protocol'] == 'git':
            raise bb.fetch2.ParameterError(
                "Invalid protocol - if you wish to fetch from a " +
                "git repository using ssh, you need to use the " +
                "git:// prefix with protocol=ssh", ud.url)

        if 'downloadfilename' in ud.parm:
            ud.basename = ud.parm['downloadfilename']
        else:
            ud.basename = os.path.basename(ud.path)

        ud.localfile = data.expand(urllib.unquote(ud.basename), d)

    def download(self, ud, d):
        """Fetch urls"""

        urlo = URI(ud.url)
        basecmd = 'sftp -oBatchMode=yes'
        port = ''
        if urlo.port:
            port = '-P %d' % urlo.port
            urlo.port = None

        dldir = data.getVar('DL_DIR', d, True)
        lpath = os.path.join(dldir, ud.localfile)

        user = ''
        if urlo.userinfo:
            user = urlo.userinfo + '@'

        path = urlo.path

        # Supoprt URIs relative to the user's home directory, with
        # the tilde syntax. (E.g. <sftp://example.com/~/foo.diff>).
        if path[:3] == '/~/':
            path = path[3:]

        remote = '%s%s:%s' % (user, urlo.hostname, path)

        cmd = '%s %s %s %s' % (basecmd, port, commands.mkarg(remote),
                               commands.mkarg(lpath))

        bb.fetch2.check_network_access(d, cmd, ud.url)
        runfetchcmd(cmd, d)
        return True
