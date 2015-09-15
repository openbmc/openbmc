require xorg-lib-common.inc

SUMMARY = "DMX: Distributed Multihead X extension library"

DESCRIPTION = "The DMX extension provides support for communication with \
and control of Xdmx(1) server. Attributes of the Xdmx(1) server and of \
the back-end screens attached to the server can be queried and modified \
via this protocol."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a3c3499231a8035efd0e004cfbd3b72a \
                    file://src/dmx.c;endline=33;md5=c43f19af03c7c8619cadc9724ed9afe1"

DEPENDS += "libxext dmxproto"

PE = "1"

SRC_URI[md5sum] = "ba983eba5a9f05d152a0725b8e863151"
SRC_URI[sha256sum] = "c97da36d2e56a2d7b6e4f896241785acc95e97eb9557465fd66ba2a155a7b201"

