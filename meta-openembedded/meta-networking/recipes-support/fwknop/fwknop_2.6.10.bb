SUMMARY = "fwknop - Single Packet Authorization"
HOMEPAGE = "http://www.cipherdyne.org/fwknop/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    "
inherit autotools-brokensep pkgconfig

SRC_URI = "http://www.cipherdyne.org/${BPN}/download/${BPN}-${PV}.tar.bz2 \
           file://0001-Fix-compilation-with-GCC-s-fno-common-flag-fixes-305.patch \
           file://0001-Use-pkg-config-to-find-gpgme.patch \
	   file://0001-configure.ac-Fix-missing-comma-in-AS_IF.patch \
          "
SRC_URI[sha256sum] = "f6c09bec97ed8e474a98ae14f9f53e1bcdda33393f20667b6af3fb6bb894ca77"

DEPENDS = "libpcap gpgme"

EXTRA_OECONF = " --with-iptables=${sbindir}/iptables"

do_configure:prepend () {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/config
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/config
}

PACKAGES =+ "${PN}-client ${PN}-daemon"

FILES:${PN}-client = "${bindir}/fwknop"
FILES:${PN}-daemon = "${sbindir}/fwknopd \
                      ${sysconfdir}/fwknop/access.conf \
                      ${sysconfdir}/fwknop/fwknopd.conf"

