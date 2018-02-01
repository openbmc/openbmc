SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "http://chandlerproject.org/bin/view/Projects/MeTooCrypto"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=95295693f047bb8f76928251a6154a60"

SRC_URI[md5sum] = "040234289fbef5bed4029f0f7d1dae35"
SRC_URI[sha256sum] = "ac303a1881307a51c85ee8b1d87844d9866ee823b4fdbc52f7e79187c2d9acef"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch "

PYPI_PACKAGE = "M2Crypto"
inherit pypi setuptools siteinfo

DEPENDS += "openssl swig-native"
RDEPENDS_${PN} += "python-typing"

DISTUTILS_BUILD_ARGS += "build_ext --openssl=${STAGING_DIR_HOST} -I${STAGING_INCDIR}"
DISTUTILS_INSTALL_ARGS += "build_ext --openssl=${STAGING_DIR_HOST}"

inherit setuptools

SWIG_FEATURES_x86-64 = "-D__x86_64__"
SWIG_FEATURES ?= ""
export SWIG_FEATURES

# Get around a problem with swig, but only if the
# multilib header file exists.
#
do_compile_prepend() {
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
