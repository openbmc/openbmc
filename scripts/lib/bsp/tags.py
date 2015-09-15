# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2012, Intel Corporation.
# All rights reserved.
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
#
# DESCRIPTION
# This module provides a place to define common constants for the
# Yocto BSP Tools.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] intel.com>
#

OPEN_TAG =     "{{"
CLOSE_TAG =    "}}"
ASSIGN_TAG =   "{{="
INPUT_TAG =    "input"
IF_TAG =       "if"
FILENAME_TAG = "yocto-bsp-filename"
DIRNAME_TAG =  "yocto-bsp-dirname"

INDENT_STR =  "    "

BLANKLINE_STR = "of.write(\"\\n\")"
NORMAL_START =  "of.write"
OPEN_START =    "current_file ="

INPUT_TYPE_PROPERTY = "type"

SRC_URI_FILE = "file://"

GIT_CHECK_URI = "git://git.yoctoproject.org/linux-yocto-dev.git"



