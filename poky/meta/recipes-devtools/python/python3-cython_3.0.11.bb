SUMMARY = "The Cython language"
HOMEPAGE = "https://pypi.org/project/Cython/"
DESCRIPTION = "Cython is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=61c3ee8961575861fa86c7e62bc9f69c"

SRC_URI += "file://0001-Output-import-relative-paths-in-generated-C-code.-GH.patch"
SRC_URI[sha256sum] = "7146dd2af8682b4ca61331851e6aebce9fe5158e75300343f80c07ca80b1faff"

inherit pypi setuptools3 cython

# No need to depend on self
DEPENDS:remove = "python3-cython-native"

UPSTREAM_CHECK_PYPI_PACKAGE = "Cython"

do_install:append() {
    # rename scripts that would conflict with the Python 2 build of Cython
    mv ${D}${bindir}/cython ${D}${bindir}/cython3
    mv ${D}${bindir}/cythonize ${D}${bindir}/cythonize3
    mv ${D}${bindir}/cygdb ${D}${bindir}/cygdb3
}

RDEPENDS:${PN}:class-target += "\
    python3-misc \
    python3-netserver \
    python3-pkgutil \
    python3-pyparsing \
    python3-setuptools \
    python3-shell \
    python3-xml \
"

RDEPENDS:${PN}:class-nativesdk += "\
    nativesdk-python3-misc \
    nativesdk-python3-netserver \
    nativesdk-python3-pkgutil \
    nativesdk-python3-pyparsing \
    nativesdk-python3-setuptools \
    nativesdk-python3-shell \
    nativesdk-python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
