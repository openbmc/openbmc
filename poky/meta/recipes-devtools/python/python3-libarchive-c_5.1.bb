SUMMARY = "Python interface to libarchive"
DESCRIPTION = "A Python interface to libarchive. It uses the standard ctypes module to \
    dynamically load and access the C library."
HOMEPAGE = "https://github.com/Changaco/python-libarchive-c"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bcab380227a83bc147350b40a81e6ffc"

SRC_URI += " \
        file://new-libarchive.patch \
        file://0001-ffi-Insert-a-replacable-anchor-for-find_library.patch \
        file://run-ptest \
"

PYPI_PACKAGE = "libarchive-c"

inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "7bcce24ea6c0fa3bc62468476c6d2f6264156db2f04878a372027c10615a2721"

DEPENDS += "patchelf-native libarchive"
# Avoid using find_library API which needs ldconfig and ld/objdump
# https://docs.python.org/3/library/ctypes.html#ctypes-reference
#
do_configure:append() {
    sed -i -e "s|@@REPLACE_FIND_LIBRARY_API@@|'${libdir}/$(patchelf --print-soname ${STAGING_LIBDIR}/libarchive.so)'|" ${S}/libarchive/ffi.py
}

RDEPENDS:${PN} += "\
  libarchive \
  python3-ctypes \
  python3-mmap \
  python3-logging \
"

RDEPENDS:${PN}-ptest += " \
        locale-base-en-us \
        python3-pytest \
        python3-unittest-automake-output \
"

BBCLASSEXTEND = "native"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -d ${D}${PTEST_PATH}/libarchive
    cp -r ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp ${S}/libarchive/* ${D}${PTEST_PATH}/libarchive/
    cp ${S}/README.rst ${D}${PTEST_PATH}/README.rst
}
