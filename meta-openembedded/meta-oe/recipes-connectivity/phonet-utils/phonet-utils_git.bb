SUMMARY = "This small package provides a few command line tools for Linux Phonet"
HOMEPAGE = ""
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRC_URI = "git://gitorious.org/meego-cellular/phonet-utils.git;branch=master \
           file://0001-Include-limits.h-for-PATH_MAX.patch \
           "
PR = "r2"
S = "${WORKDIR}/git"
SRCREV = "4acfa720fd37d178a048fc2be17180137d4a70ea"
PV = "0.0.0+gitr${SRCPV}"

inherit autotools-brokensep

FILES_${PN} += "${base_libdir}/udev/rules.d/85-phonet-utils.rules"
