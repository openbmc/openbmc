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
LICENSE = "BSD-4-Clause"

DEPENDS = "ncurses"

SRC_URI = "ftp://ftp.netbsd.org/pub/NetBSD/misc/tnftp/${BPN}-${PV}.tar.gz \
           file://0001-libedit-Include-missing-header-stdc-predef.h.patch \
"

inherit autotools update-alternatives pkgconfig

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "ftp"
ALTERNATIVE_LINK_NAME_${PN} = "${bindir}/ftp"
ALTERNATIVE_TARGET_${PN}  = "${bindir}/tnftp"

FILES:${PN} = "${bindir}/tnftp"

LIC_FILES_CHKSUM = "file://COPYING;md5=b4248c6fb8ecff27f256ba97b25f1a21"
SRC_URI[md5sum] = "fdb6dd1b53dca79148c395b77c6dba5a"
SRC_URI[sha256sum] = "101901e90b656c223ec8106370dd0d783fb63d26aa6f0b2a75f40e86a9f06ea2"

PACKAGECONFIG ?= "openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[openssl] = "--enable-ssl, --disable-ssl --with-ssl=no, openssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
