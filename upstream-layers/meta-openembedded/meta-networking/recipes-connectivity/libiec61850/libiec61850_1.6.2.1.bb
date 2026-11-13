SUMMARY = "Open source iec61850 implementation"
DESCRIPTION = "IEC 61850 is an international standard for \
communication systems in Substation Automation Systems \
(SAS) and management of Decentralized Energy Resources \
(DER). It is seen as one of the communication standards \
of the emerging Smart Grid. \
The project libIEC61850 provides a server and client \
library for the IEC 61850/MMS, IEC 61850/GOOSE and IEC \
61850-9-2/Sampled Values communication protocols \
written in C. It is available under the GPLv3 license."
HOMEPAGE = "http://libiec61850.com"
SECTION = "console/network"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "swig-native python3"
SRCREV = "6e7a603c771d6124cd3cafea7ec38b7e0f2e7aaf"

SRC_URI = "git://github.com/mz-automation/${BPN}.git;branch=v1.6_develop;protocol=https;tag=v${PV} \
           file://0001-pyiec61850-Use-CMAKE_INSTALL_LIBDIR-from-GNUInstallD.patch \
"


inherit cmake pkgconfig python3-dir python3native siteinfo python3targetconfig

EXTRA_OECMAKE = " \
    -DBUILD_EXAMPLES=OFF \
    -DBUILD_PYTHON_BINDINGS=ON \
    -DPYTHON_BASEVERSION=${PYTHON_BASEVERSION} \
"

RDEPENDS:${PN}-python = " python3-core "
RDEPENDS:${PN} = " python3-core "

FILES:${PN} += " \
    ${PYTHON_SITEPACKAGES_DIR}/pyiec61850.py \
    ${PYTHON_SITEPACKAGES_DIR}/_pyiec61850.so \
"

CVE_STATUS[CVE-2024-45969] = "fixed-version: fixed since v1.6.0"
