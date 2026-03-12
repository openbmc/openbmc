SUMMARY = "Software Construction tool (make/autotools replacement)"
HOMEPAGE = "https://github.com/SCons/scons"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d903b0b8027f461402bac9b5169b36f7"

SRC_URI[sha256sum] = "99c0e94a42a2c1182fa6859b0be697953db07ba936ecc9817ae0d218ced20b15"

inherit pypi python_setuptools_build_meta

S = "${UNPACKDIR}/scons-${PV}"

RDEPENDS:${PN}:class-target = "\
  python3-core \
  python3-compression \
  python3-fcntl \
  python3-io \
  python3-json \
  python3-shell \
  python3-pickle \
  python3-pprint \
  "

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='${STAGING_DIR_HOST}/${PYTHON_SITEPACKAGES_DIR}' PYTHONNOUSERSITE='1'
}

BBCLASSEXTEND = "native"
