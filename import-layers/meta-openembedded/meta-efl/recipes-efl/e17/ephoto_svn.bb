LICENSE = "MIT & GPL-3.0"
PV = "0.1.0+svnr${SRCREV}"
PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=fdffcddb366d0cc78e0e46c4ea70c8d9 \
                    file://COPYING.icons;md5=8f0e2cd40e05189ec81232da84bd6e1a"

require e-module.inc

inherit gettext

DEPENDS += "elementary ethumb ecore eio"

SRCNAME = "${PN}"

# autotools-brokensep
B = "${S}"

SRC_URI += "file://configure.patch"
