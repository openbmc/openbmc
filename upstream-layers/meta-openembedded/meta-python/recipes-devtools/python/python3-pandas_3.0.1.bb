SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "https://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e98642e1210ade884e5254ab18d55b7d"

SRC_URI:append:class-target = " file://0001-BLD-add-option-to-specify-numpy-header-location.patch "

SRC_URI[sha256sum] = "4186a699674af418f655dbd420ed87f50d56b4cd6603784279d9eef6627823c8"

CVE_PRODUCT = "pandas"

inherit pkgconfig pypi python_mesonpy cython

DEPENDS += " \
    python3-numpy \
    python3-versioneer-native \
"

CFLAGS:append:toolchain-clang = " -Wno-error=deprecated-declarations"

RDEPENDS:${PN} += " \
    python3-json \
    python3-numpy \
    python3-dateutil \
    python3-dateutil-zoneinfo \
    python3-pytz \
    python3-profile \
"

PYTHONPATH:prepend:class-target = "${RECIPE_SYSROOT}${PYTHON_SITEPACKAGES_DIR}:"
export PYTHONPATH

do_compile:append() {
    # Fix absolute paths in generated files
    find ${B} -name "*.c" -o -name "*.cpp" | xargs -r \
        sed -i 's|${WORKDIR}/pandas-${PV}/|${TARGET_DBGSRC_DIR}/|g'
}

do_install:prepend() {
	sed -i -e 's;${S};;g' ${B}/pandas/_libs/sparse.cpython-*/pandas/_libs/sparse.pyx.c
}

EXTRA_OEMESON:append:class-target = " -Dnumpy_inc_dir=${RECIPE_SYSROOT}${PYTHON_SITEPACKAGES_DIR}/numpy/_core/include "

BBCLASSEXTEND = "native"
