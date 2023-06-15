SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b0e1f0b7d0ce8a62c18b1287b991800e"

FILESEXTRAPATHS:prepend := "${THISDIR}/python-m2crypto:"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch \
           file://cross-compile-platform.patch \
           file://avoid-host-contamination.patch \
           file://0001-setup.py-address-openssl-3.x-build-issue.patch \
           file://CVE-2020-25657.patch \
           "
SRC_URI[sha256sum] = "99f2260a30901c949a8dc6d5f82cd5312ffb8abc92e76633baf231bbbcb2decb"

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

SWIG_FEATURES:x86 = "-D__i386__"
SWIG_FEATURES:x32 = "-D__ILP32__"

SWIG_FEATURES ?= "-D__${HOST_ARCH}__ ${@['-D__ILP32__','-D__LP64__'][d.getVar('SITEINFO_BITS') != '32']}"

SWIG_FEATURES:append:riscv64 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES:append:riscv32 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES:append:mipsarch = " -D_MIPS_SZPTR=${SITEINFO_BITS}"
SWIG_FEATURES:append:powerpc64le = " -D__powerpc64__"
export SWIG_FEATURES

export STAGING_DIR

BBCLASSEXTEND = "native"
