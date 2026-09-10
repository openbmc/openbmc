SUMMARY = "PC/SC Lite smart card framework and applications"
HOMEPAGE = "https://pcsclite.apdu.fr/"
LICENSE = "BSD-3-Clause AND GPL-3.0-or-later"
LICENSE:${PN} = "BSD-3-Clause"
LICENSE:${PN}-lib = "BSD-3-Clause"
LICENSE:${PN}-doc = "BSD-3-Clause"
LICENSE:${PN}-dev = "BSD-3-Clause"
LICENSE:${PN}-dbg = "BSD-3-Clause AND GPL-3.0-or-later"
LICENSE:${PN}-spy = "GPL-3.0-or-later"
LICENSE:${PN}-spy-dev = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9637dc508442c2f458df6444fca97e09"
DEPENDS = "autoconf-archive-native flex-native"

SRC_URI = "https://pcsclite.apdu.fr/files/${BP}.tar.xz"
SRC_URI[sha256sum] = "bfcfe38a20afc49849c6bf55325e38f449fc4b26d3923fdc32b969ae41a8741b"

inherit meson systemd pkgconfig perlnative

EXTRA_OEMESON = " \
    -Dlibusb=false \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-Dsystemdunit=system', '', d)} \
    -Dusbdropdir=${libdir}/pcsc/drivers \
"

S = "${UNPACKDIR}/pcsc-lite-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd polkit', d)} udev"
PACKAGECONFIG:class-native ??= ""

PACKAGECONFIG[systemd]  = ",-Dlibsystemd=false,systemd,"
PACKAGECONFIG[udev] = "-Dlibudev=true,-Dlibudev=false,udev"
PACKAGECONFIG[polkit] = ",-Dpolkit=false,polkit"

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-lib ${PN}-doc ${PN}-spy ${PN}-spy-dev"

RRECOMMENDS:${PN} = "ccid"
RRECOMMENDS:${PN}:class-native = ""
RPROVIDES:${PN}:append:class-native = " pcsc-lite-lib-native"

FILES:${PN} = "${sbindir}/pcscd \
               ${datadir}/polkit-1 \
               ${systemd_system_unitdir}/pcscd.service \
               ${systemd_system_unitdir}/pcscd.socket \
               ${libdir}/sysusers.d \
               ${exec_prefix}/sysusers.d \
               ${sysconfdir}/default/pcscd"
FILES:${PN}-lib = "${libdir}/libpcsclite*${SOLIBS}"
FILES:${PN}-dev = "${includedir} \
                   ${libdir}/pkgconfig \
                   ${libdir}/libpcsclite.la \
                   ${libdir}/libpcsclite_real.so \
                   ${libdir}/libpcsclite.so"

FILES:${PN}-spy = "${bindir}/pcsc-spy \
                   ${libdir}/libpcscspy*${SOLIBS}"
FILES:${PN}-spy-dev = "${libdir}/libpcscspy.la \
                       ${libdir}/libpcscspy.so "

do_install:append() {
    rm -rf ${D}${datadir}/metainfo
    # In sysvinit environments, remove all systemd-related files
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}; then
        # pcsc-lite installs pcscd-sysusers.conf into systemd's sysusersdir when
        # the systemd pkg-config variable is available (${libdir}/sysusers.d),
        # and falls back to ${exec_prefix}/sysusers.d otherwise. Drop both so the
        # file is not left unpackaged (installed-vs-shipped QA).
        rm -rf ${D}${libdir}/sysusers.d
        rm -rf ${D}${exec_prefix}/sysusers.d

        rm -rf ${D}${systemd_system_unitdir}
        rm -rf ${D}${libdir}/systemd
    fi
}

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "pcscd.socket"
RDEPENDS:${PN}-spy += "python3-core"

BBCLASSEXTEND = "native"
