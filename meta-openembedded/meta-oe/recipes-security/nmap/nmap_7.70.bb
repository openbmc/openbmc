SUMMARY = "network auditing tool"
DESCRIPTION = "Nmap ("Network Mapper") is a free and open source (license) utility for network discovery and security auditing.\nGui support via appending to IMAGE_FEATURES x11-base in local.conf"
SECTION = "security"
LICENSE = "GPL-2.0"

LIC_FILES_CHKSUM = "file://COPYING;beginline=7;endline=12;md5=1489288f46af415fadc4e8b6345ab9f4"

SRC_URI = "http://nmap.org/dist/${BP}.tar.bz2 \
           file://nmap-redefine-the-python-library-dir.patch \
           file://nmap-replace-shtool-mkdir-with-coreutils-mkdir-command.patch \
           file://0001-include-time.h-for-time-structure-definition.patch \
           file://0002-Fix-building-with-libc.patch \
           "

SRC_URI[md5sum] = "84eb6fbe788e0d4918c2b1e39421bf79"
SRC_URI[sha256sum] = "847b068955f792f4cc247593aca6dc3dc4aae12976169873247488de147a6e18"

inherit autotools-brokensep pkgconfig pythonnative

PACKAGECONFIG ?= "ncat nping ndiff pcap"

PACKAGECONFIG[pcap] = "--with-pcap=linux, --without-pcap, libpcap, libpcap"
PACKAGECONFIG[pcre] = "--with-libpcre=${STAGING_LIBDIR}/.., --with-libpcre=included, libpcre"
PACKAGECONFIG[ssl] = "--with-openssl=${STAGING_LIBDIR}/.., --without-openssl, openssl, openssl"
PACKAGECONFIG[ssh2] = "--with-openssh2=${STAGING_LIBDIR}/.., --without-openssh2, libssh2, libssh2"
PACKAGECONFIG[libz] = "--with-libz=${STAGING_LIBDIR}/.., --without-libz, zlib, zlib"

#disable/enable packages
PACKAGECONFIG[nping] = ",--without-nping,"
PACKAGECONFIG[ncat] = ",--without-ncat,"
PACKAGECONFIG[ndiff] = ",--without-ndiff,python"
PACKAGECONFIG[update] = ",--without-nmap-update,"

EXTRA_OECONF = "--with-libdnet=included --with-liblinear=included --without-subversion --with-liblua=included"

# zenmap needs python-pygtk which has been removed
# it also only works with python2
# disable for now until py3 is supported
EXTRA_OECONF += "--without-zenmap"

export PYTHON_SITEPACKAGES_DIR

do_configure() {
    autoconf
    oe_runconf
}

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR} ${datadir}/ncat"

RDEPENDS_${PN} = "python"
