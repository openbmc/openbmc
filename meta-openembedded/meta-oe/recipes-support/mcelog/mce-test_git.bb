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
          "
SRCREV = "74bffd8b0aa27051aeaa1983a7b23975ca8d8726"
PV = "20171030+git${SRCPV}"

RDEPENDS_${PN} = "mcelog mce-inject dialog bash"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

S ="${WORKDIR}/git"

do_install_append(){
   install -d ${D}/opt/mce-test
   cp -rf ${S}/* ${D}/opt/mce-test/
}

FILES_${PN} += "/opt"
