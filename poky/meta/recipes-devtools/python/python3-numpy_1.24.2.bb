SUMMARY = "A sophisticated Numeric Processing Package for Python"
HOMEPAGE = "https://numpy.org/"
DESCRIPTION = "NumPy is the fundamental package needed for scientific computing with Python."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & BSD-2-Clause & PSF-2.0 & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8026691468924fb6ec155dadfe2a1a7f"

SRCNAME = "numpy"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://0001-numpy-core-Define-RISCV-32-support.patch \
           file://0001-simd.inc.src-Change-NPY_INLINE-to-inline.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "003a9f530e880cb2cd177cba1af7220b9aa42def9c4afc2a2fc3ee6be7eb2b22"

GITHUB_BASE_URI = "https://github.com/numpy/numpy/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)$"

DEPENDS += "python3-cython-native"

inherit ptest setuptools3 github-releases

S = "${WORKDIR}/numpy-${PV}"

CLEANBROKEN = "1"

do_compile:prepend() {
    export NPY_DISABLE_SVML=1
}

# Unfortunately the following pyc files are non-deterministc due to 'frozenset'
# being written without strict ordering, even with PYTHONHASHSEED = 0
# Upstream is discussing ways to solve the issue properly, until then let's
# just not install the problematic files.
# More info: http://benno.id.au/blog/2013/01/15/python-determinism
do_install:append() {
	rm ${D}${PYTHON_SITEPACKAGES_DIR}/numpy/typing/tests/data/pass/__pycache__/literal.cpython*
}

FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/core/lib/*.a ${PYTHON_SITEPACKAGES_DIR}/numpy/random/lib/*.a"

# install what is needed for numpy.test()
RDEPENDS:${PN} = "${PYTHON_PN}-unittest \
                  ${PYTHON_PN}-difflib \
                  ${PYTHON_PN}-pprint \
                  ${PYTHON_PN}-pickle \
                  ${PYTHON_PN}-shell \
                  ${PYTHON_PN}-doctest \
                  ${PYTHON_PN}-datetime \
                  ${PYTHON_PN}-distutils \
                  ${PYTHON_PN}-misc \
                  ${PYTHON_PN}-mmap \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-numbers \
                  ${PYTHON_PN}-pydoc \
                  ${PYTHON_PN}-pkgutil \
                  ${PYTHON_PN}-email \
                  ${PYTHON_PN}-compression \
                  ${PYTHON_PN}-ctypes \
                  ${PYTHON_PN}-threading \
                  ${PYTHON_PN}-multiprocessing \
                  ${PYTHON_PN}-json \
"
RDEPENDS:${PN}-ptest += "${PYTHON_PN}-pytest \
                         ${PYTHON_PN}-hypothesis \
                         ${PYTHON_PN}-sortedcontainers \
                         ${PYTHON_PN}-resource \
                         ${PYTHON_PN}-typing-extensions \
                         ldd \
"

BBCLASSEXTEND = "native nativesdk"
