HOMEPAGE = "https://github.com/openbmc/bios-settings-mgr"

SUMMARY = "Remote BIOS Configuration via BMC"

DESCRIPTION = "Provides ability for the user to view and modify the BIOS setup \
               configuration parameters remotely via BMC at any Host state. \
               Modifications to the parameters take place upon the next system \
               reboot or immediate based on the host firmware."

PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcd9ada3a943f58551867d72893cc9ab"

SRC_URI = "git://github.com/openbmc/bios-settings-mgr"
SRCREV = "c0f926d6cbf9636a42f4bc3d33b9602e4633c478"

inherit meson pkgconfig systemd

S = "${WORKDIR}/git"
SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.biosconfig_manager.service \
                         xyz.openbmc_project.biosconfig_password.service"

DEPENDS = " boost \
            phosphor-dbus-interfaces \
            phosphor-logging \
            sdbusplus \
            systemd \
            nlohmann-json "

