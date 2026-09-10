# libdbi OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION = "Database Independent Abstraction Layer for C"
HOMEPAGE = "http://libdbi.sourceforge.net/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"
SECTION = "libs"

SRC_URI = "${SOURCEFORGE_MIRROR}/libdbi/libdbi-${PV}.tar.gz"
# SourceForge download area tops out at libdbi-0.8.2; PV 0.9.0 exists only
# outside the browsable files area, so no checkable listing maps to PV.
UPSTREAM_VERSION_UNKNOWN = "1"

inherit autotools

EXTRA_OECONF = "--disable-docs"
