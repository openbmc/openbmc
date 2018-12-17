SUMMARY = "X autotools macros"

DESCRIPTION = "M4 autotools macros used by various X.org programs."

require xorg-util-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

PE = "1"

SRC_URI[md5sum] = "5059b328fac086b733ffac6607164c41"
SRC_URI[sha256sum] = "9225c45c3de60faf971979a55a5536f3562baa4b6f02246c23e98ac0c09a75b7"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
