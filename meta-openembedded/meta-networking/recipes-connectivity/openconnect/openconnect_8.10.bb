SUMMARY = "Open client for Cisco AnyConnect VPN"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=243b725d71bb5df4a1e5920b344b86ad"

SRC_URI = " \
    git://git.infradead.org/users/dwmw2/openconnect.git \
"
SRCREV = "9d287e40c57233190a51b6434ba7345370e36f38"

DEPENDS = "vpnc libxml2 krb5 gettext-native"
RDEPENDS_${PN} = "bash python3-core vpnc-script"

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

do_install_append() {
    rm ${D}/usr/libexec/openconnect/hipreport-android.sh
}
