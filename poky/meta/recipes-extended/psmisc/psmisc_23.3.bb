require psmisc.inc
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://gitlab.com/psmisc/psmisc.git;protocol=https \
           file://0001-Use-UINTPTR_MAX-instead-of-__WORDSIZE.patch \
	   file://0001-Makefile.am-create-src-directory-before-attempting-t.patch \
           "
SRCREV = "78bde849041e6c914a2a517ebe1255b86dc98772"
S = "${WORKDIR}/git"
