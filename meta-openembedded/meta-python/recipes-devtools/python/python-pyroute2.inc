SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[md5sum] = "9c251543c4282a2f8b1a8d6aa8afc688"
SRC_URI[sha256sum] = "7afad28ee0a0f3e7c34adaa9f953d00560ed9910203e93f107833b6e8d151171"

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
