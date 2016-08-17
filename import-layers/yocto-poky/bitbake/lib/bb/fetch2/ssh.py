# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
'''
BitBake 'Fetch' implementations

This implementation is for Secure Shell (SSH), and attempts to comply with the
IETF secsh internet draft:
    http://tools.ietf.org/wg/secsh/draft-ietf-secsh-scp-sftp-ssh-uri/

    Currently does not support the sftp parameters, as this uses scp
    Also does not support the 'fingerprint' connection parameter.

    Please note that '/' is used as host, path separator not ':' as you may 
    be used to, also '~' can be used to specify user HOME, but again after '/'
    
    Example SRC_URI:
    SRC_URI = "ssh://user@host.example.com/dir/path/file.txt"
    SRC_URI = "ssh://user@host.example.com/~/file.txt"
'''

# Copyright (C) 2006  OpenedHand Ltd.
#
#
# Based in part on svk.py:
#    Copyright (C) 2006 Holger Hans Peter Freyther
#    Based on svn.py:
#        Copyright (C) 2003, 2004  Chris Larson
#        Based on functions from the base bb module:
#            Copyright 2003 Holger Schurig
#
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

import re, os
from   bb import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2 import runfetchcmd


__pattern__ = re.compile(r'''
 \s*                 # Skip leading whitespace
 ssh://              # scheme
 (                   # Optional username/password block
  (?P<user>\S+)      # username
  (:(?P<pass>\S+))?  # colon followed by the password (optional)
 )?
 (?P<cparam>(;[^;]+)*)?  # connection parameters block (optional)
 @
 (?P<host>\S+?)          # non-greedy match of the host
 (:(?P<port>[0-9]+))?    # colon followed by the port (optional)
 /
 (?P<path>[^;]+)         # path on the remote system, may be absolute or relative,
                         # and may include the use of '~' to reference the remote home
                         # directory
 (?P<sparam>(;[^;]+)*)?  # parameters block (optional)
 $
''', re.VERBOSE)

class SSH(FetchMethod):
    '''Class to fetch a module or modules via Secure Shell'''

    def supports(self, urldata, d):
        return __pattern__.match(urldata.url) != None

    def supports_checksum(self, urldata):
        return False

    def urldata_init(self, urldata, d):
        if 'protocol' in urldata.parm and urldata.parm['protocol'] == 'git':
            raise bb.fetch2.ParameterError(
                "Invalid protocol - if you wish to fetch from a git " +
                "repository using ssh, you need to use " +
                "git:// prefix with protocol=ssh", urldata.url)
        m = __pattern__.match(urldata.url)
        path = m.group('path')
        host = m.group('host')
        urldata.localpath = os.path.join(d.getVar('DL_DIR', True),
                os.path.basename(os.path.normpath(path)))

    def download(self, urldata, d):
        dldir = d.getVar('DL_DIR', True)

        m = __pattern__.match(urldata.url)
        path = m.group('path')
        host = m.group('host')
        port = m.group('port')
        user = m.group('user')
        password = m.group('pass')

        if port:
            portarg = '-P %s' % port
        else:
            portarg = ''

        if user:
            fr = user
            if password:
                fr += ':%s' % password
            fr += '@%s' % host
        else:
            fr = host
        fr += ':%s' % path


        import commands
        cmd = 'scp -B -r %s %s %s/' % (
            portarg,
            commands.mkarg(fr),
            commands.mkarg(dldir)
        )

        bb.fetch2.check_network_access(d, cmd, urldata.url)

        runfetchcmd(cmd, d)

