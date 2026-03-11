# libdbi OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION = "Database Independent Abstraction Layer for C"
HOMEPAGE = "http://libdbi.sourceforge.net/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"
SECTION = "libs"

INC_PR = "r0"

SRC_URI = "${SOURCEFORGE_MIRROR}/libdbi/libdbi-${PV}.tar.gz"

inherit autotools

EXTRA_OECONF = "--disable-docs"
