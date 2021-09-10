SUMMARY = "Software Construction tool (make/autotools replacement)"
HOMEPAGE = "https://github.com/SCons/scons"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d903b0b8027f461402bac9b5169b36f7"

SRC_URI += " file://0001-Fix-man-page-installation.patch"
SRC_URI[sha256sum] = "691893b63f38ad14295f5104661d55cb738ec6514421c6261323351c25432b0a"

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
