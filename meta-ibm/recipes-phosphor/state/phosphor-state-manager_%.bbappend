EXTRA_OEMESON:append:witherspoon = " -Dwarm-reboot=disabled"

# The scheduled-host-transition package provides support to
# schedule power on and off operations for the host at some
# time in the future. IBM systems will utilize this feature
RRECOMMENDS:${PN}-host += "${PN}-scheduled-host-transition"

# IBM systems track the state of the hypervisor so bring
# in the needed package when the host state package is
# included
RRECOMMENDS:${PN}-host += "${PN}-hypervisor"

# IBM systems want the chassis package to not allow a
# system power on if chassis power is in a bad state
RRECOMMENDS:${PN}-chassis = "${PN}-chassis-check-power-status"

# Override critical services to monitor with IBM file
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
FILES:${PN}-bmc:append = " ${sysconfdir}/phosphor-service-monitor-default.json"
SRC_URI:append = " file://phosphor-service-monitor-default.json"
do_install:append() {
    install -d ${D}${sysconfdir}/phosphor-systemd-target-monitor
    install -m 0644 ${WORKDIR}/phosphor-service-monitor-default.json \
        ${D}${sysconfdir}/phosphor-systemd-target-monitor/
}
