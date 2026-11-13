#
# BitBake Tests for server/process.py
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import tempfile
import unittest
from unittest.mock import Mock, patch

from bb.server.process import ProcessServer


class ProcessServerTests(unittest.TestCase):
    def test_shutdown_contended_lock(self):
        pid = os.getpid()
        for owner in (str(pid), str(pid) + "0"):
            for suffix in ("\n", " 127.0.0.1:12345\n"):
                with self.subTest(owner=owner, suffix=suffix):
                    with tempfile.TemporaryDirectory() as tmpdir:
                        lockname = os.path.join(tmpdir, "bitbake.lock")
                        sockname = os.path.join(tmpdir, "bitbake.sock")
                        with open(lockname, "w") as stream:
                            stream.write(owner + suffix)
                        with open(sockname, "w"):
                            pass

                        lock = Mock()
                        sock = Mock()
                        server = ProcessServer(lock, lockname, sock, sockname,
                                               0, (None, None))
                        server.cooker = Mock()
                        server.quit = True
                        acquired_lock = Mock()
                        own_lock = owner == str(pid)
                        attempts = [None, acquired_lock] if own_lock else [None]

                        with patch("bb.server.process.bb.utils.set_process_name"), \
                             patch("bb.server.process.os.path.exists", return_value=True), \
                             patch("bb.server.process.bb.utils.lockfile", side_effect=attempts) as acquire, \
                             patch("bb.server.process.bb.utils.unlockfile") as unlock, \
                             patch("bb.server.process.time.sleep") as sleep, \
                             patch("bb.server.process.serverlog"):
                            server.main()

                        lock.close.assert_called_once_with()
                        sock.close.assert_called_once_with()
                        server.cooker.shutdown.assert_called_once_with(True, idle=False)
                        self.assertEqual(acquire.call_count, 2 if own_lock else 1)
                        acquire.assert_called_with(lockname, shared=False, retry=False, block=False)
                        if own_lock:
                            sleep.assert_called_once_with(0.1)
                            unlock.assert_called_once_with(acquired_lock)
                        else:
                            sleep.assert_not_called()
                            unlock.assert_not_called()
