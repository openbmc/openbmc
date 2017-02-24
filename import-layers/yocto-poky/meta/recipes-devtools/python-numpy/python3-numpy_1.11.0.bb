SUMMARY = "A sophisticated Numeric Processing Package for Python"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7e51a5677b22b865abbfb3dff6ffb2d0"

SRCNAME = "numpy"

SRC_URI = "https://files.pythonhosted.org/packages/source/n/${SRCNAME}/${SRCNAME}-${PV}.tar.gz \
           file://0001-Don-t-search-usr-and-so-on-for-libraries-by-default-.patch \
           file://remove-build-path-in-comments.patch \
           file://fix_shebang_f2py.patch \
           ${CONFIGFILESURI} "
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/numpy/files/"

CONFIGFILESURI ?= ""

CONFIGFILESURI_aarch64 = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_arm = " \
    file://config.h \
    file://numpyconfig.h \
"
CONFIGFILESURI_armeb = " \
    file://config.h \
    file://numpyconfig.h \
"
CONFIGFILESURI_mipsel = " \
    file://config.h \
    file://numpyconfig.h \
"
CONFIGFILESURI_x86 = " \
    file://config.h \
    file://numpyconfig.h \
"
CONFIGFILESURI_x86-64 = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_mips = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_powerpc = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_powerpc64 = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_mips64 = " \
    file://config.h \
    file://_numpyconfig.h \
"
CONFIGFILESURI_mips64n32 = " \
    file://config.h \
    file://_numpyconfig.h \
"

S = "${WORKDIR}/numpy-${PV}"

inherit setuptools3

# Make the build fail and replace *config.h with proper one
# This is a ugly, ugly hack - Koen
do_compile_prepend_class-target() {
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build ${DISTUTILS_BUILD_ARGS} || \
    true
    cp ${WORKDIR}/*config.h ${S}/build/$(ls ${S}/build | grep src)/numpy/core/include/numpy/
}

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/core/lib/*.a"

SRC_URI[md5sum] = "bc56fb9fc2895aa4961802ffbdb31d0b"
SRC_URI[sha256sum] = "a1d1268d200816bfb9727a7a27b78d8e37ecec2e4d5ebd33eb64e2789e0db43e"

# install what is needed for numpy.test()
RDEPENDS_${PN} = "python3-unittest \
                  python3-difflib \
                  python3-pprint \
                  python3-pickle \
                  python3-shell \
                  python3-nose \
                  python3-doctest \
                  python3-datetime \
                  python3-distutils \
                  python3-misc \
                  python3-mmap \
                  python3-netclient \
                  python3-numbers \
                  python3-pydoc \
                  python3-pkgutil \
                  python3-email \
                  python3-subprocess \
                  python3-compression \
                  python3-ctypes \
                  python3-threading \
                  python3-textutils \
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
