SUMMARY = "Software Construction tool (make/autotools replacement)"
HOMEPAGE = "https://github.com/SCons/scons"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b94c6e2be9670c62b38f7118c12866d2"

SRC_URI += " file://0001-Fix-man-page-installation.patch"
SRC_URI[sha256sum] = "accb8035be2c9cfbab06471286eaeff86a10037a8064cf4ef4c3df04ea5a7387"

PYPI_PACKAGE = "SCons"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = "\
  python3-core \
  python3-compression \
  python3-fcntl \
  python3-importlib-metadata \
  python3-io \
  python3-json \
  python3-shell \
  python3-pickle \
  python3-pkg-resources \
  python3-pprint \
  "

FILES:${PN}-doc += "${datadir}/scons*.1"
