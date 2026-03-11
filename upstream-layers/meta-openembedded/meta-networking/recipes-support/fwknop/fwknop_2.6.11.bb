SUMMARY = "fwknop - Single Packet Authorization"
HOMEPAGE = "http://www.cipherdyne.org/fwknop/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit autotools-brokensep pkgconfig

SRC_URI = "http://www.cipherdyne.org/${BPN}/download/${BPN}-${PV}.tar.bz2 \
           file://0001-Use-pkg-config-to-find-gpgme.patch \
          "
SRC_URI[sha256sum] = "a4ec7c22dd90dd684f9f7b96d3a901c4131ec8c7a3b9db26d0428513f6774c64"

DEPENDS = "libpcap gpgme"

EXTRA_OECONF = "--with-iptables=${sbindir}/iptables \
                --with-gpg=${bindir}/gpg \
                --with-wget=${base_bindir}/wget"

do_configure:prepend () {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/config
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/config
}

PACKAGES =+ "${PN}-client ${PN}-daemon"

FILES:${PN}-client = "${bindir}/fwknop"
FILES:${PN}-daemon = "${sbindir}/fwknopd \
                      ${sysconfdir}/fwknop/access.conf \
                      ${sysconfdir}/fwknop/fwknopd.conf"

