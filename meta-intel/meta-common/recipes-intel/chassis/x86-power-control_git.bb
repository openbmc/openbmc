SUMMARY = "Power Control service for Intel based platform"
DESCRIPTION = "Power Control service for Intel based platfrom"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/x86-power-control.git"
SRCREV = "80f6d927c220be0e638a0674a986429825a070aa"

inherit cmake pkgconfig  pythonnative systemd

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Chassis.Control.Power@.service"

# Force the standby target to run these services
SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"

POWER_TMPL_CTRL = "xyz.openbmc_project.Chassis.Control.Power@.service"
#SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"
POWER_INSTFMT_CTRL = "xyz.openbmc_project.Chassis.Control.Power@{0}.service"
POWER_FMT_CTRL = "../${POWER_TMPL_CTRL}:${SYSD_TGT}.wants/${POWER_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'POWER_FMT_CTRL', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE_${PN} += " \
        obmc-host-start@.target \
        obmc-host-startmin@.target \
        obmc-host-stop@.target \
        obmc-host-reboot@.target \
        obmc-chassis-poweroff@.target \
        obmc-chassis-poweron@.target \
        obmc-chassis-hard-poweroff@.target \
        obmc-host-soft-reboot@.target \
        obmc-host-warm-reset@.target \
        obmc-chassis-powerreset@.target \
        "

RESET_TGTFMT = "obmc-chassis-powerreset@{0}.target"

RESET_ON_TMPL = "op-reset-chassis-running@.service"
RESET_ON_INSTFMT = "op-reset-chassis-running@{0}.service"
RESET_ON_FMT = "../${RESET_ON_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${RESET_ON_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_FMT', 'OBMC_CHASSIS_INSTANCES')}"

RESET_ON_CHASSIS_TMPL = "op-reset-chassis-on@.service"
RESET_ON_CHASSIS_INSTFMT = "op-reset-chassis-on@{0}.service"
RESET_ON_CHASSIS_FMT = "../${RESET_ON_CHASSIS_TMPL}:${RESET_TGTFMT}.requires/${RESET_ON_CHASSIS_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${RESET_ON_CHASSIS_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_ON_CHASSIS_FMT', 'OBMC_CHASSIS_INSTANCES')}"

# Force the standby target to run the chassis reset check target
RESET_TMPL_CTRL = "obmc-chassis-powerreset@.target"
SYSD_TGT = "${SYSTEMD_DEFAULT_TARGET}"
RESET_INSTFMT_CTRL = "obmc-chassis-powerreset@{0}.target"
RESET_FMT_CTRL = "../${RESET_TMPL_CTRL}:${SYSD_TGT}.wants/${RESET_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'RESET_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

START_TMPL = "intel-power-start@.service"
START_TGTFMT = "obmc-chassis-poweron@{0}.target"
START_INSTFMT = "intel-power-start@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${START_TMPL}"

STOP_TMPL = "intel-power-stop@.service"
STOP_TGTFMT = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT = "intel-power-stop@{0}.service"
STOP_FMT = "../${STOP_TMPL}:${STOP_TGTFMT}.requires/${STOP_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${STOP_TMPL}"

WARM_RESET_TMPL = "intel-power-warm-reset@.service"
WARM_RESET_TGTFMT = "obmc-host-warm-reset@{0}.target"
WARM_RESET_INSTFMT = "intel-power-warm-reset@{0}.service"
WARM_RESET_FMT = "../${WARM_RESET_TMPL}:${WARM_RESET_TGTFMT}.requires/${WARM_RESET_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${WARM_RESET_TMPL}"

# Build up requires relationship for START_TGTFMT and STOP_TGTFMT
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'START_FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'STOP_FMT',  'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'WARM_RESET_FMT',  'OBMC_CHASSIS_INSTANCES')}"

#The main control target requires these power targets
START_TMPL_CTRL = "obmc-chassis-poweron@.target"
START_TGTFMT_CTRL = "obmc-host-startmin@{0}.target"
START_INSTFMT_CTRL = "obmc-chassis-poweron@{0}.target"
START_FMT_CTRL = "../${START_TMPL_CTRL}:${START_TGTFMT_CTRL}.requires/${START_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'START_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Chassis off requires host off
STOP_TMPL_CTRL = "obmc-host-stop@.target"
STOP_TGTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
STOP_INSTFMT_CTRL = "obmc-host-stop@{0}.target"
STOP_FMT_CTRL = "../${STOP_TMPL_CTRL}:${STOP_TGTFMT_CTRL}.requires/${STOP_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'STOP_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Hard power off requires chassis off
HARD_OFF_TMPL_CTRL = "obmc-chassis-poweroff@.target"
HARD_OFF_TGTFMT_CTRL = "obmc-chassis-hard-poweroff@{0}.target"
HARD_OFF_INSTFMT_CTRL = "obmc-chassis-poweroff@{0}.target"
HARD_OFF_FMT_CTRL = "../${HARD_OFF_TMPL_CTRL}:${HARD_OFF_TGTFMT_CTRL}.requires/${HARD_OFF_INSTFMT_CTRL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HARD_OFF_FMT_CTRL', 'OBMC_CHASSIS_INSTANCES')}"

