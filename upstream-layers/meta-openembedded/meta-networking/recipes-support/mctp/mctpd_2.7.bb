require mctp.inc

SUMMARY = "Management Component Transport Protocol (MCTP) control protocol daemon"

inherit systemd features_check useradd

MESON_INSTALL_TAGS += "mctpd"

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/conf/mctpd.service \
            ${D}${systemd_system_unitdir}/mctpd.service
    install -m 0644 ${S}/conf/*.target \
            ${D}${systemd_system_unitdir}/
    install -d ${D}${datadir}/dbus-1/system.d
    install -m 0644 ${S}/conf/mctpd-dbus.conf \
            ${D}${datadir}/dbus-1/system.d/mctpd.conf
}
FILES:${PN} += "${datadir}/dbus-1/system.d/mctpd.conf"

SYSTEMD_SERVICE:${PN} = "mctpd.service mctp.target mctp-local.target"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -U -s /bin/false mctpd"

DEPENDS += "systemd"
REQUIRED_DISTRO_FEATURES = "systemd"
