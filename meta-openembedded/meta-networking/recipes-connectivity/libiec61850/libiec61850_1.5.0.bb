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
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "swig-native python3"
SRCREV = "fcefc746fea286aeaa40d2f62240216da81c85e5"

SRC_URI = "git://github.com/mz-automation/${BPN}.git;branch=v1.5;protocol=https \
           file://0001-pyiec61850-don-t-break-CMAKE_INSTALL_PATH-by-trying-.patch \
"

S = "${WORKDIR}/git"

inherit cmake pkgconfig python3-dir python3native siteinfo

EXTRA_OECMAKE = " \
    -DBUILD_EXAMPLES=OFF \
    -DBUILD_PYTHON_BINDINGS=ON \
"

RDEPENDS:${PN}-python = " python3-core "
RDEPENDS:${PN} = " python3-core "

FILES:${PN} += " \
    ${libdir}/${PYTHON_DIR}/site-packages/iec61850.py \
    ${libdir}/${PYTHON_DIR}/site-packages/_iec61850.so \
"
