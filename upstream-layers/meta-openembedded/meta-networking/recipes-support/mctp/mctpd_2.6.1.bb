require mctp.inc

SUMMARY = "Management Component Transport Protocol (MCTP) control protocol daemon"

inherit systemd features_check useradd

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

SYSTEMD_SERVICE:${PN} = "mctpd.service mctp.target mctp-local.target"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -U -s /bin/false mctpd"

DEPENDS += "systemd"
REQUIRED_DISTRO_FEATURES = "systemd"

# Meson adds mctp utility binaries to FILES and do_install(), which cannot be
# overriden by MESON_TARGET. Override FILES and remove binaries to correct.
FILES:${PN} = "${datadir}/dbus-1/system.d/mctpd.conf ${sbindir}/mctpd"

do_install:append () {
    rm -r ${D}${bindir}
}
