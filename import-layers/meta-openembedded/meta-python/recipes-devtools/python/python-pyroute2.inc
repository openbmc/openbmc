SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "733adb362b6603c7269c0b3df3045a55"
SRC_URI[sha256sum] = "5cdf44656cf623369f0cbf183d9d14a1a50ebdffbd50d4e30ffda62c0a05d7b3"

SRC_URI += "file://import-simplejson-as-json.patch"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-pyroute2:"

inherit pypi

RDEPENDS_${PN} += "\
  ${PYTHON_PN}-distutils \
  ${PYTHON_PN}-simplejson \
  ${PYTHON_PN}-multiprocessing \
  ${PYTHON_PN}-io ${PYTHON_PN}-pprint \
  ${PYTHON_PN}-pickle \
  ${PYTHON_PN}-logging \
  ${PYTHON_PN}-threading \
  ${PYTHON_PN}-textutils \
  ${PYTHON_PN}-subprocess \
  ${PYTHON_PN}-netclient \
"
