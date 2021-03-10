SUMMARY = "Phosphor post code manager"
DESCRIPTION = "Phosphor post Code Manager monitors post code posted on dbus \
interface /xyz/openbmc_project/state/boot/raw by snoopd daemon and save them \
in a file under /var/lib for history."

SRC_URI = "git://github.com/openbmc/phosphor-post-code-manager.git"
SRCREV = "84a4c19c48ad28b537cfcce15df39e841e1ed565"

S = "${WORKDIR}/git"

PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit cmake pkgconfig systemd


def get_service(d):
    if(d.getVar('OBMC_HOST_INSTANCES') == '0'):
      return "xyz.openbmc_project.State.Boot.PostCode.service"
    else:
      return " ".join(["xyz.openbmc_project.State.Boot.PostCode@{}.service".format(x) for x in d.getVar('OBMC_HOST_INSTANCES').split()])

SYSTEMD_SERVICE_${PN} = "${@get_service(d)}"

DEPENDS += " \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    "

FILES_${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.State.Boot.PostCode@.service"
FILES_${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.State.Boot.PostCode.service"
