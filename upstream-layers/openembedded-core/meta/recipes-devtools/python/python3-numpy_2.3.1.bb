SUMMARY = "A sophisticated Numeric Processing Package for Python"
HOMEPAGE = "https://numpy.org/"
DESCRIPTION = "NumPy is the fundamental package needed for scientific computing with Python."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & BSD-2-Clause & PSF-2.0 & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26080bf81b2662c7119d3ef28ae197fd"

SRCNAME = "numpy"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://fix_reproducibility.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "1ec9ae20a4226da374362cca3c62cd753faf2f951440b0e3b98e93c235441d2b"

GITHUB_BASE_URI = "https://github.com/numpy/numpy/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)$"

inherit pkgconfig ptest python_mesonpy github-releases cython

S = "${UNPACKDIR}/numpy-${PV}"

PACKAGECONFIG[svml] = "-Ddisable-svml=false,-Ddisable-svml=true"

# Remove references to buildpaths from numpy's __config__.py
do_install:append() {
    sed -i \
        -e 's|${S}=||g' \
        -e 's|${B}=||g' \
        -e 's|${RECIPE_SYSROOT_NATIVE}=||g' \
        -e 's|${RECIPE_SYSROOT_NATIVE}||g' \
        -e 's|${RECIPE_SYSROOT}=||g' \
        -e 's|${RECIPE_SYSROOT}||g' ${D}${PYTHON_SITEPACKAGES_DIR}/numpy/__config__.py

    nativepython3 -mcompileall -s ${D} ${D}${PYTHON_SITEPACKAGES_DIR}/numpy/__config__.py
}

FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/_core/lib/*.a \
                          ${PYTHON_SITEPACKAGES_DIR}/numpy/random/lib/*.a \
"

# install what is needed for numpy.test()
RDEPENDS:${PN} = "\
                  python3-compression \
                  python3-ctypes \
                  python3-datetime \
                  python3-difflib \
                  python3-doctest \
                  python3-email \
                  python3-json \
                  python3-misc \
                  python3-mmap \
                  python3-multiprocessing \
                  python3-netclient \
                  python3-numbers \
                  python3-pickle \
                  python3-pkgutil \
                  python3-pprint \
                  python3-pydoc \
                  python3-shell \
                  python3-threading \
                  python3-unittest \
"
RDEPENDS:${PN}-ptest += "\
                         ldd \
                         meson \
                         python3-hypothesis \
                         python3-pytest \
                         python3-resource \
                         python3-sortedcontainers \
                         python3-typing-extensions \
                         python3-unittest-automake-output \
"

BBCLASSEXTEND = "native nativesdk"
