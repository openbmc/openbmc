require xorg-proto-common.inc

SUMMARY = "This package provides the basic headers for the X Window System"
DESCRIPTION = "The Present extension provides a way for applications to update their \
               window contents from a pixmap in a well defined fashion, synchronizing \
               with the display refresh and potentially using a more efficient \
               mechanism than copying the contents of the source pixmap.\
              "

LICENSE = "GPLv2"

SRCREV = "bfdc7e052302c79c5803ad95a73c9b63b350c40c"
PV = "1.1+git${SRCPV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=47e508ca280fde97906eacb77892c3ac"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/proto/presentproto"
S = "${WORKDIR}/git"

inherit autotools

BBCLASSEXTEND = "native"
