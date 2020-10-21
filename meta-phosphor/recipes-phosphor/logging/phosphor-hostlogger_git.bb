SUMMARY = "Phosphor Host logger"
DESCRIPTION = "Save log messages from host's console to the persistent storage."
HOMEPAGE = "https://github.com/openbmc/phosphor-hostlogger"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson
inherit systemd

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

# Dependencies
DEPENDS += " \
            phosphor-logging \
            zlib \
           "
RDEPENDS_${PN} += "obmc-console"
RRECOMMENDS_${PN} += "phosphor-debug-collector"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-hostlogger"
SRCREV = "e9af83c6f5cc14a7493de5359b3c65b8832c99f0"

# Systemd service template
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "hostlogger@.service"

# Default service instance to install (single-host mode)
DEFAULT_INSTANCE = "ttyVUART0"
DEFAULT_SERVICE = "hostlogger@${DEFAULT_INSTANCE}.service"

# Multi-host mode setup - list of configuration files to install, can be added
# via SRC_URI in a bbappend. The file name is the name of the service instance,
# which should match the corresponding instance of the obmc-console service.
CUSTOM_CONFIGS = "${@custom_configs('${WORKDIR}')}"
CUSTOM_SERVICES = "${@custom_services('${CUSTOM_CONFIGS}')}"

# Preset systemd units
SYSTEMD_SERVICE_${PN} += "${@'${CUSTOM_SERVICES}' if len('${CUSTOM_SERVICES}') \
                                                  else '${DEFAULT_SERVICE}'}"

# Gets list of custom config files in a directory
def custom_configs(workdir):
    if os.path.exists(workdir):
        return ' '.join([f for f in os.listdir(workdir) if f.endswith('.conf')])

# Get list of custom service instances
def custom_services(configs):
    return ' '.join(['hostlogger@' + i.replace('.conf', '.service') \
                     for i in configs.split()])

do_install_append() {
  # Install config files
  if [ -n "${CUSTOM_CONFIGS}" ]; then
    for CONFIG_FILE in ${CUSTOM_CONFIGS}; do
        install -Dm 0644 ${WORKDIR}/${CONFIG_FILE} \
                     ${D}${sysconfdir}/hostlogger/${CONFIG_FILE}
    done
  else
    install -Dm 0644 ${S}/default.conf \
                     ${D}${sysconfdir}/hostlogger/${DEFAULT_INSTANCE}.conf
  fi
}
