SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[md5sum] = "2f6f951e94356ae9d224f417dc6ebed2"
SRC_URI[sha256sum] = "42bf74495d95a0196a74dd171357f660175aba2bfc23f9b5f63e3830ccbef9ac"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-pyroute2:"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-unixadmin \
"
