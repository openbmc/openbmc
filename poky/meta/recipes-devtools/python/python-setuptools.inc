SUMMARY = "Download, build, install, upgrade, and uninstall Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=19;md5=9a33897f1bca1160d7aad3835152e158"

PYPI_PACKAGE_EXT = "zip"

inherit pypi

SRC_URI_append_class-native = " file://0001-conditionally-do-not-fetch-code-by-easy_install.patch"

SRC_URI[md5sum] = "712697a5bc975cf479d487cd68206c20"
SRC_URI[sha256sum] = "6e4eec90337e849ade7103723b9a99631c1f0d19990d6e8412dc42f5ae8b304d"

DEPENDS += "${PYTHON_PN}"

RDEPENDS_${PN} = "\
  ${PYTHON_PN}-compile \
  ${PYTHON_PN}-compression \
  ${PYTHON_PN}-ctypes \
  ${PYTHON_PN}-distutils \
  ${PYTHON_PN}-email \
  ${PYTHON_PN}-html \
  ${PYTHON_PN}-netserver \
  ${PYTHON_PN}-numbers \
  ${PYTHON_PN}-pkgutil \
  ${PYTHON_PN}-plistlib \
  ${PYTHON_PN}-shell \
  ${PYTHON_PN}-stringold \
  ${PYTHON_PN}-threading \
  ${PYTHON_PN}-unittest \
  ${PYTHON_PN}-xml \
"
do_install_prepend() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

BBCLASSEXTEND = "native nativesdk"
