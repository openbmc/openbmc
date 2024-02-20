SUMMARY = "A sophisticated Numeric Processing Package for Python"
HOMEPAGE = "https://numpy.org/"
DESCRIPTION = "NumPy is the fundamental package needed for scientific computing with Python."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & BSD-2-Clause & PSF-2.0 & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a752eb20459cf74a9d84ee4825e8317c"

SRCNAME = "numpy"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://0001-numpy-core-Define-RISCV-32-support.patch \
           file://fix_reproducibility.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "2a02aba9ed12e4ac4eb3ea9421c420301a0c6460d9830d74a9df87efa4912010"

GITHUB_BASE_URI = "https://github.com/numpy/numpy/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)$"

DEPENDS += "python3-cython-native"

inherit ptest setuptools3 github-releases

S = "${WORKDIR}/numpy-${PV}"

CLEANBROKEN = "1"

do_compile:prepend() {
    export NPY_DISABLE_SVML=1
}

FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/core/lib/*.a ${PYTHON_SITEPACKAGES_DIR}/numpy/random/lib/*.a"

# install what is needed for numpy.test()
RDEPENDS:${PN} = "python3-unittest \
                  python3-difflib \
                  python3-pprint \
                  python3-pickle \
                  python3-shell \
                  python3-doctest \
                  python3-datetime \
                  python3-misc \
                  python3-mmap \
                  python3-netclient \
                  python3-numbers \
                  python3-pydoc \
                  python3-pkgutil \
                  python3-email \
                  python3-compression \
                  python3-ctypes \
                  python3-threading \
                  python3-multiprocessing \
                  python3-json \
"
RDEPENDS:${PN}-ptest += "python3-pytest \
                         python3-hypothesis \
                         python3-sortedcontainers \
                         python3-resource \
                         python3-typing-extensions \
                         ldd \
"

BBCLASSEXTEND = "native nativesdk"
