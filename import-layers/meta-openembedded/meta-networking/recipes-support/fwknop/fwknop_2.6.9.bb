SUMMARY = "fwknop - Single Packet Authorization"
HOMEPAGE = "http://www.cipherdyne.org/fwknop/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    "
inherit autotools-brokensep

SRC_URI = "http://www.cipherdyne.org/${BPN}/download/${BPN}-${PV}.tar.bz2 \
          "

SRC_URI[md5sum] = "e2c49e9674888a028bd443a55c3aaa22"
SRC_URI[sha256sum] = "5bf47fe1fd30e862d29464f762c0b8bf89b5e298665c37624d6707826da956d4"

DEPENDS = "libpcap gpgme"

EXTRA_OECONF = " --with-iptables=/usr/sbin/iptables \
               "

do_configure () {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/config
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/config

	aclocal
	libtoolize --automake --copy --force
	autoconf
	autoheader
	automake -a
	oe_runconf
}

PACKAGES =+ "${PN}-client ${PN}-daemon"

FILES_${PN}-client = "${bindir}/fwknop"
FILES_${PN}-daemon = "${sbindir}/fwknopd \
                      ${sysconfdir}/fwknop/access.conf \
                      ${sysconfdir}/fwknop/fwknopd.conf"

