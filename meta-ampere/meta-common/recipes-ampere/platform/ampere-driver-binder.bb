SUMMARY = "Ampere Driver Binder Implementation"
DESCRIPTION = "The driver binder for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "bash"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = " \
           file://ampere-power-on-driver-binder@.service \
           file://ampere-host-on-driver-binder@.service \
           file://ampere-bmc-ready-driver-binder.service \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
                         ampere-power-on-driver-binder@.service \
                         ampere-host-on-driver-binder@.service \
                         ampere-bmc-ready-driver-binder.service \
                        "
# bind driver after the power is on
POWER_ON_DRIVER_BINDER_TGT = "ampere-power-on-driver-binder@.service"
POWER_ON_DRIVER_BINDER_INSTMPL = "ampere-power-on-driver-binder@{0}.service"
AMPER_POWER_ON = "obmc-power-already-on@{0}.target"
POWER_ON_DRIVER_BINDER_TARGET_FMT = "../${POWER_ON_DRIVER_BINDER_TGT}:${AMPER_POWER_ON}.wants/${POWER_ON_DRIVER_BINDER_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'POWER_ON_DRIVER_BINDER_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"

# bind driver after the host is on
HOST_ON_DRIVER_BINDER_TGT = "ampere-host-on-driver-binder@.service"
HOST_ON_DRIVER_BINDER_INSTMPL = "ampere-host-on-driver-binder@{0}.service"
AMPER_HOST_RUNNING = "obmc-host-already-on@{0}.target"
HOST_ON_DRIVER_BINDER_TARGET_FMT = "../${HOST_ON_DRIVER_BINDER_TGT}:${AMPER_HOST_RUNNING}.wants/${HOST_ON_DRIVER_BINDER_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_ON_DRIVER_BINDER_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"
