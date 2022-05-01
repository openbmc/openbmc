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
import logging.config
import os
from itertools import groupby
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
    ERRORONCE = logging.ERROR - 1
    WARNING = logging.WARNING
    WARNONCE = logging.WARNING - 1
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
        WARNONCE : 'WARNING',
        ERROR   : 'ERROR',
        ERRORONCE   : 'ERROR',
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
        WARNONCE : YELLOW,
        ERROR   : RED,
        ERRORONCE : RED,
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

    def __repr__(self):
        return "%s fmt='%s' color=%s" % (self.__class__.__name__, self._fmt, "True" if self.color_enabled else "False")

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

class LogFilterShowOnce(logging.Filter):
    def __init__(self):
        self.seen_warnings = set()
        self.seen_errors = set()

    def filter(self, record):
        if record.levelno == bb.msg.BBLogFormatter.WARNONCE:
            if record.msg in self.seen_warnings:
                return False
            self.seen_warnings.add(record.msg)
        if record.levelno == bb.msg.BBLogFormatter.ERRORONCE:
            if record.msg in self.seen_errors:
                return False
            self.seen_errors.add(record.msg)
        return True

class LogFilterGEQLevel(logging.Filter):
    def __init__(self, level):
        self.strlevel = str(level)
        self.level = stringToLevel(level)

    def __repr__(self):
        return "%s level >= %s (%d)" % (self.__class__.__name__, self.strlevel, self.level)

    def filter(self, record):
        return (record.levelno >= self.level)

class LogFilterLTLevel(logging.Filter):
    def __init__(self, level):
        self.strlevel = str(level)
        self.level = stringToLevel(level)

    def __repr__(self):
        return "%s level < %s (%d)" % (self.__class__.__name__, self.strlevel, self.level)

    def filter(self, record):
        return (record.levelno < self.level)

# Message control functions
#

loggerDefaultLogLevel = BBLogFormatter.NOTE
loggerDefaultDomains = {}

def init_msgconfig(verbose, debug, debug_domains=None):
    """
    Set default verbosity and debug levels config the logger
    """
    if debug:
        bb.msg.loggerDefaultLogLevel = BBLogFormatter.DEBUG - debug + 1
    elif verbose:
        bb.msg.loggerDefaultLogLevel = BBLogFormatter.VERBOSE
    else:
        bb.msg.loggerDefaultLogLevel = BBLogFormatter.NOTE

    bb.msg.loggerDefaultDomains = {}
    if debug_domains:
        for (domainarg, iterator) in groupby(debug_domains):
            dlevel = len(tuple(iterator))
            bb.msg.loggerDefaultDomains["BitBake.%s" % domainarg] = logging.DEBUG - dlevel + 1

def constructLogOptions():
    return loggerDefaultLogLevel, loggerDefaultDomains

def addDefaultlogFilter(handler, cls = BBLogFilter, forcelevel=None):
    level, debug_domains = constructLogOptions()

    if forcelevel is not None:
        level = forcelevel

    cls(handler, level, debug_domains)

def stringToLevel(level):
    try:
        return int(level)
    except ValueError:
        pass

    try:
        return getattr(logging, level)
    except AttributeError:
        pass

    return getattr(BBLogFormatter, level)

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
    console.addFilter(bb.msg.LogFilterShowOnce())
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

def mergeLoggingConfig(logconfig, userconfig):
    logconfig = copy.deepcopy(logconfig)
    userconfig = copy.deepcopy(userconfig)

    # Merge config with the default config
    if userconfig.get('version') != logconfig['version']:
        raise BaseException("Bad user configuration version. Expected %r, got %r" % (logconfig['version'], userconfig.get('version')))

    # Set some defaults to make merging easier
    userconfig.setdefault("loggers", {})

    # If a handler, formatter, or filter is defined in the user
    # config, it will replace an existing one in the default config
    for k in ("handlers", "formatters", "filters"):
        logconfig.setdefault(k, {}).update(userconfig.get(k, {}))

    seen_loggers = set()
    for name, l in logconfig["loggers"].items():
        # If the merge option is set, merge the handlers and
        # filters. Otherwise, if it is False, this logger won't get
        # add to the set of seen loggers and will replace the
        # existing one
        if l.get('bitbake_merge', True):
            ulogger = userconfig["loggers"].setdefault(name, {})
            ulogger.setdefault("handlers", [])
            ulogger.setdefault("filters", [])

            # Merge lists
            l.setdefault("handlers", []).extend(ulogger["handlers"])
            l.setdefault("filters", []).extend(ulogger["filters"])

            # Replace other properties if present
            if "level" in ulogger:
                l["level"] = ulogger["level"]

            if "propagate" in ulogger:
                l["propagate"] = ulogger["propagate"]

            seen_loggers.add(name)

    # Add all loggers present in the user config, but not any that
    # have already been processed
    for name in set(userconfig["loggers"].keys()) - seen_loggers:
        logconfig["loggers"][name] = userconfig["loggers"][name]

    return logconfig

def setLoggingConfig(defaultconfig, userconfigfile=None):
    logconfig = copy.deepcopy(defaultconfig)

    if userconfigfile:
        with open(os.path.normpath(userconfigfile), 'r') as f:
            if userconfigfile.endswith('.yml') or userconfigfile.endswith('.yaml'):
                import yaml
                userconfig = yaml.safe_load(f)
            elif userconfigfile.endswith('.json') or userconfigfile.endswith('.cfg'):
                import json
                userconfig = json.load(f)
            else:
                raise BaseException("Unrecognized file format: %s" % userconfigfile)

            if userconfig.get('bitbake_merge', True):
                logconfig = mergeLoggingConfig(logconfig, userconfig)
            else:
                # Replace the entire default config
                logconfig = userconfig

    # Convert all level parameters to integers in case users want to use the
    # bitbake defined level names
    for name, h in logconfig["handlers"].items():
        if "level" in h:
            h["level"] = bb.msg.stringToLevel(h["level"])

        # Every handler needs its own instance of the once filter.
        once_filter_name = name + ".showonceFilter"
        logconfig.setdefault("filters", {})[once_filter_name] = {
            "()": "bb.msg.LogFilterShowOnce",
        }
        h.setdefault("filters", []).append(once_filter_name)

    for l in logconfig["loggers"].values():
        if "level" in l:
            l["level"] = bb.msg.stringToLevel(l["level"])

    conf = logging.config.dictConfigClass(logconfig)
    conf.configure()

    # The user may have specified logging domains they want at a higher debug
    # level than the standard.
    for name, l in logconfig["loggers"].items():
        if not name.startswith("BitBake."):
            continue

        if not "level" in l:
            continue

        curlevel = bb.msg.loggerDefaultDomains.get(name)
        # Note: level parameter should already be a int because of conversion
        # above
        newlevel = int(l["level"])
        if curlevel is None or newlevel < curlevel:
            bb.msg.loggerDefaultDomains[name] = newlevel

        # TODO: I don't think that setting the global log level should be necessary
        #if newlevel < bb.msg.loggerDefaultLogLevel:
        #    bb.msg.loggerDefaultLogLevel = newlevel

    return conf
