SUMMARY = "Open client for Cisco AnyConnect VPN"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "vpnc libxml2 gnutls lz4 krb5 libproxy pcsc-lite"
RDEPENDS_${PN} = "vpnc"

PV = "7.06"

SRCREV = "35542d52202672b8c12ecc63867432128244013a"
SRC_URI = "git://git.infradead.org/users/dwmw2/openconnect.git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-vpnc-script=${SYSROOT_DESTDIR}${sysconfdir}/vpnc/vpnc-script \
                 --disable-static"
