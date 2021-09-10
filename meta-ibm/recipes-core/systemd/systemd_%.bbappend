FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ibm-ac-server = " file://journald-storage-policy.conf"
SRC_URI:append:ibm-ac-server = " file://systemd-journald-override.conf"
SRC_URI:append:ibm-ac-server = " file://journald-size-policy-2MB.conf"

SRC_URI:append:p10bmc = " file://journald-storage-policy.conf"
SRC_URI:append:p10bmc = " file://systemd-journald-override.conf"
SRC_URI:append:p10bmc = " file://journald-size-policy-16MB.conf"

FILES:${PN}:append:ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES:${PN}:append:ibm-ac-server = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES:${PN}:append:ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

FILES:${PN}:append:p10bmc = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES:${PN}:append:p10bmc = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES:${PN}:append:p10bmc = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

do_install:append:ibm-ac-server() {
        install -m 644 -D ${WORKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${WORKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${WORKDIR}/journald-size-policy-2MB.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}
do_install:append:p10bmc() {
        install -m 644 -D ${WORKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${WORKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${WORKDIR}/journald-size-policy-16MB.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}

# Witherspoon doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:witherspoon = "zstd"
PACKAGECONFIG:append:witherspoon = " xz"
