"""
BitBake progress handling code
"""

# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import time
import inspect
import bb.event
import bb.build
from bb.build import StdoutNoopContextManager

class ProgressHandler(object):
    """
    Base class that can pretend to be a file object well enough to be
    used to build objects to intercept console output and determine the
    progress of some operation.
    """
    def __init__(self, d, outfile=None):
        self._progress = 0
        self._data = d
        self._lastevent = 0
        if outfile:
            self._outfile = outfile
        else:
            self._outfile = StdoutNoopContextManager()

    def __enter__(self):
        self._outfile.__enter__()
        return self

    def __exit__(self, *excinfo):
        self._outfile.__exit__(*excinfo)

    def _fire_progress(self, taskprogress, rate=None):
        """Internal function to fire the progress event"""
        bb.event.fire(bb.build.TaskProgress(taskprogress, rate), self._data)

    def write(self, string):
        self._outfile.write(string)

    def flush(self):
        self._outfile.flush()

    def update(self, progress, rate=None):
        ts = time.time()
        if progress > 100:
            progress = 100
        if progress != self._progress or self._lastevent + 1 < ts:
            self._fire_progress(progress, rate)
            self._lastevent = ts
            self._progress = progress

class LineFilterProgressHandler(ProgressHandler):
    """
    A ProgressHandler variant that provides the ability to filter out
    the lines if they contain progress information. Additionally, it
    filters out anything before the last line feed on a line. This can
    be used to keep the logs clean of output that we've only enabled for
    getting progress, assuming that that can be done on a per-line
    basis.
    """
    def __init__(self, d, outfile=None):
        self._linebuffer = ''
        super(LineFilterProgressHandler, self).__init__(d, outfile)

    def write(self, string):
        self._linebuffer += string
        while True:
            breakpos = self._linebuffer.find('\n') + 1
            if breakpos == 0:
                break
            line = self._linebuffer[:breakpos]
            self._linebuffer = self._linebuffer[breakpos:]
            # Drop any line feeds and anything that precedes them
            lbreakpos = line.rfind('\r') + 1
            if lbreakpos:
                line = line[lbreakpos:]
            if self.writeline(line):
                super(LineFilterProgressHandler, self).write(line)

    def writeline(self, line):
        return True

class BasicProgressHandler(ProgressHandler):
    def __init__(self, d, regex=r'(\d+)%', outfile=None):
        super(BasicProgressHandler, self).__init__(d, outfile)
        self._regex = re.compile(regex)
        # Send an initial progress event so the bar gets shown
        self._fire_progress(0)

    def write(self, string):
        percs = self._regex.findall(string)
        if percs:
            progress = int(percs[-1])
            self.update(progress)
        super(BasicProgressHandler, self).write(string)

class OutOfProgressHandler(ProgressHandler):
    def __init__(self, d, regex, outfile=None):
        super(OutOfProgressHandler, self).__init__(d, outfile)
        self._regex = re.compile(regex)
        # Send an initial progress event so the bar gets shown
        self._fire_progress(0)

    def write(self, string):
        nums = self._regex.findall(string)
        if nums:
            progress = (float(nums[-1][0]) / float(nums[-1][1])) * 100
            self.update(progress)
        super(OutOfProgressHandler, self).write(string)

