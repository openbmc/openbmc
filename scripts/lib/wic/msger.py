#!/usr/bin/env python -tt
# vim: ai ts=4 sts=4 et sw=4
#
# Copyright (c) 2009, 2010, 2011 Intel, Inc.
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

import os
import sys
import re
import time

__ALL__ = ['set_mode',
           'get_loglevel',
           'set_loglevel',
           'set_logfile',
           'raw',
           'debug',
           'verbose',
           'info',
           'warning',
           'error',
           'ask',
           'pause',
          ]

# COLORs in ANSI
INFO_COLOR = 32 # green
WARN_COLOR = 33 # yellow
ERR_COLOR = 31 # red
ASK_COLOR = 34 # blue
NO_COLOR = 0

PREFIX_RE = re.compile('^<(.*?)>\s*(.*)', re.S)

INTERACTIVE = True

LOG_LEVEL = 1
LOG_LEVELS = {
    'quiet': 0,
    'normal': 1,
    'verbose': 2,
    'debug': 3,
    'never': 4,
}

LOG_FILE_FP = None
LOG_CONTENT = ''
CATCHERR_BUFFILE_FD = -1
CATCHERR_BUFFILE_PATH = None
CATCHERR_SAVED_2 = -1

def _general_print(head, color, msg=None, stream=None, level='normal'):
    global LOG_CONTENT
    if not stream:
        stream = sys.stdout

    if LOG_LEVELS[level] > LOG_LEVEL:
        # skip
        return

    # encode raw 'unicode' str to utf8 encoded str
    if msg and isinstance(msg, unicode):
        msg = msg.encode('utf-8', 'ignore')

    errormsg = ''
    if CATCHERR_BUFFILE_FD > 0:
        size = os.lseek(CATCHERR_BUFFILE_FD, 0, os.SEEK_END)
        os.lseek(CATCHERR_BUFFILE_FD, 0, os.SEEK_SET)
        errormsg = os.read(CATCHERR_BUFFILE_FD, size)
        os.ftruncate(CATCHERR_BUFFILE_FD, 0)

    # append error msg to LOG
    if errormsg:
        LOG_CONTENT += errormsg

    # append normal msg to LOG
    save_msg = msg.strip() if msg else None
    if save_msg:
        timestr = time.strftime("[%m/%d %H:%M:%S %Z] ", time.localtime())
        LOG_CONTENT += timestr + save_msg + '\n'

    if errormsg:
        _color_print('', NO_COLOR, errormsg, stream, level)

    _color_print(head, color, msg, stream, level)

def _color_print(head, color, msg, stream, level):
    colored = True
    if color == NO_COLOR or \
       not stream.isatty() or \
       os.getenv('ANSI_COLORS_DISABLED') is not None:
        colored = False

    if head.startswith('\r'):
        # need not \n at last
        newline = False
    else:
        newline = True

    if colored:
        head = '\033[%dm%s:\033[0m ' %(color, head)
        if not newline:
            # ESC cmd to clear line
            head = '\033[2K' + head
    else:
        if head:
            head += ': '
            if head.startswith('\r'):
                head = head.lstrip()
                newline = True

    if msg is not None:
        if isinstance(msg, unicode):
            msg = msg.encode('utf8', 'ignore')

        stream.write('%s%s' % (head, msg))
        if newline:
            stream.write('\n')

    stream.flush()

def _color_perror(head, color, msg, level='normal'):
    if CATCHERR_BUFFILE_FD > 0:
        _general_print(head, color, msg, sys.stdout, level)
    else:
        _general_print(head, color, msg, sys.stderr, level)

def _split_msg(head, msg):
    if isinstance(msg, list):
        msg = '\n'.join(map(str, msg))

    if msg.startswith('\n'):
        # means print \n at first
        msg = msg.lstrip()
        head = '\n' + head

    elif msg.startswith('\r'):
        # means print \r at first
        msg = msg.lstrip()
        head = '\r' + head

    match = PREFIX_RE.match(msg)
    if match:
        head += ' <%s>' % match.group(1)
        msg = match.group(2)

    return head, msg

def get_loglevel():
    return (k for k, v in LOG_LEVELS.items() if v == LOG_LEVEL).next()

