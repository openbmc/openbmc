SUMMARY = "X autotools macros"

DESCRIPTION = "M4 autotools macros used by various X.org programs."

require xorg-util-common.inc

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

PE = "1"

SRC_URI[md5sum] = "66cb74d4a0120a06e32c3b01c29417d8"
SRC_URI[sha256sum] = "624bb6c3a4613d18114a7e3849a3d70f2d7af9dc6eabeaba98060d87e3aef35b"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
