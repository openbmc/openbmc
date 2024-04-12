SUMMARY = "Software Construction tool (make/autotools replacement)"
HOMEPAGE = "https://github.com/SCons/scons"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d903b0b8027f461402bac9b5169b36f7"

SRC_URI += " file://0001-Fix-man-page-installation.patch"
SRC_URI[sha256sum] = "d8b617f6610a73e46509de70dcf82f76861b79762ff602d546f4e80918ec81f3"

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

do_install:append() {
    install -d ${D}${mandir}/man1
    mv ${D}${prefix}/scons*.1 ${D}${mandir}/man1/
}

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='${STAGING_DIR_HOST}/${PYTHON_SITEPACKAGES_DIR}' PYTHONNOUSERSITE='1'
}

BBCLASSEXTEND = "native"
