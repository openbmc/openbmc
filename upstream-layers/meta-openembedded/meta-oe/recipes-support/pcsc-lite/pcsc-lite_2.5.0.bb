SUMMARY = "PC/SC Lite smart card framework and applications"
HOMEPAGE = "https://pcsclite.apdu.fr/"
LICENSE = "BSD-3-Clause & GPL-3.0-or-later"
LICENSE:${PN} = "BSD-3-Clause"
LICENSE:${PN}-lib = "BSD-3-Clause"
LICENSE:${PN}-doc = "BSD-3-Clause"
LICENSE:${PN}-dev = "BSD-3-Clause"
LICENSE:${PN}-dbg = "BSD-3-Clause & GPL-3.0-or-later"
LICENSE:${PN}-spy = "GPL-3.0-or-later"
LICENSE:${PN}-spy-dev = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9637dc508442c2f458df6444fca97e09"
DEPENDS = "autoconf-archive-native flex-native"

SRC_URI = "https://pcsclite.apdu.fr/files/${BP}.tar.xz"
SRC_URI[sha256sum] = "59b3c4b5be4ab228698edeb5b3ef46ad54ea217e7dd0891372770bb92b55db92"

inherit meson systemd pkgconfig perlnative

EXTRA_OEMESON = " \
    -Dlibusb=false \
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
               ${nonarch_libdir}/systemd/user \
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
    # pcsc-lite installs pcscd-sysusers.conf into systemd's sysusersdir when
    # the systemd pkg-config variable is available (${libdir}/sysusers.d),
    # and falls back to ${exec_prefix}/sysusers.d otherwise. Drop both so the
    # file is not left unpackaged (installed-vs-shipped QA).
    rm -rf ${D}${libdir}/sysusers.d
    rm -rf ${D}${exec_prefix}/sysusers.d
}

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "pcscd.socket"
RDEPENDS:${PN}-spy += "python3-core"

BBCLASSEXTEND = "native"
