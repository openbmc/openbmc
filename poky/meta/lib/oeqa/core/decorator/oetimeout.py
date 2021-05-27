#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import signal
from . import OETestDecorator, registerDecorator
from oeqa.core.exception import OEQATimeoutError

@registerDecorator
class OETimeout(OETestDecorator):
    attrs = ('oetimeout',)

    def setUpDecorator(self):
        timeout = self.oetimeout
        def _timeoutHandler(signum, frame):
            raise OEQATimeoutError("Timed out after %s "
                    "seconds of execution" % timeout)

        self.logger.debug("Setting up a %d second(s) timeout" % self.oetimeout)
        self.alarmSignal = signal.signal(signal.SIGALRM, _timeoutHandler)
        signal.alarm(self.oetimeout)

    def tearDownDecorator(self):
        signal.alarm(0)
        if hasattr(self, 'alarmSignal'):
            signal.signal(signal.SIGALRM, self.alarmSignal)
            self.logger.debug("Removed SIGALRM handler")
