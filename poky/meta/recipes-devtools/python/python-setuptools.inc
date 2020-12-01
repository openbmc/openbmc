SUMMARY = "Download, build, install, upgrade, and uninstall Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=19;md5=9a33897f1bca1160d7aad3835152e158"

PYPI_PACKAGE_EXT = "zip"

inherit pypi

SRC_URI_append_class-native = " file://0001-conditionally-do-not-fetch-code-by-easy_install.patch"

SRC_URI[md5sum] = "0c956eea142af9c2b02d72e3c042af30"
SRC_URI[sha256sum] = "89c6e6011ec2f6d57d43a3f9296c4ef022c2cbf49bab26b407fe67992ae3397f"

DEPENDS += "${PYTHON_PN}"

RDEPENDS_${PN} = "\
  ${PYTHON_PN}-2to3 \
  ${PYTHON_PN}-compile \
  ${PYTHON_PN}-compression \
  ${PYTHON_PN}-ctypes \
  ${PYTHON_PN}-distutils \
  ${PYTHON_PN}-email \
  ${PYTHON_PN}-html \
  ${PYTHON_PN}-json \
  ${PYTHON_PN}-netserver \
  ${PYTHON_PN}-numbers \
  ${PYTHON_PN}-pickle \
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

# The pkg-resources module can be used by itself, without the package downloader
# and easy_install. Ship it in a separate package so that it can be used by
# minimal distributions.
PACKAGES =+ "${PYTHON_PN}-pkg-resources "
FILES_${PYTHON_PN}-pkg-resources = "${PYTHON_SITEPACKAGES_DIR}/pkg_resources/*"
# Due to the way OE-Core implemented native recipes, the native class cannot
# have a dependency on something that is not a recipe name. Work around that by
# manually setting RPROVIDES.
RDEPENDS_${PN}_append = " ${PYTHON_PN}-pkg-resources"
RPROVIDES_append_class-native = " ${PYTHON_PN}-pkg-resources-native"
