# Python bindings for libcups, from the CUPS project.
#
# Copyright (c) Ambu A/S - All rights reserved
# SPDX-License-Identifier: MIT
#
# Author(s)
#   clst@ambu.com (Claus Stovgaard)
#

DESCRIPTION = "pycups - CUPS bindings for Python"
HOMEPAGE = "https://github.com/OpenPrinting/pycups"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS += "cups"

# See https://pypi.org/project/pycups/ for data
SRC_URI[sha256sum] = "843e385c1dbf694996ca84ef02a7f30c28376035588f5fbeacd6bae005cf7c8d"

inherit pypi python_setuptools_build_meta
