SUMMARY = "Download, build, install, upgrade, and uninstall Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"
SECTION = "devel/python"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=19;md5=9a33897f1bca1160d7aad3835152e158"

PYPI_PACKAGE_EXT = "zip"

inherit pypi

SRC_URI[md5sum] = "379642a4f17214071fdc1894255d8d11"
SRC_URI[sha256sum] = "94dc566247f35009ed42c0f4422f2b4f0a032fab1372c8308b864c8f26d93388"

DEPENDS += "${PYTHON_PN}"
DEPENDS_class-native += "${PYTHON_PN}-native"
DEPENDS_class-nativesdk += "nativesdk-${PYTHON_PN}"

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${PYTHON_SITEPACKAGES_DIR} \
                           --script-dir=${bindir}"

RDEPENDS_${PN}_class-native = "\
  ${PYTHON_PN}-distutils \
  ${PYTHON_PN}-compression \
"
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
