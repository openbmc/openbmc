SUMMARY = "A Python crypto and SSL toolkit"
HOMEPAGE = "http://chandlerproject.org/bin/view/Projects/MeTooCrypto"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=68f7880de949412b2ea248932d97ed54"

SRCNAME = "M2Crypto"
SRC_URI = "http://pypi.python.org/packages/source/M/M2Crypto/M2Crypto-${PV}.tar.gz"

SRC_URI[md5sum] = "89557730e245294a6cab06de8ad4fb42"
SRC_URI[sha256sum] = "1ac3b6eafa5ff7e2a0796675316d7569b28aada45a7ab74042ad089d15a9567f"

SRC_URI += "file://0001-setup.py-link-in-sysroot-not-in-host-directories.patch "

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

DEPENDS += "openssl swig-native"

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
