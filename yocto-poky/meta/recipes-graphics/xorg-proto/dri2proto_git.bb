require xorg-proto-common.inc

SUMMARY = "DRI2: Direct Rendering Infrastructure 2 headers"

DESCRIPTION = "This package provides the wire protocol for the Direct \
Rendering Ifnrastructure 2.  DIR is required for may hardware \
accelerated OpenGL drivers."

SRCREV = "66c56ab10d917e3f47f93178d7eac6430970d3c4"
PV = "1.99.3+git${SRCPV}"
PR = "r2"

SRC_URI = "git://anongit.freedesktop.org/git/xorg/proto/dri2proto"

LIC_FILES_CHKSUM="file://COPYING;md5=2e396fa91834f8786032cad2da5638f3"

S = "${WORKDIR}/git"

