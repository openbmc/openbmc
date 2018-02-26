SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "https://gitlab.com/m2crypto/m2crypto"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b0e1f0b7d0ce8a62c18b1287b991800e"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch \
            file://cross-compile-platform.patch \
            file://m2crypto-0.26.4-gcc_macros.patch \
           "
SRC_URI[md5sum] = "5c74c25ba8b45122318a165a3a2059ad"
SRC_URI[sha256sum] = "5cae7acc0b34821f8c0ddf6665e482893fe1f198ad6379e61ffa9d8e65f5c199"

PYPI_PACKAGE = "M2Crypto"
inherit pypi setuptools siteinfo

DEPENDS += "openssl swig-native"
RDEPENDS_${PN} += "python-typing"

DISTUTILS_BUILD_ARGS += "build_ext --openssl=${STAGING_DIR_HOST} -I${STAGING_INCDIR}"
DISTUTILS_INSTALL_ARGS += "build_ext --openssl=${STAGING_DIR_HOST}"

SWIG_FEATURES_x86 = "-D__i386__"
SWIG_FEATURES ?= "-D__${HOST_ARCH}__"
export SWIG_FEATURES

# Get around a problem with swig, but only if the
# multilib header file exists.
#
do_compile_prepend() {
    ${CPP} -dM - < /dev/null | grep -v __STDC__ | grep -v __REGISTER_PREFIX__ | grep -v __GNUC__ \
	| sed 's/^\(#define \([^ ]*\) .*\)$/#undef \2\n\1/' > SWIG/gcc_macros.h
    if [ "${SITEINFO_BITS}" = "64" ];then
        bit="64"
    else
        bit="32"
    fi

    if [ -e ${STAGING_INCDIR}/openssl/opensslconf-${bit}.h ] ;then
        for i in SWIG/_ec.i SWIG/_evp.i; do
            sed -i -e "s/opensslconf.*\./opensslconf-${bit}\./" "$i"
        done
    elif [ -e ${STAGING_INCDIR}/openssl/opensslconf-n${bit}.h ] ;then
        for i in SWIG/_ec.i SWIG/_evp.i; do
            sed -i -e "s/opensslconf.*\./opensslconf-n${bit}\./" "$i"
        done
    fi
}

BBCLASSEXTEND = "native"
