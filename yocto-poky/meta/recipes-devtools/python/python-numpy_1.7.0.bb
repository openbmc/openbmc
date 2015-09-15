SUMMARY = "A sophisticated Numeric Processing Package for Python"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f87832d854acbade6e9f5c601c8b30b1"
PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/numpy/numpy-${PV}.tar.gz \
           file://no-host-paths.patch \
           ${CONFIGFILESURI} "

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

inherit distutils

# Make the build fail and replace *config.h with proper one
# This is a ugly, ugly hack - Koen
do_compile_prepend_class-target() {
    BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
    ${STAGING_BINDIR_NATIVE}/python-native/python setup.py build ${DISTUTILS_BUILD_ARGS} || \
    true
    cp ${WORKDIR}/*config.h ${S}/build/$(ls ${S}/build | grep src)/numpy/core/include/numpy/
}

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/numpy/core/lib/*.a"

SRC_URI[md5sum] = "4fa54e40b6a243416f0248123b6ec332"
SRC_URI[sha256sum] = "f4fa70b7edbab65ee6432eb63743f5489f1919c614632b20b2fb45aa7e682ac6"

# install what is needed for numpy.test()
RDEPENDS_${PN} = "python-unittest \
                  python-difflib \
                  python-pprint \
                  python-pickle \
                  python-shell \
                  python-nose \
                  python-doctest \
                  python-datetime \
                  python-distutils \
                  python-misc \
                  python-mmap \
                  python-netclient \
                  python-numbers \
                  python-pydoc \
                  python-pkgutil \
                  python-email \
                  python-subprocess \
                  python-compression \
"

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
