SUMMARY = "network auditing tool"
DESCRIPTION = "Nmap ("Network Mapper") is a free and open source (license) utility for network discovery and security auditing.\nGui support via appending to IMAGE_FEATURES x11-base in local.conf"
SECTION = "security"
LICENSE = "GPL-2.0"

LIC_FILES_CHKSUM = "file://COPYING;beginline=7;endline=12;md5=66938a7e5b4c118eda78271de14874c2"

SRC_URI = "http://nmap.org/dist/${BP}.tar.bz2 \
           file://nmap-redefine-the-python-library-dir.patch \
           file://nmap-replace-shtool-mkdir-with-coreutils-mkdir-command.patch \
           file://0001-Include-time.h-header-to-pass-clang-compilation.patch \
           file://0002-Fix-building-with-libc.patch \
           file://0001-Make-ndiff-support-python3.patch \
           file://0001-configure.ac-make-ndiff-depend-on-python3.patch \
           "

SRC_URI[md5sum] = "d37b75b06d1d40f27b76d60db420a1f5"
SRC_URI[sha256sum] = "fcfa5a0e42099e12e4bf7a68ebe6fde05553383a682e816a7ec9256ab4773faa"

inherit autotools-brokensep pkgconfig python3native

PACKAGECONFIG ?= "ncat nping ndiff pcap"

PACKAGECONFIG[pcap] = "--with-pcap=linux, --without-pcap, libpcap, libpcap"
PACKAGECONFIG[pcre] = "--with-libpcre=${STAGING_LIBDIR}/.., --with-libpcre=included, libpcre"
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

do_install_append() {
    if [ -f "${D}${bindir}/ndiff" ]; then
       sed -i 's@^#!.*$@#!/usr/bin/env python3@g'   ${D}${bindir}/ndiff
    fi
}

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR} ${datadir}/ncat"

RDEPENDS_${PN} += "python3-core"
