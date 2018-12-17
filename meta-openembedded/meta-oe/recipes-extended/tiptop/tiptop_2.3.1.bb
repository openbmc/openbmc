SUMMARY = "Hardware performance monitoring counters"
HOMEPAGE = "http://tiptop.gforge.inria.fr/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "ncurses libxml2 bison-native flex-native"

SRC_URI = "http://tiptop.gforge.inria.fr/releases/${BP}.tar.gz \
           file://0001-Fix-parallel-build-problems-by-Adrian-Bunk.patch \
           file://0002-fix-reproducibility-of-build-process.patch \
           file://0001-Fix-build-when-S-B.patch \
           "
SRC_URI[md5sum] = "46ca0fdf0236f02dd2b96d347626d2a2"
SRC_URI[sha256sum] = "51c4449c95bba34f16b429729c2f58431490665d8093efaa8643b2e1d1084182"

inherit autotools

EXTRA_OECONF = "CFLAGS="$CFLAGS -I${STAGING_INCDIR}/libxml2""
COMPATIBLE_HOST = "(i.86|x86_64|arm|powerpc|aarch64).*-linux"

do_configure_prepend () {
    # Two bugs in configure.ac when cross-compiling.
    # 1. The path of libxml2. Specify it in EXTRA_OECONF.
    # 2. hw's value on other platforms. Replace it if the target is
    #    not i*86/x86_64.
    if ( echo "${TARGET_ARCH}" | grep -q -e 'i.86' -e 'x86_64' ); then
        sed -i 's= -I/usr/include/libxml2=='    ${S}/configure.ac
    else
        sed -i 's/hw=`uname -m`/hw="unknown"/'  ${S}/configure.ac
        sed -i 's= -I/usr/include/libxml2=='    ${S}/configure.ac
    fi
}
