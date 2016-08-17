require xorg-proto-common.inc

SUMMARY = "This package provides the basic headers for the X Window System"
DESCRIPTION = "The Present extension provides a way for applications to update their \
               window contents from a pixmap in a well defined fashion, synchronizing \
               with the display refresh and potentially using a more efficient \
               mechanism than copying the contents of the source pixmap.\
              "

LICENSE = "GPLv2"

SRCREV = "24f3a56e541b0a9e6c6ee76081f441221a120ef9"
PV = "1.0+git${SRCPV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=47e508ca280fde97906eacb77892c3ac"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/proto/presentproto"
S = "${WORKDIR}/git"

PR = "r1"

inherit autotools

BBCLASSEXTEND = "native"
