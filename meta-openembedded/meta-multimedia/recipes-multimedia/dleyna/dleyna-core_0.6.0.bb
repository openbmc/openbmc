SUMMARY = "Utility functions for dLeyna libraries"
DESCRIPTION = "dleyna-core is a library of utility functions that are used \
by the higher level dLeyna libraries that communicate with DLNA devices, \
e.g., dleyna-server. In brief, it provides APIs for logging, error, settings \
and task management and an IPC asbstraction API."
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libdleyna/core/core.c;endline=21;md5=68602998351825b0844aae34c684c54e"

DEPENDS = "glib-2.0 gupnp"

PV .= "+git"

SRC_URI = "git://github.com/01org/${BPN}.git;branch=master;protocol=https"
SRCREV = "1c6853f5bc697dc0a8774fd70dbc915c4dbe7c5b"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

SKIP_RECIPE[dleyna-core] ?= "Upstream is dead moreover needs porting to work with latest gupnp >= 1.2"