def set_loglevel(level):
    global LOG_LEVEL
    if level not in LOG_LEVELS:
        # no effect
        return

    LOG_LEVEL = LOG_LEVELS[level]

def set_interactive(mode=True):
    global INTERACTIVE
    if mode:
        INTERACTIVE = True
    else:
        INTERACTIVE = False

def log(msg=''):
    # log msg to LOG_CONTENT then save to logfile
    global LOG_CONTENT
    if msg:
        LOG_CONTENT += msg

def raw(msg=''):
    _general_print('', NO_COLOR, msg)

def info(msg):
    head, msg = _split_msg('Info', msg)
    _general_print(head, INFO_COLOR, msg)

def verbose(msg):
    head, msg = _split_msg('Verbose', msg)
    _general_print(head, INFO_COLOR, msg, level='verbose')

def warning(msg):
    head, msg = _split_msg('Warning', msg)
    _color_perror(head, WARN_COLOR, msg)

def debug(msg):
    head, msg = _split_msg('Debug', msg)
    _color_perror(head, ERR_COLOR, msg, level='debug')

def error(msg):
    head, msg = _split_msg('Error', msg)
    _color_perror(head, ERR_COLOR, msg)
    sys.exit(1)

def ask(msg, default=True):
    _general_print('\rQ', ASK_COLOR, '')
    try:
        if default:
            msg += '(Y/n) '
        else:
            msg += '(y/N) '
        if INTERACTIVE:
            while True:
                repl = raw_input(msg)
                if repl.lower() == 'y':
                    return True
                elif repl.lower() == 'n':
                    return False
                elif not repl.strip():
                    # <Enter>
                    return default

                # else loop
        else:
            if default:
                msg += ' Y'
            else:
                msg += ' N'
            _general_print('', NO_COLOR, msg)

            return default
    except KeyboardInterrupt:
        sys.stdout.write('\n')
        sys.exit(2)

def choice(msg, choices, default=0):
    if default >= len(choices):
        return None
    _general_print('\rQ', ASK_COLOR, '')
    try:
        msg += " [%s] " % '/'.join(choices)
        if INTERACTIVE:
            while True:
                repl = raw_input(msg)
                if repl in choices:
                    return repl
                elif not repl.strip():
                    return choices[default]
        else:
            msg += choices[default]
            _general_print('', NO_COLOR, msg)

            return choices[default]
    except KeyboardInterrupt:
        sys.stdout.write('\n')
        sys.exit(2)

def pause(msg=None):
    if INTERACTIVE:
        _general_print('\rQ', ASK_COLOR, '')
        if msg is None:
            msg = 'press <ENTER> to continue ...'
        raw_input(msg)

def set_logfile(fpath):
    global LOG_FILE_FP

    def _savelogf():
        if LOG_FILE_FP:
            with open(LOG_FILE_FP, 'w') as log:
                log.write(LOG_CONTENT)

    if LOG_FILE_FP is not None:
        warning('duplicate log file configuration')

    LOG_FILE_FP = fpath

    import atexit
    atexit.register(_savelogf)

def enable_logstderr(fpath):
    global CATCHERR_BUFFILE_FD
    global CATCHERR_BUFFILE_PATH
    global CATCHERR_SAVED_2

    if os.path.exists(fpath):
        os.remove(fpath)
    CATCHERR_BUFFILE_PATH = fpath
    CATCHERR_BUFFILE_FD = os.open(CATCHERR_BUFFILE_PATH, os.O_RDWR|os.O_CREAT)
    CATCHERR_SAVED_2 = os.dup(2)
    os.dup2(CATCHERR_BUFFILE_FD, 2)

def disable_logstderr():
    global CATCHERR_BUFFILE_FD
    global CATCHERR_BUFFILE_PATH
    global CATCHERR_SAVED_2

    raw(msg=None) # flush message buffer and print it.
    os.dup2(CATCHERR_SAVED_2, 2)
    os.close(CATCHERR_SAVED_2)
    os.close(CATCHERR_BUFFILE_FD)
    os.unlink(CATCHERR_BUFFILE_PATH)
    CATCHERR_BUFFILE_FD = -1
    CATCHERR_BUFFILE_PATH = None
    CATCHERR_SAVED_2 = -1
