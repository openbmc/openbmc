SUMMARY = "C and C++ INI Library"
HOMEPAGE = "http://code.google.com/p/inih/"
PV = "0.0+gitr${SRCPV}"
PKGV = "${GITPKGVTAG}"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dae28cbf28207425e0d0b3eb835f4bde"

PR = "r3"

# The github repository provides a cmake and pkg-config integration
SRCREV = "25078f7156eb8647b3b35dd25f9ae6f8c4ee0589"
SRC_URI = "git://github.com/OSSystems/inih.git"

S = "${WORKDIR}/git"

inherit gitpkgv cmake

# We don't have libinih since we only have static libraries
ALLOW_EMPTY_${PN} = "1"
