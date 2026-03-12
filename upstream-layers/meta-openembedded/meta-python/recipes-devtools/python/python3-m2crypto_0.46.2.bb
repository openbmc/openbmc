SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSES/BSD-2-Clause.txt;md5=8099b0e569f862ece05740aef06c82a2"

SRC_URI[sha256sum] = "13c2fa89562f7b8af40cc74b55f490be5e2ab8ccfb739f11c16d3ce6221a61ba"

SRC_URI += " \
          file://0001-setup.py-Make-the-cmd-available.patch \
          file://0001-fix-allow-64-bit-time_t-on-32-bit-systems-in-test_is.patch \
          file://0002-fix-correct-struct-packing-on-32-bit-with-_TIME_BITS.patch \
"

CVE_STATUS[CVE-2009-0127] = "disputed: upstream claims there is no bug"
CVE_STATUS[CVE-2020-25657] = "fixed-version: the used version (0.46.2) contains the fix already"

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

do_configure:prepend:class-target() {
    # workaround for https://github.com/swiftlang/swift/issues/69311
    sed -i "/sys\/types.h/d" ${RECIPE_SYSROOT}${includedir}/openssl/e_os2.h
}

do_install:append() {
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/SSL/__pycache__/*.cpython-*.pyc
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/M2Crypto/__pycache__/*.cpython-*.pyc
}

CVE_PRODUCT = "m2crypto"

BBCLASSEXTEND = "native"
