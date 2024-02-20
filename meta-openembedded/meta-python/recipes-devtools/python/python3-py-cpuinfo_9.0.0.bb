# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Get CPU info with pure Python 2 & 3"
HOMEPAGE = "https://github.com/workhorsy/py-cpuinfo"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2379ecb0d7a8299101b1e4c160cd1f7"

SRC_URI[sha256sum] = "3cdbbf3fac90dc6f118bfd64384f309edeadd902d7c8fb17f02ffa1fc3f49690"

inherit ptest pypi setuptools3

SRC_URI += "file://run-ptest \
            file://0001-test_cli.py-disable.patch \
           "

do_install:append() {
    # Make sure we use /usr/bin/env python3
    for PYTHSCRIPT in `grep -rIl '^#!.*python' ${D}`; do
        sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
    done
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-pytest \
    python3-unittest-automake-output \
"

RDEPENDS:${PN} += "python3-core python3-ctypes python3-datetime python3-json python3-io python3-mmap python3-multiprocessing python3-netclient python3-pickle python3-pprint python3-shell"

BBCLASSEXTEND = "native nativesdk"
