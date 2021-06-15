SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b0e1f0b7d0ce8a62c18b1287b991800e"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-m2crypto:"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch \
            file://cross-compile-platform.patch \
            file://0001-Allow-verify_cb_-to-be-called-with-ok-True.patch \
            file://0001-Use-of-RSA_SSLV23_PADDING-has-been-deprecated.patch \
           "
SRC_URI[sha256sum] = "e4e42f068b78ccbf113e5d0a72ae5f480f6c3ace4940b91e4fff5598cfff6fb3"

PYPI_PACKAGE = "M2Crypto"
inherit pypi siteinfo setuptools3

DEPENDS += "openssl swig-native"
RDEPENDS_${PN} += "\
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

SWIG_FEATURES_x86 = "-D__i386__"
SWIG_FEATURES_x32 = "-D__ILP32__"

SWIG_FEATURES ?= "-D__${HOST_ARCH}__ ${@['-D__ILP32__','-D__LP64__'][d.getVar('SITEINFO_BITS') != '32']}"

SWIG_FEATURES_append_riscv64 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES_append_riscv32 = " -D__SIZEOF_POINTER__=${SITEINFO_BITS}/8 -D__riscv_xlen=${SITEINFO_BITS}"
SWIG_FEATURES_append_mipsarch = " -D_MIPS_SZPTR=${SITEINFO_BITS}"
export SWIG_FEATURES

BBCLASSEXTEND = "native"
