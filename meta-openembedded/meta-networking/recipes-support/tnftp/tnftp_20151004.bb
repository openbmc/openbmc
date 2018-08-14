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
    file://tnftp-autotools.patch \
"

inherit autotools update-alternatives pkgconfig

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "ftp"
ALTERNATIVE_LINK_NAME_${PN} = "${bindir}/ftp"
ALTERNATIVE_TARGET_${PN}  = "${bindir}/tnftp"

FILES_${PN} = "${bindir}/tnftp"

LIC_FILES_CHKSUM = "file://COPYING;md5=6d6796cb166a9bb050958241dad9479e"
SRC_URI[md5sum] = "a49fbe752318d5a7893f900046ea00d5"
SRC_URI[sha256sum] = "c94a8a49d3f4aec1965feea831d4d5bf6f90c65fd8381ee0863d11a5029a43a0"

PACKAGECONFIG ?= "openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[openssl] = "--enable-ssl, --disable-ssl --with-ssl=no, openssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
