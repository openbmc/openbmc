SUMMARY = "User space daemon for extended IEEE 802.11 management"
HOMEPAGE = "http://w1.fi/hostapd/"
SECTION = "kernel/userland"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://hostapd/README;md5=1ec986bec88070e2a59c68c95d763f89"

DEPENDS = "libnl openssl"

SRC_URI = " \
    http://w1.fi/releases/hostapd-${PV}.tar.gz \
    file://defconfig \
    file://init \
    file://hostapd.service \
"

SRC_URI[md5sum] = "ed2c254e5f400838cb9d8e7b6e43b86c"
SRC_URI[sha256sum] = "929f522be6eeec38c53147e7bc084df028f65f148a3f7e4fa6c4c3f955cee4b0"

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
