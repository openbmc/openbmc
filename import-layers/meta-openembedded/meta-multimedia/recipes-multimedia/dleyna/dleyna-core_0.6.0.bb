SUMMARY = "Utility functions for dLeyna libraries"
DESCRIPTION = "dleyna-core is a library of utility functions that are used \
by the higher level dLeyna libraries that communicate with DLNA devices, \
e.g., dleyna-server. In brief, it provides APIs for logging, error, settings \
and task management and an IPC asbstraction API."
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libdleyna/core/core.c;endline=21;md5=139cba0c634344abc9456694fbb5083b"

DEPENDS = "glib-2.0 gupnp"

SRC_URI = "git://github.com/01org/${BPN}.git"
SRCREV = "27a3786ec013f64fd58243410a60798f824acec3"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
