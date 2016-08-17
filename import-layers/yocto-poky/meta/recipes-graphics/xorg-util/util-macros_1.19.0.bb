SUMMARY = "X autotools macros"

DESCRIPTION = "M4 autotools macros used by various X.org programs."

require xorg-util-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

PE = "1"

SRC_URI[md5sum] = "40e1caa49a71a26e0aa68ddd00203717"
SRC_URI[sha256sum] = "0d4df51b29023daf2f63aebf3ebc638ea88efedfd560ab5866741ab3f92acaa1"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
