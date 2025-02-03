SUMMARY = "Software Construction tool (make/autotools replacement)"
HOMEPAGE = "https://github.com/SCons/scons"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d903b0b8027f461402bac9b5169b36f7"

SRC_URI[sha256sum] = "5b641357904d2f56f7bfdbb37e165ab996b6143c948b9df0efc7305f54949daa"

inherit pypi python_setuptools_build_meta

S = "${WORKDIR}/SCons-${PV}"

RDEPENDS:${PN}:class-target = "\
  python3-core \
  python3-compression \
  python3-fcntl \
  python3-io \
  python3-json \
  python3-shell \
  python3-pickle \
  python3-pkg-resources \
  python3-pprint \
  "

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='${STAGING_DIR_HOST}/${PYTHON_SITEPACKAGES_DIR}' PYTHONNOUSERSITE='1'
}

BBCLASSEXTEND = "native"
