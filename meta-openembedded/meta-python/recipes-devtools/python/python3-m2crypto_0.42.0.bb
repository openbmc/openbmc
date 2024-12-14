SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b0e1f0b7d0ce8a62c18b1287b991800e"

SRC_URI[sha256sum] = "42b62df2caf623161b1d643a7235464c2fe2a3105049ebc498a6d47dc08f64b4"

inherit pypi siteinfo python_setuptools_build_meta

DEPENDS += "openssl swig-native"
RDEPENDS:${PN} += "\
  python3-datetime \
  python3-setuptools \
  python3-logging \
  python3-netclient \
  python3-netserver \
  python3-numbers \
  python3-xmlrpc \
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

do_configure:prepend() {
    # workaround for https://github.com/swiftlang/swift/issues/69311
    sed -i "/sys\/types.h/d" ${RECIPE_SYSROOT}${includedir}/openssl/e_os2.h
}

do_install:append() {
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/SSL/__pycache__/*.cpython-*.pyc
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/__pycache__/*.cpython-*.pyc
}

BBCLASSEXTEND = "native"
