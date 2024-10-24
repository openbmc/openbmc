SUMMARY = "Remote BIOS Configuration via BMC"
DESCRIPTION = "Provides ability for the user to view and modify the BIOS setup \
               configuration parameters remotely via BMC at any Host state. \
               Modifications to the parameters take place upon the next system \
               reboot or immediate based on the host firmware."
HOMEPAGE = "https://github.com/openbmc/bios-settings-mgr"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcd9ada3a943f58551867d72893cc9ab"
DEPENDS = " boost \
            libcereal \
            nlohmann-json \
            openssl \
            phosphor-dbus-interfaces \
            phosphor-logging \
            sdbusplus \
            systemd "
SRCREV = "4b1e496addb232ff89a57c1937e5e8b6046a2ae9"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/bios-settings-mgr;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.biosconfig_manager.service"

inherit meson pkgconfig systemd
