# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from abc import abstractmethod

class OETarget(object):

    def __init__(self, logger, *args, **kwargs):
        self.logger = logger

    @abstractmethod
    def start(self):
        pass

    @abstractmethod
    def stop(self):
        pass

    @abstractmethod
    def run(self, cmd, timeout=None):
        pass

    @abstractmethod
    def copyTo(self, localSrc, remoteDst):
        pass

    @abstractmethod
    def copyFrom(self, remoteSrc, localDst):
        pass

    @abstractmethod
    def copyDirTo(self, localSrc, remoteDst):
        pass
