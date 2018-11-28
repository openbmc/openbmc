SUMMARY = "Phosphor State Management"
DESCRIPTION = "Phosphor State Manager provides a set of system state \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

STATE_MGR_PACKAGES = " \
    ${PN}-host \
    ${PN}-chassis \
    ${PN}-bmc \
    ${PN}-discover \
    ${PN}-host-check \
    ${PN}-reset-sensor-states \
"
PACKAGE_BEFORE_PN += "${STATE_MGR_PACKAGES}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${STATE_MGR_PACKAGES}"

SYSTEMD_PACKAGES = "${PN}-discover \
                    ${PN}-reset-sensor-states \
"

# The host-check function will check if the host is running
# after a BMC reset.
# The reset-sensor-states function will reset the host
# sensors on a BMC reset or system power loss.
# Neither is required for host state function but are
# recommended to deal properly with these reset scenarios.
RRECOMMENDS_${PN}-host = "${PN}-host-check ${PN}-reset-sensor-states"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "libcereal"
RDEPENDS_${PN} += "sdbusplus"

RDEPENDS_${PN}-host += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-chassis += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-bmc += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-discover += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-host-check += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-reset-sensor-states += "libsystemd phosphor-dbus-interfaces"

FILES_${PN}-host = "${sbindir}/phosphor-host-state-manager"
DBUS_SERVICE_${PN}-host += "xyz.openbmc_project.State.Host.service"
DBUS_SERVICE_${PN}-host += "phosphor-reboot-host@.service"
SYSTEMD_ENVIRONMENT_FILE_${PN}-host += "obmc/phosphor-reboot-host/reboot.conf"
SYSTEMD_SERVICE_${PN}-host += "phosphor-reset-host-reboot-attempts@.service"

FILES_${PN}-chassis = "${sbindir}/phosphor-chassis-state-manager"
DBUS_SERVICE_${PN}-chassis += "xyz.openbmc_project.State.Chassis.service"

FILES_${PN}-bmc = "${sbindir}/phosphor-bmc-state-manager"
DBUS_SERVICE_${PN}-bmc += "xyz.openbmc_project.State.BMC.service"

FILES_${PN}-discover = "${sbindir}/phosphor-discover-system-state"
SYSTEMD_SERVICE_${PN}-discover += "phosphor-discover-system-state@.service"

FILES_${PN}-host-check = "${sbindir}/phosphor-host-check"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-check@.service"
SYSTEMD_SERVICE_${PN}-host-check += "phosphor-reset-host-running@.service"

SYSTEMD_SERVICE_${PN}-reset-sensor-states += "phosphor-reset-sensor-states@.service"

RESET_CHECK_TMPL = "phosphor-reset-host-check@.service"
RESET_CHECK_TGTFMT = "obmc-host-reset@{1}.target"
RESET_CHECK_INSTFMT = "phosphor-reset-host-check@{0}.service"
RESET_CHECK_FMT = "../${RESET_CHECK_TMPL}:${RESET_CHECK_TGTFMT}.requires/${RESET_CHECK_INSTFMT}"

SENSOR_RESET_TMPL = "phosphor-reset-sensor-states@.service"
SENSOR_RESET_TGTFMT = "obmc-host-reset@{1}.target"
SENSOR_RESET_INSTFMT = "phosphor-reset-sensor-states@{0}.service"
SENSOR_RESET_FMT = "../${SENSOR_RESET_TMPL}:${SENSOR_RESET_TGTFMT}.requires/${SENSOR_RESET_INSTFMT}"

RESET_RUNNING_TMPL = "phosphor-reset-host-running@.service"
RESET_RUNNING_TGTFMT = "obmc-host-reset@{1}.target"
RESET_RUNNING_INSTFMT = "phosphor-reset-host-running@{0}.service"
RESET_RUNNING_FMT = "../${RESET_RUNNING_TMPL}:${RESET_RUNNING_TGTFMT}.requires/${RESET_RUNNING_INSTFMT}"

SYSTEMD_LINK_${PN}-host-check += "${@compose_list_zip(d, 'RESET_CHECK_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN}-host-check += "${@compose_list_zip(d, 'RESET_RUNNING_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_LINK_${PN}-reset-sensor-states += "${@compose_list_zip(d, 'SENSOR_RESET_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

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

# And also force the reboot target to call the host startmin service
HOST_REBOOT_SVC = "phosphor-reboot-host@.service"
HOST_REBOOT_SVC_INST = "phosphor-reboot-host@{0}.service"
HOST_REBOOT_SVC_FMT = "../${HOST_REBOOT_SVC}:${HOST_REBOOT_TGTFMT}.requires/${HOST_REBOOT_SVC_INST}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_REBOOT_SVC_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

# Force the host-start target to call the host-startmin target
HOST_STARTMIN_TMPL = "obmc-host-startmin@.target"
HOST_START_TGTFMT = "obmc-host-start@{0}.target"
HOST_STARTMIN_INSTFMT = "obmc-host-startmin@{0}.target"
HOST_START_FMT = "../${HOST_STARTMIN_TMPL}:${HOST_START_TGTFMT}.requires/${HOST_STARTMIN_INSTFMT}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_START_FMT', 'OBMC_HOST_INSTANCES')}"

# Force the host-start target to call the reboot count reset service
HOST_RST_RBT_ATTEMPTS_SVC = "phosphor-reset-host-reboot-attempts@.service"
HOST_RST_RBT_ATTEMPTS_SVC_INST = "phosphor-reset-host-reboot-attempts@{0}.service"
HOST_RST_RBT_ATTEMPTS_SVC_FMT = "../${HOST_RST_RBT_ATTEMPTS_SVC}:${HOST_START_TGTFMT}.requires/${HOST_RST_RBT_ATTEMPTS_SVC_INST}"
SYSTEMD_LINK_${PN}-host += "${@compose_list_zip(d, 'HOST_RST_RBT_ATTEMPTS_SVC_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_HOST_INSTANCES')}"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "32c532ea9bcddcab30f4fff30e6938211fdf584d"

S = "${WORKDIR}/git"
