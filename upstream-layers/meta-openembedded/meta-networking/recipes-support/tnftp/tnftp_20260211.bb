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
           file://0001-Add-casts-to-appease-conversions-between-wchar_t-and.patch \
           file://0002-Add-casts-to-appease-conversions-between-wchar_t-and.patch \
"

inherit autotools update-alternatives pkgconfig

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
ALTERNATIVE_TARGET[ftp] = "${bindir}/tnftp"

FILES:${PN} = "${bindir}/tnftp"

LIC_FILES_CHKSUM = "file://COPYING;md5=a78330785e3081e1679266f0ba58c555"
SRC_URI[sha256sum] = "101cda6927e5de4338ad9d4b264304d7d15d6a78b435968a7b95093e0a2efe03"

PACKAGECONFIG ?= "openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[openssl] = "--enable-ssl, --disable-ssl --with-ssl=no, openssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
