SUMMARY = "X autotools macros"

DESCRIPTION = "M4 autotools macros used by various X.org programs."

require xorg-util-common.inc

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=42ba50748cb7ccf8739424e5e2072b02"

PE = "1"

SRC_URI += "file://0001-xorg-macros.m4.in-do-not-run-AC_CANONICAL_HOST-in-ma.patch"

SRC_URI[sha256sum] = "b373f72887b1394ce2193180a60cb0d1fb8b17bc96ddd770cfd7a808cb489a15"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
DEV_PKG_DEPENDENCY = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
