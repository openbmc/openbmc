SUMMARY = "Enhanced NetBSD ftp client"
DESCRIPTION = "tnftp (formerly known as lukemftp) is a port of the NetBSD FTP client \
to other systems. It offers many enhancements over the traditional \
BSD FTP client, including command-line editing, command-line fetches \
of FTP and HTTP URLs (including via proxies), command-line uploads of \
FTP URLs, context-sensitive word completion, dynamic progress bar, \
IPv6 support, modification time preservation, paging of local and \
remote files, passive mode support (with fallback to active mode), \
SOCKS support, TIS FWTK gate-ftp server support, and transfer rate \
throttling."

SECTION = "net"
LICENSE = "BSD-2-Clause"

DEPENDS = "ncurses"

SRC_URI = "https://ftp.netbsd.org/pub/NetBSD/misc/tnftp/${BPN}-${PV}.tar.gz \
           file://0001-libedit-Include-missing-header-stdc-predef.h.patch \
"

inherit autotools update-alternatives pkgconfig

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
ALTERNATIVE_TARGET[ftp] = "${bindir}/tnftp"

FILES:${PN} = "${bindir}/tnftp"

LIC_FILES_CHKSUM = "file://COPYING;md5=fbbb944979c7466ed5509b4bbc6c328b"
SRC_URI[sha256sum] = "be0134394bd7d418a3b34892b0709eeb848557e86474e1786f0d1a887d3a6580"

PACKAGECONFIG ?= "openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[openssl] = "--enable-ssl, --disable-ssl --with-ssl=no, openssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
