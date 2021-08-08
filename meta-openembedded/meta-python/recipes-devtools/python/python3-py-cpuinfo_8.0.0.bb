# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Get CPU info with pure Python 2 & 3"
HOMEPAGE = "https://github.com/workhorsy/py-cpuinfo"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b0b97c022f12b14d9e02de0b283ee9e9"

SRC_URI[sha256sum] = "5f269be0e08e33fd959de96b34cd4aeeeacac014dd8305f70eb28d06de2345c5"

inherit ptest pypi setuptools3

SRC_URI += "file://run-ptest \
           "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-pytest \
"

RDEPENDS:${PN} += "python3-core python3-ctypes python3-datetime python3-json python3-io python3-mmap python3-multiprocessing python3-netclient python3-pickle python3-pprint python3-shell"

BBCLASSEXTEND = "native nativesdk"