# Host soft reboot to run the shutdown target
HOST_SHUTDOWN_TMPL = "obmc-host-shutdown@.target"
HOST_SOFT_REBOOT_TMPL = "obmc-host-soft-reboot@.target"
HOST_SOFT_REBOOT_TGTFMT = "obmc-host-soft-reboot@{0}.target"
HOST_SHUTDOWN_INSTFMT = "obmc-host-shutdown@{0}.target"
HOST_SOFT_REBOOT_FMT = "../${HOST_SHUTDOWN_TMPL}:${HOST_SOFT_REBOOT_TGTFMT}.requires/${HOST_SHUTDOWN_INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_SOFT_REBOOT_FMT', 'OBMC_HOST_INSTANCES')}"
# And also to call the host startmin service
HOST_SOFT_REBOOT_SVC = "phosphor-reboot-host@.service"
HOST_SOFT_REBOOT_SVC_INST = "phosphor-reboot-host@{0}.service"
HOST_SOFT_REBOOT_SVC_FMT = "../${HOST_SOFT_REBOOT_SVC}:${HOST_SOFT_REBOOT_TGTFMT}.requires/${HOST_SOFT_REBOOT_SVC_INST}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_SOFT_REBOOT_SVC_FMT', 'OBMC_HOST_INSTANCES')}"

#Broadcast Host state
PRE_HOST_START_TMPL = "obmc-send-signal-pre-host-start@.service"
PRE_HOST_START_TGTFMT = "obmc-host-start-pre@{0}.target"
PRE_HOST_START_INSTFMT = "obmc-send-signal-pre-host-start@{0}.service"
PRE_HOST_START_FMT = "../${PRE_HOST_START_TMPL}:${PRE_HOST_START_TGTFMT}.requires/${PRE_HOST_START_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${PRE_HOST_START_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'PRE_HOST_START_FMT', 'OBMC_HOST_INSTANCES')}"

POST_HOST_START_TMPL = "obmc-send-signal-post-host-start@.service"
POST_HOST_START_TGTFMT = "obmc-host-started@{0}.target"
POST_HOST_START_INSTFMT = "obmc-send-signal-post-host-start@{0}.service"
POST_HOST_START_FMT = "../${POST_HOST_START_TMPL}:${POST_HOST_START_TGTFMT}.requires/${POST_HOST_START_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${POST_HOST_START_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'POST_HOST_START_FMT', 'OBMC_HOST_INSTANCES')}"

HOST_STARTING_TMPL = "obmc-send-signal-host-starting@.service"
HOST_STARTING_TGTFMT = "obmc-host-starting@{0}.target"
HOST_STARTING_INSTFMT = "obmc-send-signal-host-starting@{0}.service"
HOST_STARTING_FMT = "../${HOST_STARTING_TMPL}:${HOST_STARTING_TGTFMT}.requires/${HOST_STARTING_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${HOST_STARTING_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_STARTING_FMT', 'OBMC_HOST_INSTANCES')}"

PRE_HOST_STOP_TMPL = "obmc-send-signal-pre-host-stop@.service"
PRE_HOST_STOP_TGTFMT = "obmc-host-stop-pre@{0}.target"
PRE_HOST_STOP_INSTFMT = "obmc-send-signal-pre-host-stop@{0}.service"
PRE_HOST_STOP_FMT = "../${PRE_HOST_STOP_TMPL}:${PRE_HOST_STOP_TGTFMT}.requires/${PRE_HOST_STOP_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${PRE_HOST_STOP_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'PRE_HOST_STOP_FMT', 'OBMC_HOST_INSTANCES')}"

POST_HOST_STOP_TMPL = "obmc-send-signal-post-host-stop@.service"
POST_HOST_STOP_TGTFMT = "obmc-host-stopped@{0}.target"
POST_HOST_STOP_INSTFMT = "obmc-send-signal-post-host-stop@{0}.service"
POST_HOST_STOP_FMT = "../${POST_HOST_STOP_TMPL}:${POST_HOST_STOP_TGTFMT}.requires/${POST_HOST_STOP_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${POST_HOST_STOP_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'POST_HOST_STOP_FMT', 'OBMC_HOST_INSTANCES')}"

HOST_STOPPING_TMPL = "obmc-send-signal-host-stopping@.service"
HOST_STOPPING_TGTFMT = "obmc-host-stopping@{0}.target"
HOST_STOPPING_INSTFMT = "obmc-send-signal-host-stopping@{0}.service"
HOST_STOPPING_FMT = "../${HOST_STOPPING_TMPL}:${HOST_STOPPING_TGTFMT}.requires/${HOST_STOPPING_INSTFMT}"
SYSTEMD_SERVICE_${PN} += "${HOST_STOPPING_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_STOPPING_FMT', 'OBMC_HOST_INSTANCES')}"

DEPENDS += " \
    autoconf-archive-native \
    boost \
    systemd \
    sdbusplus \
    sdbusplus-native \
    phosphor-dbus-interfaces \
    phosphor-dbus-interfaces-native \
    phosphor-logging \
    "

EXTRA_OECMAKE = " -DENABLE_GTEST=OFF "
