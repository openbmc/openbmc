SUMMARY = "C and C++ INI Library"
HOMEPAGE = "http://code.google.com/p/inih/"
PV = "0.0+gitr${SRCPV}"
PKGV = "${GITPKGVTAG}"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dae28cbf28207425e0d0b3eb835f4bde"

PR = "r3"

# The github repository provides a cmake and pkg-config integration
SRCREV = "c858aff8c31fa63ef4d1e0176c10e5928cde9a23"
SRC_URI = "git://github.com/OSSystems/inih.git \
          "

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit gitpkgv cmake

# We don't have libinih since we only have static libraries
ALLOW_EMPTY_${PN} = "1"
