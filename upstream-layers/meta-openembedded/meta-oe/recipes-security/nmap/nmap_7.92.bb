SUMMARY = "network auditing tool"
DESCRIPTION = "Nmap (Network Mapper) is a free and open source (license) utility for network discovery and security auditing.\nGui support via appending to IMAGE_FEATURES x11-base in local.conf"
SECTION = "security"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b8823a06822788010eae05b4f5e921b3"

SRC_URI = "http://nmap.org/dist/${BP}.tar.bz2 \
           file://0001-redefine-the-python-library-install-dir.patch \
           file://0002-replace-.-shtool-mkdir-with-coreutils-mkdir-command.patch \
           file://0003-Include-time.h-header-to-pass-clang-compilation.patch \
           file://0004-Fix-building-with-libc.patch \
           file://0005-fix-racing-between-build-ncat-and-build-lua.patch \
           file://0006-Fix-build-with-libpcap-1.10.5.patch \
           "
SRC_URI[sha256sum] = "a5479f2f8a6b0b2516767d2f7189c386c1dc858d997167d7ec5cfc798c7571a1" 

UPSTREAM_CHECK_REGEX = "nmap-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep pkgconfig python3native

PACKAGECONFIG ?= "pcre ncat nping pcap"

PACKAGECONFIG[pcap] = "--with-pcap=linux, --without-pcap, libpcap, libpcap"
PACKAGECONFIG[pcre] = "--with-libpcre=${STAGING_LIBDIR}/.., --with-libpcre=included, libpcre"
PACKAGECONFIG[ssl] = "--with-openssl=${STAGING_LIBDIR}/.., --without-openssl, openssl, openssl"
PACKAGECONFIG[ssh2] = "--with-openssh2=${STAGING_LIBDIR}/.., --without-openssh2, libssh2, libssh2"
PACKAGECONFIG[libz] = "--with-libz=${STAGING_LIBDIR}/.., --without-libz, zlib, zlib"

# disable/enable packages
PACKAGECONFIG[nping] = ",--without-nping,"
PACKAGECONFIG[ncat] = ",--without-ncat,"
PACKAGECONFIG[ndiff] = "--with-ndiff=yes,--without-ndiff,python3 python3-setuptools-native"
PACKAGECONFIG[update] = ",--without-nmap-update,"

EXTRA_OECONF = "--with-libdnet=included --with-liblinear=included --without-subversion --with-liblua=included"

# zenmap needs python-pygtk which has been removed
# it also only works with python2
# disable for now until py3 is supported
EXTRA_OECONF += "--without-zenmap"

export PYTHON_SITEPACKAGES_DIR

do_configure() {
    autoconf
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf
}

do_install:append() {
    for f in ndiff uninstall_ndiff; do
        if [ -f ${D}${bindir}/$f ]; then
            sed -i 's@^#!.*$@#!/usr/bin/env python3@g' ${D}${bindir}/$f
        fi
    done
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR} ${datadir}/ncat"

RDEPENDS:${PN} += " \
    python3-difflib \
    python3-asyncio \
    python3-xml \
"
