SUMMARY = "network auditing tool"
DESCRIPTION = "Nmap (Network Mapper) is a free and open source (license) utility for network discovery and security auditing.\nGui support via appending to IMAGE_FEATURES x11-base in local.conf"
SECTION = "security"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=895af8527fe4bcb72f271fd1841fd2f6"

SRC_URI = "http://nmap.org/dist/${BP}.tar.bz2 \
           file://nmap-redefine-the-python-library-dir.patch \
           file://nmap-replace-shtool-mkdir-with-coreutils-mkdir-command.patch \
           file://0001-Include-time.h-header-to-pass-clang-compilation.patch \
           file://0002-Fix-building-with-libc.patch \
           file://0003-Fix-off-by-one-overflow-in-the-IP-protocol-table.patch \
           file://0001-fix-racing-between-build-ncat-and-build-lua.patch \
           "
SRC_URI[sha256sum] = "e14ab530e47b5afd88f1c8a2bac7f89cd8fe6b478e22d255c5b9bddb7a1c5778" 
inherit autotools-brokensep pkgconfig python3native

PACKAGECONFIG ?= "pcre ncat nping pcap"

PACKAGECONFIG[pcap] = "--with-pcap=linux, --without-pcap, libpcap, libpcap"
PACKAGECONFIG[pcre] = "--with-libpcre=${STAGING_LIBDIR}/.., --with-libpcre=included, libpcre2"
PACKAGECONFIG[ssl] = "--with-openssl=${STAGING_LIBDIR}/.., --without-openssl, openssl, openssl"
PACKAGECONFIG[ssh2] = "--with-openssh2=${STAGING_LIBDIR}/.., --without-openssh2, libssh2, libssh2"
PACKAGECONFIG[libz] = "--with-libz=${STAGING_LIBDIR}/.., --without-libz, zlib, zlib"

#disable/enable packages
PACKAGECONFIG[nping] = ",--without-nping,"
PACKAGECONFIG[ncat] = ",--without-ncat,"
PACKAGECONFIG[ndiff] = "--with-ndiff=yes,--without-ndiff,python3"
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
