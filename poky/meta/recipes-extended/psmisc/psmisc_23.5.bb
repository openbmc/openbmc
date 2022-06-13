require psmisc.inc
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://gitlab.com/psmisc/psmisc.git;protocol=https;branch=master \
           file://0001-Use-UINTPTR_MAX-instead-of-__WORDSIZE.patch \
           "
SRCREV = "17353f3fddef6b360bc47f7834f2cd011bea2b87"
S = "${WORKDIR}/git"
