SUMMARY = "Phosphor Host logger"
DESCRIPTION = "Save log messages from host's console to the persistent storage."
HOMEPAGE = "https://github.com/openbmc/phosphor-hostlogger"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit pkgconfig
inherit pythonnative
inherit systemd

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

# Dependencies
DEPENDS += "sdbusplus \
            sdbusplus-native \
            phosphor-dbus-interfaces"
RDEPENDS_${PN} += "obmc-console"
RRECOMMENDS_${PN} += "phosphor-debug-collector"

# systemd service setup
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "hostlogger.service"
SYSTEMD_DEFAULT_TARGET ?= "multi-user.target"

# Host TTY setup
OBMC_CONSOLE_HOST_TTY ?= "ttyVUART0"

# Extra parameters for 'configure' script
EXTRA_OECONF = "HOST_TTY=${OBMC_CONSOLE_HOST_TTY} \
                SYSTEMD_TARGET=${SYSTEMD_DEFAULT_TARGET}"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-hostlogger"
SRCREV = "4d5a5dcd6f974166c8d0f2657e0079a7f3fc5e01"
