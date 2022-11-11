SUMMARY = "Phosphor post code manager"
DESCRIPTION = "Phosphor post Code Manager monitors post code posted on dbus \
interface /xyz/openbmc_project/state/boot/raw by snoopd daemon and save them \
in a file under /var/lib for history."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "c1819379d12c286c8f17175f76e970d14c73d480"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/phosphor-post-code-manager.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd

def get_service(d):
    service_list = "xyz.openbmc_project.State.Boot.PostCode.service xyz.openbmc_project.State.Boot.PostCode@.service "
    if(d.getVar('OBMC_HOST_INSTANCES') == '0'):
      return service_list
    else:
      return service_list+" ".join(["xyz.openbmc_project.State.Boot.PostCode@{}.service".format(x) for x in d.getVar('OBMC_HOST_INSTANCES').split()])
SYSTEMD_SERVICE:${PN} = "${@get_service(d)}"
DEPENDS += " \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    libcereal \
    "
