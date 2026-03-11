#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import sys
import tempfile
import contextlib
import socket
from oeqa.utils.commands import bitbake, get_bb_var, Command
from oeqa.utils.network import get_free_port

@contextlib.contextmanager
def unfs_server(directory, logger = None, udp = True):
    unfs_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "unfs3-native")
    if not os.path.exists(os.path.join(unfs_sysroot, "usr", "bin", "unfsd")):
        # build native tool
        bitbake("unfs3-native -c addto_recipe_sysroot")

    exports = None
    cmd = None
    try:
        # create the exports file
        with tempfile.NamedTemporaryFile(delete = False) as exports:
            exports.write("{0} (rw,no_root_squash,no_all_squash,insecure)\n".format(directory).encode())

        # find some ports for the server
        nfsport, mountport = get_free_port(udp), get_free_port(udp)

        nenv = dict(os.environ)
        nenv['PATH'] = "{0}/sbin:{0}/usr/sbin:{0}/usr/bin:".format(unfs_sysroot) + nenv.get('PATH', '')
        cmd = Command(["unfsd", "-d", "-p", "-e", exports.name, "-n", str(nfsport), "-m", str(mountport)],
                bg = True, env = nenv, output_log = logger)
        cmd.run()
        yield nfsport, mountport
    finally:
        if cmd is not None:
            cmd.stop()
        if exports is not None:
            # clean up exports file
            os.unlink(exports.name)

