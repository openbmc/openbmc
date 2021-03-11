SUMMARY = "A sophisticated Numeric Processing Package for Python"
HOMEPAGE = "https://numpy.org/"
DESCRIPTION = "NumPy is the fundamental package needed for scientific computing with Python."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & BSD-2-Clause & PSF & Apache-2.0 & BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1a32aba007a415aa8a1c708a0e2b86a1"

SRCNAME = "numpy"

SRC_URI = "https://github.com/${SRCNAME}/${SRCNAME}/releases/download/v${PV}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://0001-numpy-random-setup.py-remove-the-detection-of-x86-ta.patch \
           file://0001-convert-shebang-from-python-to-python3.patch \
           "
SRC_URI[md5sum] = "9147c3ee75e58d657b5b8b5a4f3564e0"
SRC_URI[sha256sum] = "fb0415475e673cb9a6dd816df999e0ab9f86fa3af2b1770944e7288d2bea4ac9"

UPSTREAM_CHECK_URI = "https://github.com/numpy/numpy/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

# Needed for building with gcc 4.x from centos 7
CFLAGS_append_class-native = " -std=c99"

S = "${WORKDIR}/numpy-${PV}"

CLEANBROKEN = "1"

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/core/lib/*.a"

# install what is needed for numpy.test()
RDEPENDS_${PN} = "${PYTHON_PN}-unittest \
                  ${PYTHON_PN}-difflib \
                  ${PYTHON_PN}-pprint \
                  ${PYTHON_PN}-pickle \
                  ${PYTHON_PN}-shell \
                  ${PYTHON_PN}-nose \
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
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
