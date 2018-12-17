require xorg-lib-common.inc

SUMMARY = "DMX: Distributed Multihead X extension library"

DESCRIPTION = "The DMX extension provides support for communication with \
and control of Xdmx(1) server. Attributes of the Xdmx(1) server and of \
the back-end screens attached to the server can be queried and modified \
via this protocol."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a3c3499231a8035efd0e004cfbd3b72a \
                    file://src/dmx.c;endline=33;md5=c43f19af03c7c8619cadc9724ed9afe1"

DEPENDS += "libxext xorgproto"

PE = "1"

SRC_URI[md5sum] = "d2f1f0ec68ac3932dd7f1d9aa0a7a11c"
SRC_URI[sha256sum] = "253f90005d134fa7a209fbcbc5a3024335367c930adf0f3203e754cf32747243"

