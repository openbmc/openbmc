"""
BitBake 'msg' implementation

Message handling infrastructure for bitbake

"""

# Copyright (C) 2006        Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import copy
import logging
import collections
from itertools import groupby
import warnings
import bb
import bb.event

class BBLogFormatter(logging.Formatter):
    """Formatter which ensures that our 'plain' messages (logging.INFO + 1) are used as is"""

    DEBUG3 = logging.DEBUG - 2
    DEBUG2 = logging.DEBUG - 1
    DEBUG = logging.DEBUG
    VERBOSE = logging.INFO - 1
    NOTE = logging.INFO
    PLAIN = logging.INFO + 1
    VERBNOTE = logging.INFO + 2
    ERROR = logging.ERROR
    WARNING = logging.WARNING
    CRITICAL = logging.CRITICAL

    levelnames = {
        DEBUG3   : 'DEBUG',
        DEBUG2   : 'DEBUG',
        DEBUG   : 'DEBUG',
        VERBOSE: 'NOTE',
        NOTE    : 'NOTE',
        PLAIN  : '',
        VERBNOTE: 'NOTE',
        WARNING : 'WARNING',
        ERROR   : 'ERROR',
        CRITICAL: 'ERROR',
    }

    color_enabled = False
    BASECOLOR, BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE = list(range(29,38))

    COLORS = {
        DEBUG3  : CYAN,
        DEBUG2  : CYAN,
        DEBUG   : CYAN,
        VERBOSE : BASECOLOR,
        NOTE    : BASECOLOR,
        PLAIN   : BASECOLOR,
        VERBNOTE: BASECOLOR,
        WARNING : YELLOW,
        ERROR   : RED,
        CRITICAL: RED,
    }

    BLD = '\033[1;%dm'
    STD = '\033[%dm'
    RST = '\033[0m'

    def getLevelName(self, levelno):
        try:
            return self.levelnames[levelno]
        except KeyError:
            self.levelnames[levelno] = value = 'Level %d' % levelno
            return value

    def format(self, record):
        record.levelname = self.getLevelName(record.levelno)
        if record.levelno == self.PLAIN:
            msg = record.getMessage()
        else:
            if self.color_enabled:
                record = self.colorize(record)
            msg = logging.Formatter.format(self, record)
        if hasattr(record, 'bb_exc_formatted'):
            msg += '\n' + ''.join(record.bb_exc_formatted)
        elif hasattr(record, 'bb_exc_info'):
            etype, value, tb = record.bb_exc_info
            formatted = bb.exceptions.format_exception(etype, value, tb, limit=5)
            msg += '\n' + ''.join(formatted)
        return msg

    def colorize(self, record):
        color = self.COLORS[record.levelno]
        if self.color_enabled and color is not None:
            record = copy.copy(record)
            record.levelname = "".join([self.BLD % color, record.levelname, self.RST])
            record.msg = "".join([self.STD % color, record.msg, self.RST])
        return record

    def enable_color(self):
        self.color_enabled = True

class BBLogFilter(object):
    def __init__(self, handler, level, debug_domains):
        self.stdlevel = level
        self.debug_domains = debug_domains
        loglevel = level
        for domain in debug_domains:
            if debug_domains[domain] < loglevel:
                loglevel = debug_domains[domain]
        handler.setLevel(loglevel)
        handler.addFilter(self)

    def filter(self, record):
        if record.levelno >= self.stdlevel:
            return True
        if record.name in self.debug_domains and record.levelno >= self.debug_domains[record.name]:
            return True
        return False

class BBLogFilterStdErr(BBLogFilter):
    def filter(self, record):
        if not BBLogFilter.filter(self, record):
            return False
        if record.levelno >= logging.ERROR:
            return True
        return False

class BBLogFilterStdOut(BBLogFilter):
    def filter(self, record):
        if not BBLogFilter.filter(self, record):
            return False
        if record.levelno < logging.ERROR:
            return True
        return False

# Message control functions
#

loggerDefaultDebugLevel = 0
loggerDefaultVerbose = False
loggerVerboseLogs = False
loggerDefaultDomains = []

def init_msgconfig(verbose, debug, debug_domains=None):
    """
    Set default verbosity and debug levels config the logger
    """
    bb.msg.loggerDefaultDebugLevel = debug
    bb.msg.loggerDefaultVerbose = verbose
    if verbose:
        bb.msg.loggerVerboseLogs = True
    if debug_domains:
        bb.msg.loggerDefaultDomains = debug_domains
    else:
        bb.msg.loggerDefaultDomains = []

def constructLogOptions():
    debug = loggerDefaultDebugLevel
    verbose = loggerDefaultVerbose
    domains = loggerDefaultDomains

    if debug:
        level = BBLogFormatter.DEBUG - debug + 1
    elif verbose:
        level = BBLogFormatter.VERBOSE
    else:
        level = BBLogFormatter.NOTE

    debug_domains = {}
    for (domainarg, iterator) in groupby(domains):
        dlevel = len(tuple(iterator))
        debug_domains["BitBake.%s" % domainarg] = logging.DEBUG - dlevel + 1
    return level, debug_domains

def addDefaultlogFilter(handler, cls = BBLogFilter, forcelevel=None):
    level, debug_domains = constructLogOptions()

    if forcelevel is not None:
        level = forcelevel

    cls(handler, level, debug_domains)

#
# Message handling functions
#

def fatal(msgdomain, msg):
    if msgdomain:
        logger = logging.getLogger("BitBake.%s" % msgdomain)
    else:
        logger = logging.getLogger("BitBake")
    logger.critical(msg)
    sys.exit(1)

def logger_create(name, output=sys.stderr, level=logging.INFO, preserve_handlers=False, color='auto'):
    """Standalone logger creation function"""
    logger = logging.getLogger(name)
    console = logging.StreamHandler(output)
    format = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
    if color == 'always' or (color == 'auto' and output.isatty()):
        format.enable_color()
    console.setFormatter(format)
    if preserve_handlers:
        logger.addHandler(console)
    else:
        logger.handlers = [console]
    logger.setLevel(level)
    return logger

def has_console_handler(logger):
    for handler in logger.handlers:
        if isinstance(handler, logging.StreamHandler):
            if handler.stream in [sys.stderr, sys.stdout]:
                return True
    return False
