SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "https://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e98642e1210ade884e5254ab18d55b7d"

SRC_URI:append:class-target = " file://0001-BLD-add-option-to-specify-numpy-header-location.patch "

SRC_URI[sha256sum] = "dca3734d6ab7c906e6730f0788b0a1dbb9f2467731f9711f77995c8e9d62d712"

CVE_PRODUCT = "pandas"

inherit pkgconfig pypi python_mesonpy cython

DEPENDS += " \
    python3-numpy \
    python3-versioneer-native \
    python3-wheel-native \
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
    # Cython embeds the path of the .pyx it was generated from into the
    # generated C sources, which are shipped in the -dbg package
    find ${B} \( -name "*.c" -o -name "*.cpp" \) -print0 | xargs -0 -r \
        sed -i 's|${S}/|${TARGET_DBGSRC_DIR}/|g'
}

EXTRA_OEMESON:append:class-target = " -Dnumpy_inc_dir=${RECIPE_SYSROOT}${PYTHON_SITEPACKAGES_DIR}/numpy/_core/include "

BBCLASSEXTEND = "native"
