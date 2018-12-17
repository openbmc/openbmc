SUMMARY = "User space daemon for extended IEEE 802.11 management"
HOMEPAGE = "http://w1.fi/hostapd/"
SECTION = "kernel/userland"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://hostapd/README;md5=8aa4e8c78b59b12016c4cb2d0a8db350"

DEPENDS = "libnl openssl"

SRC_URI = " \
    http://w1.fi/releases/hostapd-${PV}.tar.gz \
    file://defconfig \
    file://init \
    file://hostapd.service \
    file://0001-hostapd-Avoid-key-reinstallation-in-FT-handshake.patch \
    file://0002-Prevent-reinstallation-of-an-already-in-use-group-ke.patch \
    file://0003-Extend-protection-of-GTK-IGTK-reinstallation-of-WNM-.patch \
    file://0004-Prevent-installation-of-an-all-zero-TK.patch \
    file://0005-Fix-PTK-rekeying-to-generate-a-new-ANonce.patch \
    file://0006-TDLS-Reject-TPK-TK-reconfiguration.patch \
    file://0007-FT-Do-not-allow-multiple-Reassociation-Response-fram.patch \
    file://hostapd-CVE-2018-14526.patch \
"

SRC_URI[md5sum] = "eaa56dce9bd8f1d195eb62596eab34c7"
SRC_URI[sha256sum] = "01526b90c1d23bec4b0f052039cc4456c2fd19347b4d830d1d58a0a6aea7117d"

S = "${WORKDIR}/hostapd-${PV}"
B = "${WORKDIR}/hostapd-${PV}/hostapd"

inherit update-rc.d systemd pkgconfig distro_features_check

CONFLICT_DISTRO_FEATURES = "openssl-no-weak-ciphers"

INITSCRIPT_NAME = "hostapd"

SYSTEMD_SERVICE_${PN} = "hostapd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

do_configure_append() {
    install -m 0644 ${WORKDIR}/defconfig ${B}/.config
}

do_compile() {
    export CFLAGS="-MMD -O2 -Wall -g"
    export EXTRA_CFLAGS="${CFLAGS}"
    make V=1
}

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/init.d ${D}${systemd_unitdir}/system/
    install -m 0644 ${B}/hostapd.conf ${D}${sysconfdir}
    install -m 0755 ${B}/hostapd ${D}${sbindir}
    install -m 0755 ${B}/hostapd_cli ${D}${sbindir}
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/hostapd
    install -m 0644 ${WORKDIR}/hostapd.service ${D}${systemd_unitdir}/system/
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/hostapd.service
}

CONFFILES_${PN} += "${sysconfdir}/hostapd.conf"
