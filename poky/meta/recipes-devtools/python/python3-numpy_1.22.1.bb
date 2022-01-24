SUMMARY = "A sophisticated Numeric Processing Package for Python"
HOMEPAGE = "https://numpy.org/"
DESCRIPTION = "NumPy is the fundamental package needed for scientific computing with Python."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & BSD-2-Clause & PSF & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8026691468924fb6ec155dadfe2a1a7f"

SRCNAME = "numpy"

SRC_URI = "https://github.com/${SRCNAME}/${SRCNAME}/releases/download/v${PV}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://0001-numpy-core-Define-RISCV-32-support.patch \
           file://run-ptest \
           file://0001-numpy-core-setup.py-disable-svml-for-now.patch \
           "
SRC_URI[sha256sum] = "dd1968402ae20dfd59b34acd799b494be340c774f6295e9bf1c2b9842a5e416d"

UPSTREAM_CHECK_URI = "https://github.com/numpy/numpy/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

DEPENDS += "python3-cython-native"

inherit ptest setuptools3

S = "${WORKDIR}/numpy-${PV}"

CLEANBROKEN = "1"

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
                         ldd \
"

BBCLASSEXTEND = "native nativesdk"
