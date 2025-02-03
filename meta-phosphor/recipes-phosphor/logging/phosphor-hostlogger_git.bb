SUMMARY = "Phosphor Host logger"
DESCRIPTION = "Save log messages from host's console to the persistent storage."
HOMEPAGE = "https://github.com/openbmc/phosphor-hostlogger"
# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
# Dependencies
DEPENDS += " \
            phosphor-logging \
            zlib \
           "
SRCREV = "e0bb03b042373c43db2eee0ca8c65a74a3b68cef"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-hostlogger;branch=master;protocol=https"

# Source code repository
S = "${WORKDIR}/git"
# Systemd service template
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "hostlogger@.service"
# Preset systemd units
SYSTEMD_SERVICE:${PN} += "${@'${CUSTOM_SERVICES}' if len('${CUSTOM_SERVICES}') \
                                                  else '${DEFAULT_SERVICE}'}"

inherit pkgconfig meson
inherit systemd

# Disable unit tests
EXTRA_OEMESON:append = " -Dtests=disabled"

RDEPENDS:${PN} += "obmc-console"

RRECOMMENDS:${PN} += "phosphor-debug-collector"

# Default service instance to install (single-host mode)
DEFAULT_INSTANCE = "ttyVUART0"
DEFAULT_SERVICE = "hostlogger@${DEFAULT_INSTANCE}.service"
# Multi-host mode setup - list of configuration files to install, can be added
# via SRC_URI in a bbappend. The file name is the name of the service instance,
# which should match the corresponding instance of the obmc-console service.
CUSTOM_CONFIGS = "${@custom_configs('${UNPACKDIR}')}"
CUSTOM_SERVICES = "${@custom_services('${CUSTOM_CONFIGS}')}"
# Gets list of custom config files in a directory
def custom_configs(workdir):
    if os.path.exists(workdir):
        return ' '.join([f for f in os.listdir(workdir) if f.endswith('.conf')])
# Get list of custom service instances
def custom_services(configs):
    return ' '.join(['hostlogger@' + i.replace('.conf', '.service') \
                     for i in configs.split()])
do_install:append() {
  # Install config files
  if [ -n "${CUSTOM_CONFIGS}" ]; then
    for CONFIG_FILE in ${CUSTOM_CONFIGS}; do
        install -Dm 0644 ${UNPACKDIR}/${CONFIG_FILE} \
                     ${D}${sysconfdir}/hostlogger/${CONFIG_FILE}
    done
  else
    install -Dm 0644 ${S}/default.conf \
                     ${D}${sysconfdir}/hostlogger/${DEFAULT_INSTANCE}.conf
  fi
}
