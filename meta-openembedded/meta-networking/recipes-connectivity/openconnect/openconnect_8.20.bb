SUMMARY = "Open client for Cisco AnyConnect VPN"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=8c2e1ec1540fb3e0beb68361344cba7e"

SRC_URI = " \
    git://git.infradead.org/users/dwmw2/openconnect.git;branch=master \
"
SRCREV = "03a3b9c76a9b6d0a65073b6bebbc1192e3445507"

DEPENDS = "vpnc libxml2 krb5 gettext-native"
RDEPENDS:${PN} = "bash python3-core vpnc-script"

PACKAGECONFIG ??= "gnutls lz4 libproxy"

# config defaults
PACKAGECONFIG[gnutls]    = "--with-gnutls,--without-gnutls,gnutls,"
PACKAGECONFIG[lz4]       = "--with-lz4,--without-lz4,lz4,"
PACKAGECONFIG[libproxy]  = "--with-libproxy,--without-libproxy,libproxy,"

# not config defaults
PACKAGECONFIG[pcsc-lite] = "--with-libpcsclite,--without-libpcsclite,pcsc-lite,"

S = "${WORKDIR}/git"

inherit autotools pkgconfig bash-completion

EXTRA_OECONF += "--with-vpnc-script=${sysconfdir}/vpnc/vpnc-script \
                 --disable-static"

do_install:append() {
    rm ${D}/usr/libexec/openconnect/hipreport-android.sh
}
