# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from . import OETestDecorator, registerDecorator

import signal
from threading import Timer

from oeqa.core.threaded import OETestRunnerThreaded
from oeqa.core.exception import OEQATimeoutError

@registerDecorator
class OETimeout(OETestDecorator):
    attrs = ('oetimeout',)

    def setUpDecorator(self):
        self.logger.debug("Setting up a %d second(s) timeout" % self.oetimeout)

        if isinstance(self.case.tc.runner, OETestRunnerThreaded):
            self.timeouted = False
            def _timeoutHandler():
                self.timeouted = True

            self.timer = Timer(self.oetimeout, _timeoutHandler)
            self.timer.start()
        else:
            timeout = self.oetimeout
            def _timeoutHandler(signum, frame):
                raise OEQATimeoutError("Timed out after %s "
                    "seconds of execution" % timeout)

            self.alarmSignal = signal.signal(signal.SIGALRM, _timeoutHandler)
            signal.alarm(self.oetimeout)

    def tearDownDecorator(self):
        if isinstance(self.case.tc.runner, OETestRunnerThreaded):
            self.timer.cancel()
            self.logger.debug("Removed Timer handler")
            if self.timeouted:
                raise OEQATimeoutError("Timed out after %s "
                    "seconds of execution" % self.oetimeout)
        else:
            signal.alarm(0)
            signal.signal(signal.SIGALRM, self.alarmSignal)
            self.logger.debug("Removed SIGALRM handler")
