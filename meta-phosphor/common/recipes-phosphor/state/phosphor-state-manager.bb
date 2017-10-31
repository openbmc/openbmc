SUMMARY = "Phosphor State Management"
DESCRIPTION = "Phosphor State Manager provides a set of system state \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

STATE_MGR_PACKAGES = " \
    ${PN}-host \
    ${PN}-chassis \
    ${PN}-bmc \
    ${PN}-discover \
    ${PN}-host-check \
"
PACKAGES =+ "${STATE_MGR_PACKAGES}"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${STATE_MGR_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${STATE_MGR_PACKAGES}"

DBUS_PACKAGES = "${STATE_MGR_PACKAGES}"

SYSTEMD_PACKAGES = "${PN}-discover"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "cereal"
RDEPENDS_${PN} += "sdbusplus"

RDEPENDS_${PN}-host += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-chassis += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-bmc += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-discover += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-host-check += "libsystemd phosphor-dbus-interfaces"

FILES_${PN}-host = "${sbindir}/phosphor-host-state-manager"
DBUS_SERVICE_${PN}-host += "xyz.openbmc_project.State.Host.service"
DBUS_SERVICE_${PN}-host += "phosphor-reboot-host@.service"

FILES_${PN}-chassis = "${sbindir}/phosphor-chassis-state-manager"
DBUS_SERVICE_${PN}-chassis += "xyz.openbmc_project.State.Chassis.service"

FILES_${PN}-bmc = "${sbindir}/phosphor-bmc-state-manager"
DBUS_SERVICE_${PN}-bmc += "xyz.openbmc_project.State.BMC.service"

FILES_${PN}-discover = "${sbindir}/phosphor-discover-system-state"
SYSTEMD_SERVICE_${PN}-discover += "phosphor-discover-system-state@.service"

FILES_${PN}-host-check = "${sbindir}/phosphor-host-check"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-check@.service"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-running@.service"

RESET_CHECK_TMPL = "phosphor-reset-host-check@.service"
RESET_CHECK_TGTFMT = "obmc-host-reset@{1}.target"
RESET_CHECK_INSTFMT = "phosphor-reset-host-check@{0}.service"
RESET_CHECK_FMT = "../${RESET_CHECK_TMPL}:${RESET_CHECK_TGTFMT}.requires/${RESET_CHECK_INSTFMT}"

RESET_RUNNING_TMPL = "phosphor-reset-host-running@.service"
RESET_RUNNING_TGTFMT = "obmc-host-reset@{1}.target"
RESET_RUNNING_INSTFMT = "phosphor-reset-host-running@{0}.service"
RESET_RUNNING_FMT = "../${RESET_RUNNING_TMPL}:${RESET_RUNNING_TGTFMT}.requires/${RESET_RUNNING_INSTFMT}"

SYSTEMD_LINK_${PN}-host-check += "${@compose_list_zip(d, 'RESET_CHECK_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN}-host-check += "${@compose_list_zip(d, 'RESET_RUNNING_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Force the standby target to run the host reset check target
RESET_TMPL_CTRL = "obmc-host-reset@.target"
SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"
RESET_INSTFMT_CTRL = "obmc-host-reset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN}-host-check += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"

TMPL = "phosphor-discover-system-state@.service"
INSTFMT = "phosphor-discover-system-state@{0}.service"
FMT = "../${TMPL}:${SYSTEMD_DEFAULT_TARGET}.wants/${INSTFMT}"
SYSTEMD_LINK_${PN}-discover += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"

# Force the shutdown target to run the chassis-poweroff target
CHASSIS_STOP_TMPL = "obmc-chassis-poweroff@.target"
HOST_STOP_TGTFMT = "obmc-host-shutdown@{1}.target"
CHASSIS_STOP_INSTFMT = "obmc-chassis-poweroff@{0}.target"
HOST_STOP_FMT = "../${CHASSIS_STOP_TMPL}:${HOST_STOP_TGTFMT}.requires/${CHASSIS_STOP_INSTFMT}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_STOP_FMT', 'OBMC_CHASSIS_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Force the host reboot target to run the shutdown target
HOST_SHUTDOWN_TMPL = "obmc-host-shutdown@.target"
HOST_REBOOT_TGTFMT = "obmc-host-reboot@{0}.target"
HOST_SHUTDOWN_INSTFMT = "obmc-host-shutdown@{0}.target"
HOST_REBOOT_FMT = "../${HOST_SHUTDOWN_TMPL}:${HOST_REBOOT_TGTFMT}.requires/${HOST_SHUTDOWN_INSTFMT}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_REBOOT_FMT', 'OBMC_HOST_INSTANCES')}"

# And also force the reboot target to call the host start service
HOST_REBOOT_SVC = "phosphor-reboot-host@.service"
HOST_REBOOT_SVC_INST = "phosphor-reboot-host@{0}.service"
HOST_REBOOT_SVC_FMT = "../${HOST_REBOOT_SVC}:${HOST_REBOOT_TGTFMT}.requires/${HOST_REBOOT_SVC_INST}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_REBOOT_SVC_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "31292085a7310ee11d7b1e5b3b26d9a387f9d273"

S = "${WORKDIR}/git"
