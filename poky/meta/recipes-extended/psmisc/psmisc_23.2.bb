require psmisc.inc
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://gitlab.com/psmisc/psmisc.git;protocol=https \
           file://0001-Use-UINTPTR_MAX-instead-of-__WORDSIZE.patch \
	   file://0001-Makefile.am-create-src-directory-before-attempting-t.patch \
           "
SRCREV = "44eab9a3a63394eae6b79a7ef0a042f57e0c8a8f"
S = "${WORKDIR}/git"
