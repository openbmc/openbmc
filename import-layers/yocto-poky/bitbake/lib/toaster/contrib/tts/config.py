#!/usr/bin/python

# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2015 Alexandru Damian for Intel Corp.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# This is the configuration/single module for tts
# everything that would be a global variable goes here

import os, sys, logging
import socket

LOGDIR = "log"
SETTINGS_FILE = os.path.join(os.path.dirname(__file__), "settings.json")
TEST_DIR_NAME = "tts_testdir"

DEBUG = True

OWN_PID = os.getpid()

W3C_VALIDATOR = "http://icarus.local/w3c-validator/check?doctype=HTML5&uri="

TOASTER_PORT = 56789

TESTDIR = None

#we parse the w3c URL to know where to connect

import urlparse

def get_public_ip():
    temp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    parsed_url = urlparse.urlparse("http://icarus.local/w3c-validator/check?doctype=HTML5&uri=")
    temp_socket.connect((parsed_url.netloc, 80 if parsed_url.port is None else parsed_url.port))
    public_ip = temp_socket.getsockname()[0]
    temp_socket.close()
    return public_ip

TOASTER_BASEURL = "http://%s:%d/" % (get_public_ip(), TOASTER_PORT)


OWN_EMAIL_ADDRESS = "Toaster Testing Framework <alexandru.damian@intel.com>"
REPORT_EMAIL_ADDRESS = "alexandru.damian@intel.com"

# make sure we have the basic logging infrastructure

#pylint: disable=invalid-name
# we disable the invalid name because the module-level "logger" is used througout bitbake
logger = logging.getLogger("toastertest")
__console__ = logging.StreamHandler(sys.stdout)
__console__.setFormatter(logging.Formatter("%(asctime)s %(levelname)s: %(message)s"))
logger.addHandler(__console__)
logger.setLevel(logging.DEBUG)


# singleton file names
LOCKFILE = "/tmp/ttf.lock"
BACKLOGFILE = os.path.join(os.path.dirname(__file__), "backlog.txt")

# task states
def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    reverse = dict((value, key) for key, value in enums.iteritems())
    enums['reverse_mapping'] = reverse
    return type('Enum', (), enums)


class TASKS(object):
    #pylint: disable=too-few-public-methods
    PENDING = "PENDING"
    INPROGRESS = "INPROGRESS"
    DONE = "DONE"

    @staticmethod
    def next_task(task):
        if task == TASKS.PENDING:
            return TASKS.INPROGRESS
        if task == TASKS.INPROGRESS:
            return TASKS.DONE
        raise Exception("Invalid next task state for %s" % task)

# TTS specific
CONTRIB_REPO = "git@git.yoctoproject.org:poky-contrib"

