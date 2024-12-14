#
# Copyright (C) 2012 Wind River Systems, Inc.
#
SUMMARY = "MCE test suite"

DESCRIPTION = "The MCE test suite is a collection of tools and test scripts for \
testing the Linux RAS related features, including CPU/Memory error \
containment and recovery, ACPI/APEI support etc."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://git.kernel.org/pub/scm/utils/cpu/mce/mce-test.git;protocol=git;branch=master \
           file://makefile-remove-ldflags.patch \
           file://0001-gcov_merge.py-scov_merge.py-switch-to-python3.patch \
          "
SRCREV = "9d11fc3e05eae7b454efeb5941beded56f80445b"
PV = "20230601+git"

RDEPENDS:${PN} = "mcelog mce-inject dialog bash"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

S ="${WORKDIR}/git"

EXTRA_OEMAKE += "CFLAGS='${CFLAGS}'"

do_install:append(){
   install -d ${D}/opt/mce-test
   cp -rf ${S}/* ${D}/opt/mce-test/
}

FILES:${PN} += "/opt"