class MultiStageProgressReporter(object):
    """
    Class which allows reporting progress without the caller
    having to know where they are in the overall sequence. Useful
    for tasks made up of python code spread across multiple
    classes / functions - the progress reporter object can
    be passed around or stored at the object level and calls
    to next_stage() and update() made whereever needed.
    """
    def __init__(self, d, stage_weights, debug=False):
        """
        Initialise the progress reporter.

        Parameters:
        * d: the datastore (needed for firing the events)
        * stage_weights: a list of weight values, one for each stage.
          The value is scaled internally so you only need to specify
          values relative to other values in the list, so if there
          are two stages and the first takes 2s and the second takes
          10s you would specify [2, 10] (or [1, 5], it doesn't matter).
        * debug: specify True (and ensure you call finish() at the end)
          in order to show a printout of the calculated stage weights
          based on timing each stage. Use this to determine what the
          weights should be when you're not sure.
        """
        self._data = d
        total = sum(stage_weights)
        self._stage_weights = [float(x)/total for x in stage_weights]
        self._stage = -1
        self._base_progress = 0
        # Send an initial progress event so the bar gets shown
        self._fire_progress(0)
        self._debug = debug
        self._finished = False
        if self._debug:
            self._last_time = time.time()
            self._stage_times = []
            self._stage_total = None
            self._callers = []

    def __enter__(self):
        return self

    def __exit__(self, *excinfo):
        pass

    def _fire_progress(self, taskprogress):
        bb.event.fire(bb.build.TaskProgress(taskprogress), self._data)

    def next_stage(self, stage_total=None):
        """
        Move to the next stage.
        Parameters:
        * stage_total: optional total for progress within the stage,
          see update() for details
        NOTE: you need to call this before the first stage.
        """
        self._stage += 1
        self._stage_total = stage_total
        if self._stage == 0:
            # First stage
            if self._debug:
                self._last_time = time.time()
        else:
            if self._stage < len(self._stage_weights):
                self._base_progress = sum(self._stage_weights[:self._stage]) * 100
                if self._debug:
                    currtime = time.time()
                    self._stage_times.append(currtime - self._last_time)
                    self._last_time = currtime
                    self._callers.append(inspect.getouterframes(inspect.currentframe())[1])
            elif not self._debug:
                bb.warn('ProgressReporter: current stage beyond declared number of stages')
                self._base_progress = 100
            self._fire_progress(self._base_progress)

    def update(self, stage_progress):
        """
        Update progress within the current stage.
        Parameters:
        * stage_progress: progress value within the stage. If stage_total
          was specified when next_stage() was last called, then this
          value is considered to be out of stage_total, otherwise it should
          be a percentage value from 0 to 100.
        """
        if self._stage_total:
            stage_progress = (float(stage_progress) / self._stage_total) * 100
        if self._stage < 0:
            bb.warn('ProgressReporter: update called before first call to next_stage()')
        elif self._stage < len(self._stage_weights):
            progress = self._base_progress + (stage_progress * self._stage_weights[self._stage])
        else:
            progress = self._base_progress
        if progress > 100:
            progress = 100
        self._fire_progress(progress)

    def finish(self):
        if self._finished:
            return
        self._finished = True
        if self._debug:
            import math
            self._stage_times.append(time.time() - self._last_time)
            mintime = max(min(self._stage_times), 0.01)
            self._callers.append(None)
            stage_weights = [int(math.ceil(x / mintime)) for x in self._stage_times]
            bb.warn('Stage weights: %s' % stage_weights)
            out = []
            for stage_weight, caller in zip(stage_weights, self._callers):
                if caller:
                    out.append('Up to %s:%d: %d' % (caller[1], caller[2], stage_weight))
                else:
                    out.append('Up to finish: %d' % stage_weight)
            bb.warn('Stage times:\n  %s' % '\n  '.join(out))

class MultiStageProcessProgressReporter(MultiStageProgressReporter):
    """
    Version of MultiStageProgressReporter intended for use with
    standalone processes (such as preparing the runqueue)
    """
    def __init__(self, d, processname, stage_weights, debug=False):
        self._processname = processname
        self._started = False
        MultiStageProgressReporter.__init__(self, d, stage_weights, debug)

    def start(self):
        if not self._started:
            bb.event.fire(bb.event.ProcessStarted(self._processname, 100), self._data)
            self._started = True

    def _fire_progress(self, taskprogress):
        if taskprogress == 0:
            self.start()
            return
        bb.event.fire(bb.event.ProcessProgress(self._processname, taskprogress), self._data)

    def finish(self):
        MultiStageProgressReporter.finish(self)
        bb.event.fire(bb.event.ProcessFinished(self._processname), self._data)

class DummyMultiStageProcessProgressReporter(MultiStageProgressReporter):
    """
    MultiStageProcessProgressReporter that takes the calls and does nothing
    with them (to avoid a bunch of "if progress_reporter:" checks)
    """
    def __init__(self):
        MultiStageProcessProgressReporter.__init__(self, "", None, [])

    def _fire_progress(self, taskprogress, rate=None):
        pass

    def start(self):
        pass

    def next_stage(self, stage_total=None):
        pass

    def update(self, stage_progress):
        pass

    def finish(self):
        pass
