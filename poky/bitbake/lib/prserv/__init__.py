#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

__version__ = "1.0.0"

import os, time
import sys,logging

def init_logger(logfile, loglevel):
    numeric_level = getattr(logging, loglevel.upper(), None)
    if not isinstance(numeric_level, int):
        raise ValueError('Invalid log level: %s' % loglevel)
    FORMAT = '%(asctime)-15s %(message)s'
    logging.basicConfig(level=numeric_level, filename=logfile, format=FORMAT)

class NotFoundError(Exception):
    pass
