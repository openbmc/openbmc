SUMMARY = "TLS handshake utilities for in-kernel TLS consumers"
DESCRIPTION = "In-kernel TLS consumers need a mechanism to perform TLS \
handshakes on a connected socket to negotiate TLS session parameters that \
can then be programmed into the kernel's TLS record protocol engine."
DEPENDS = "gnutls keyutils glib-2.0 libnl"
RDEPENDS:${PN} += " gnutls"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d568123389d9a12625cca2b089b1728b"

SRC_URI = "https://github.com/oracle/ktls-utils/releases/download/${BP}/${BP}.tar.gz"
SRC_URI[sha256sum] = "acd9e1851e492e7e16b879b30fc800f4a98d4214fa534ebb7e24d379f5984770"

inherit autotools pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemd,,systemd"

SYSTEMD_SERVICE:${PN} = "tlshd.service"
# ../../../sources/ktls-utils-1.3.0/src/tlshd/quic.c:515:58: error: comparison of integers of different signs: 'unsigned long' and 'long' [-Werror,-Wsign-compare]
CFLAGS:append:libc-musl:toolchain-clang = " -Wno-error=sign-compare"
