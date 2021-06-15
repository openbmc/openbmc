#
# Copyright (C) 2012 Wind River Systems, Inc.
#
SUMMARY = "MCE test suite"

DESCRIPTION = "The MCE test suite is a collection of tools and test scripts for \
testing the Linux RAS related features, including CPU/Memory error \
containment and recovery, ACPI/APEI support etc."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://git.kernel.org/pub/scm/utils/cpu/mce/mce-test.git;protocol=git \
           file://makefile-remove-ldflags.patch \
           file://0001-gcov_merge.py-scov_merge.py-switch-to-python3.patch \
          "
SRCREV = "7643baf6c3919b3d727e6ba6c2e545dc6a653307"
PV = "20190917+git${SRCPV}"

RDEPENDS_${PN} = "mcelog mce-inject dialog bash"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

S ="${WORKDIR}/git"

do_install_append(){
   install -d ${D}/opt/mce-test
   cp -rf ${S}/* ${D}/opt/mce-test/
}

FILES_${PN} += "/opt"
