SUMMARY = "network auditing tool"
DESCRIPTION = "Nmap (Network Mapper) is a free and open source (license) utility for network discovery and security auditing.\nGui support via appending to IMAGE_FEATURES x11-base in local.conf"
SECTION = "security"
LICENSE = "NPSL"

LIC_FILES_CHKSUM = "file://LICENSE;md5=895af8527fe4bcb72f271fd1841fd2f6"

SRC_URI = "http://nmap.org/dist/${BP}.tar.bz2 \
           file://nmap-replace-shtool-mkdir-with-coreutils-mkdir-command.patch \
           file://0001-Include-time.h-header-to-pass-clang-compilation.patch \
           file://0002-Fix-building-with-libc.patch \
           file://0001-fix-racing-between-build-ncat-and-build-lua.patch \
           file://0004-libdnet-fix-PF_PACKET-conftest-nested-function.patch \
           "
SRC_URI[sha256sum] = "df512492ffd108e53a27a06f26d8635bbe89e0e569455dc8ffef058c035d51b2"

UPSTREAM_CHECK_REGEX = "nmap-(?P<pver>\d+(\.\d+)+)\.tar"

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
PACKAGECONFIG[ndiff] = "--with-ndiff=yes,--without-ndiff,python3 python3-setuptools-native"
PACKAGECONFIG[update] = ",--without-nmap-update,"

EXTRA_OECONF = "--with-libdnet=included --with-liblinear=included --without-subversion --with-liblua=included"

# zenmap needs python-pygtk which has been removed
# it also only works with python2
# disable for now until py3 is supported
EXTRA_OECONF += "--without-zenmap"

# nmap links the bundled libdnet statically (libdnet-stripped/src/.libs/libdnet.a),
# but OE's default --disable-static propagates into that libtool sub-package
# (last on the command line, so it wins) and only the shared library is built.
# Drop --disable-static so libtool also produces libdnet.a.
DISABLE_STATIC = ""

# nmap's top-level Makefile does not order the link of nmap/nping after the
# recursive build of the bundled libdnet, so a parallel build can try to link
# against libdnet-stripped/src/.libs/libdnet.a before it exists. Serialize.
PARALLEL_MAKE = ""

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

RDEPENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'ndiff', 'python3-difflib python3-asyncio python3-xml', '', d)}"
