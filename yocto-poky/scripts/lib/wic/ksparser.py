#!/usr/bin/env python -tt
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2016 Intel, Inc.
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
#
# DESCRIPTION
# This module provides parser for kickstart format
#
# AUTHORS
# Ed Bartosh <ed.bartosh> (at] linux.intel.com>

"""Kickstart parser module."""

import os
import shlex
from argparse import ArgumentParser, ArgumentError, ArgumentTypeError

from wic import msger
from wic.partition import Partition
from wic.utils.misc import find_canned

class KickStartError(Exception):
    """Custom exception."""
    pass

class KickStartParser(ArgumentParser):
    """
    This class overwrites error method to throw exception
    instead of producing usage message(default argparse behavior).
    """
    def error(self, message):
        raise ArgumentError(None, message)

def sizetype(arg):
    """
    Custom type for ArgumentParser
    Converts size string in <num>[K|k|M|G] format into the integer value
    """
    if arg.isdigit():
        return int(arg) * 1024L

    if not arg[:-1].isdigit():
        raise ArgumentTypeError("Invalid size: %r" % arg)

    size = int(arg[:-1])
    if arg.endswith("k") or arg.endswith("K"):
        return size
    if arg.endswith("M"):
        return size * 1024L
    if arg.endswith("G"):
        return size * 1024L * 1024L

    raise ArgumentTypeError("Invalid size: %r" % arg)

def overheadtype(arg):
    """
    Custom type for ArgumentParser
    Converts overhead string to float and checks if it's bigger than 1.0
    """
    try:
        result = float(arg)
    except ValueError:
        raise ArgumentTypeError("Invalid value: %r" % arg)

    if result < 1.0:
        raise ArgumentTypeError("Overhead factor should be > 1.0" % arg)

    return result

def cannedpathtype(arg):
    """
    Custom type for ArgumentParser
    Tries to find file in the list of canned wks paths
    """
    scripts_path = os.path.abspath(os.path.dirname(__file__) + '../../..')
    result = find_canned(scripts_path, arg)
    if not result:
        raise ArgumentTypeError("file not found: %s" % arg)
    return result

class KickStart(object):
    """"Kickstart parser implementation."""

    def __init__(self, confpath):

        self.partitions = []
        self.bootloader = None
        self.lineno = 0
        self.partnum = 0

        parser = KickStartParser()
        subparsers = parser.add_subparsers()

        part = subparsers.add_parser('part')
        part.add_argument('mountpoint')
        part.add_argument('--active', action='store_true')
        part.add_argument('--align', type=int)
        part.add_argument("--extra-space", type=sizetype, default=10*1024L)
        part.add_argument('--fsoptions', dest='fsopts')
        part.add_argument('--fstype')
        part.add_argument('--label')
        part.add_argument('--no-table', action='store_true')
        part.add_argument('--ondisk', '--ondrive', dest='disk')
        part.add_argument("--overhead-factor", type=overheadtype, default=1.3)
        part.add_argument('--part-type')
        part.add_argument('--rootfs-dir')
        part.add_argument('--size', type=sizetype, default=0)
        part.add_argument('--source')
        part.add_argument('--sourceparams')
        part.add_argument('--use-uuid', action='store_true')
        part.add_argument('--uuid')

        bootloader = subparsers.add_parser('bootloader')
        bootloader.add_argument('--append')
        bootloader.add_argument('--configfile')
        bootloader.add_argument('--ptable', choices=('msdos', 'gpt'),
                                default='msdos')
        bootloader.add_argument('--timeout', type=int)
        bootloader.add_argument('--source')

        include = subparsers.add_parser('include')
        include.add_argument('path', type=cannedpathtype)

        self._parse(parser, confpath)
        if not self.bootloader:
            msger.warning('bootloader config not specified, using defaults')
            self.bootloader = bootloader.parse_args([])

    def _parse(self, parser, confpath):
        """
        Parse file in .wks format using provided parser.
        """
        with open(confpath) as conf:
            lineno = 0
            for line in conf:
                line = line.strip()
                lineno += 1
                if line and line[0] != '#':
                    try:
                        parsed = parser.parse_args(shlex.split(line))
                    except ArgumentError as err:
                        raise KickStartError('%s:%d: %s' % \
                                             (confpath, lineno, err))
                    if line.startswith('part'):
                        self.partnum += 1
                        self.partitions.append(Partition(parsed, self.partnum))
                    elif line.startswith('include'):
                        self._parse(parser, parsed.path)
                    elif line.startswith('bootloader'):
                        if not self.bootloader:
                            self.bootloader = parsed
                        else:
                            err = "%s:%d: more than one bootloader specified" \
                                      % (confpath, lineno)
                            raise KickStartError(err)
