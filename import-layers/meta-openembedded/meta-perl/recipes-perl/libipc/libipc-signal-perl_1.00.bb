SUMMARY = "IPC::Signal - Utility functions dealing with signals"
DESCRIPTION = "IPC::Signal - This module contains utility functions for \
dealing with signals."

HOMEPAGE = "http://search.cpan.org/~rosch/IPC-Signal-1.00/"
SECTION = "libs"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=16;endline=18;md5=f36550f59a0ae5e6e3b0be6a4da60d26"

S = "${WORKDIR}/IPC-Signal-${PV}"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/R/RO/ROSCH/IPC-Signal-${PV}.tar.gz"

SRC_URI[md5sum] = "4cebf17fdf1785eaf8c151bf2e8c360a"
SRC_URI[sha256sum] = "7c21f9c8c2d0c0f0f0f46e77de7c3d879dd562668ddf0525875c38cef2076fd0"

inherit cpan
