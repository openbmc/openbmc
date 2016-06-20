require psmisc.inc
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI[md5sum] = "935c0fd6eb208288262b385fa656f1bf"
SRC_URI[sha256sum] = "97323cad619210845b696d7d722c383852b2acb5c49b5b0852c4f29c77a8145a"

SRC_URI = "${SOURCEFORGE_MIRROR}/psmisc/psmisc-${PV}.tar.gz \
           file://0001-Typo-in-fuser-makes-M-on-all-the-time.patch \
           file://0002-Include-limits.h-for-PATH_MAX.patch \
           file://0001-Use-UINTPTR_MAX-instead-of-__WORDSIZE.patch \
           "
