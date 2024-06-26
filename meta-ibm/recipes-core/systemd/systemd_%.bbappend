FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ibm-ac-server = " file://journald-storage-policy.conf"
SRC_URI:append:ibm-ac-server = " file://systemd-journald-override.conf"
SRC_URI:append:ibm-ac-server = " file://journald-size-policy-2MB.conf"

SRC_URI:append:p10bmc = " file://journald-storage-policy.conf"
SRC_URI:append:p10bmc = " file://systemd-journald-override.conf"
SRC_URI:append:p10bmc = " file://journald-size-policy-16MB.conf"
SRC_URI:append:p10bmc = " file://vm.conf"
SRC_URI:append:p10bmc = " file://network.conf"
SRC_URI:append:p10bmc = " file://systemd-networkd-only-wait-for-one.conf"
SRC_URI:append:p10bmc = " file://systemd-sulogin-force.conf"

SRC_URI:append:genesis3 = " file://systemd-networkd-only-wait-for-one.conf"
SRC_URI:append:sbp1 = " file://systemd-networkd-only-wait-for-one.conf"

SRC_URI:append:system1 = " file://systemd-networkd-only-wait-for-one.conf"
SRC_URI:append:system1 = " file://journald-storage-policy.conf"
SRC_URI:append:system1 = " file://journald-size-policy-16MB.conf"
SRC_URI:append:system1 = " file://systemd-journald-override.conf"

FILES:${PN}:append:ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES:${PN}:append:ibm-ac-server = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES:${PN}:append:ibm-ac-server = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

FILES:${PN}:append:p10bmc = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES:${PN}:append:p10bmc = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES:${PN}:append:p10bmc = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"
FILES:${PN}:append:p10bmc = " ${sysconfdir}/sysctl.d/vm.conf"
FILES:${PN}:append:p10bmc = " ${sysconfdir}/sysctl.d/network.conf"
FILES:${PN}:append:p10bmc = " ${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf"
FILES:${PN}:append:p10bmc = " ${systemd_system_unitdir}/emergency.service.d/systemd-sulogin-force.conf"
FILES:${PN}:append:p10bmc = " ${systemd_system_unitdir}/rescue.service.d/systemd-sulogin-force.conf"

FILES:${PN}:append:genesis3 = " ${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf"
FILES:${PN}:append:sbp1 = " ${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf"

FILES:${PN}:append:system1 = " ${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf"
FILES:${PN}:append:system1 = " ${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf"
FILES:${PN}:append:system1 = " ${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf"
FILES:${PN}:append:system1 = " ${systemd_unitdir}/journald.conf.d/journald-size-policy.conf"

do_install:append:ibm-ac-server() {
        install -m 644 -D ${UNPACKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${UNPACKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${UNPACKDIR}/journald-size-policy-2MB.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}
do_install:append:p10bmc() {
        install -m 644 -D ${UNPACKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${UNPACKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${UNPACKDIR}/journald-size-policy-16MB.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
        install -m 644 -D ${UNPACKDIR}/vm.conf ${D}${sysconfdir}/sysctl.d/vm.conf
        install -m 644 -D ${UNPACKDIR}/network.conf ${D}${sysconfdir}/sysctl.d/network.conf
        install -m 644 -D ${UNPACKDIR}/systemd-networkd-only-wait-for-one.conf ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
        install -m 644 -D ${UNPACKDIR}/systemd-sulogin-force.conf ${D}${systemd_system_unitdir}/emergency.service.d/systemd-sulogin-force.conf
        install -m 644 -D ${UNPACKDIR}/systemd-sulogin-force.conf ${D}${systemd_system_unitdir}/rescue.service.d/systemd-sulogin-force.conf
}

# Genesis3 and SBP1 uses both BMC's RGMII MACs, so wait for only one to be online
do_install:append:genesis3() {
        install -d ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/
        install -m 644 -D ${UNPACKDIR}/systemd-networkd-only-wait-for-one.conf ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
}
do_install:append:sbp1() {
        install -d ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/
        install -m 644 -D ${UNPACKDIR}/systemd-networkd-only-wait-for-one.conf ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
}
do_install:append:system1() {
        install -d ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/
        install -m 644 -D ${UNPACKDIR}/systemd-networkd-only-wait-for-one.conf ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
        install -m 644 -D ${UNPACKDIR}/journald-storage-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
        install -m 644 -D ${UNPACKDIR}/systemd-journald-override.conf ${D}${systemd_system_unitdir}/systemd-journald.service.d/systemd-journald-override.conf
        install -m 644 -D ${UNPACKDIR}/journald-size-policy-16MB.conf ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf
}
# Witherspoon doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:witherspoon = "zstd"
PACKAGECONFIG:append:witherspoon = " xz"
