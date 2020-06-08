SUMMARY = "Phosphor Host logger"
DESCRIPTION = "Save log messages from host's console to the persistent storage."
HOMEPAGE = "https://github.com/openbmc/phosphor-hostlogger"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit pkgconfig
inherit python3native
inherit systemd

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

# Dependencies
DEPENDS += "\
            autoconf-archive-native \
            sdbusplus \
            ${PYTHON_PN}-sdbus++-native \
            phosphor-dbus-interfaces \
           "
RDEPENDS_${PN} += "obmc-console"
RRECOMMENDS_${PN} += "phosphor-debug-collector"

# systemd service setup
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "hostlogger.service"

# Host TTY setup
OBMC_CONSOLE_HOST_TTY ?= "ttyVUART0"

# Extra parameters for 'configure' script
EXTRA_OECONF = "HOST_TTY=${OBMC_CONSOLE_HOST_TTY} \
                SYSTEMD_TARGET=multi-user.target"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-hostlogger"
SRCREV = "ea31658b6df1c51b28ed28e3e459a64fb8d13da8"
