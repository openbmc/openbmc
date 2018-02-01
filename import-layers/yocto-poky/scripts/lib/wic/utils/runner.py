#!/usr/bin/env python -tt
#
# Copyright (c) 2011 Intel, Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.
import subprocess

from wic import WicError

def runtool(cmdln_or_args):
    """ wrapper for most of the subprocess calls
    input:
        cmdln_or_args: can be both args and cmdln str (shell=True)
    return:
        rc, output
    """
    if isinstance(cmdln_or_args, list):
        cmd = cmdln_or_args[0]
        shell = False
    else:
        import shlex
        cmd = shlex.split(cmdln_or_args)[0]
        shell = True

    sout = subprocess.PIPE
    serr = subprocess.STDOUT

    try:
        process = subprocess.Popen(cmdln_or_args, stdout=sout,
                                   stderr=serr, shell=shell)
        sout, serr = process.communicate()
        # combine stdout and stderr, filter None out and decode
        out = ''.join([out.decode('utf-8') for out in [sout, serr] if out])
    except OSError as err:
        if err.errno == 2:
            # [Errno 2] No such file or directory
            raise WicError('Cannot run command: %s, lost dependency?' % cmd)
        else:
            raise # relay

    return process.returncode, out
