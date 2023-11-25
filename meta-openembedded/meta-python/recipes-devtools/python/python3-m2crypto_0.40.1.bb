SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b0e1f0b7d0ce8a62c18b1287b991800e"

FILESEXTRAPATHS:prepend := "${THISDIR}/python-m2crypto:"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch \
            file://cross-compile-platform.patch \
            file://avoid-host-contamination.patch \
            file://0001-setup.py-address-openssl-3.x-build-issue.patch \
            "
SRC_URI[sha256sum] = "bbfd113ec55708c05816252a4f09e4237df4f3bbfc8171cbbc33057d257bbb30"

PYPI_PACKAGE = "M2Crypto"
inherit pypi siteinfo setuptools3

DEPENDS += "openssl swig-native"
RDEPENDS:${PN} += "\
  ${PYTHON_PN}-datetime \
  ${PYTHON_PN}-distutils \
  ${PYTHON_PN}-logging \
  ${PYTHON_PN}-netclient \
  ${PYTHON_PN}-netserver \
  ${PYTHON_PN}-numbers \
  ${PYTHON_PN}-smtpd \
  ${PYTHON_PN}-xmlrpc \
"

DISTUTILS_BUILD_ARGS += "build_ext --openssl=${STAGING_EXECPREFIXDIR} -I${STAGING_INCDIR}"
DISTUTILS_INSTALL_ARGS += "build_ext --openssl=${STAGING_EXECPREFIXDIR}"

SWIG_FEATURES ?= "-D__${HOST_ARCH}__ ${@['-D__ILP32__','-D__LP64__'][d.getVar('SITEINFO_BITS') != '32']} -DOPENSSL_NO_FILENAMES"

SWIG_FEATURES:append:riscv64 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES:append:riscv32 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES:append:mipsarch = " -D_MIPS_SZPTR=${SITEINFO_BITS}"
SWIG_FEATURES:append:powerpc64le = " -D__powerpc64__"
SWIG_FEATURES:append:x86 = " -D__i386__"
SWIG_FEATURES:append:x32 = " -D__ILP32__"

export SWIG_FEATURES

export STAGING_DIR

do_install:append() {
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/SSL/__pycache__/*.cpython-*.pyc
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/__pycache__/*.cpython-*.pyc
}

BBCLASSEXTEND = "native"
