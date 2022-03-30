SUMMARY = "Hardware performance monitoring counters"
HOMEPAGE = "https://team.inria.fr/pacap/software/tiptop/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "ncurses libxml2 bison-native flex-native"

SRC_URI = "http://files.inria.fr/pacap/${BPN}/${BP}.tar.gz \
           file://0001-Fix-parallel-build-problems-by-Adrian-Bunk.patch \
           file://0002-fix-reproducibility-of-build-process.patch \
           file://0001-Fix-build-when-S-B.patch \
           file://0001-Fix-a-lot-of-Werror-format-security-errors-with-mvwp.patch \
           "
SRC_URI[sha256sum] = "51c4449c95bba34f16b429729c2f58431490665d8093efaa8643b2e1d1084182"

UPSTREAM_CHECK_URI = "https://team.inria.fr/pacap/software/tiptop/"

inherit autotools

EXTRA_OECONF = "CFLAGS="$CFLAGS -I${STAGING_INCDIR}/libxml2""
COMPATIBLE_HOST = "(i.86|x86_64|arm|powerpc|aarch64).*-linux"

do_configure:prepend () {
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
