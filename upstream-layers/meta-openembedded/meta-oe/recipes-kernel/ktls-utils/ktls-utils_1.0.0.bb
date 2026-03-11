SUMMARY = "TLS handshake utilities for in-kernel TLS consumers"
DESCRIPTION = "In-kernel TLS consumers need a mechanism to perform TLS \
handshakes on a connected socket to negotiate TLS session parameters that \
can then be programmed into the kernel's TLS record protocol engine."
DEPENDS = "gnutls keyutils glib-2.0 libnl"
RDEPENDS:${PN} += " gnutls"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d568123389d9a12625cca2b089b1728b"

SRCREV = "c787cd26330af457e1ca9cf43232db2c71c03154"
SRC_URI = " \
	git://github.com/oracle/ktls-utils.git;nobranch=1;protocol=https \
	file://0001-tlshd-Define-ALLPERMS-if-it-doesn-t-exist-to-fix-mus.patch \
	file://0002-tlshd-configure.ac-Use-AC_CHECK_HEADER-instead-of-AC.patch \
	"


inherit autotools-brokensep pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemd,,systemd"

SYSTEMD_SERVICE:${PN} = "tlshd.service"
