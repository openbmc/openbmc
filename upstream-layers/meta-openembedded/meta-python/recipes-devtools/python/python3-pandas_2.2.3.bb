SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "https://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb819092901ddb13a7d0a4f5e05f098a"

SRC_URI += " \
            file://0001-pyproject.toml-don-t-pin-dependency-versions.patch \
            "

SRC_URI:append:class-target = " file://0001-BLD-add-option-to-specify-numpy-header-location.patch "

SRC_URI[sha256sum] = "4f18ba62b61d7e192368b84517265a99b4d7ee8912f8708660fb4a366cc82667"

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
