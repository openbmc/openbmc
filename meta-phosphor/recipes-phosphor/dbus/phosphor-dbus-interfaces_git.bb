SUMMARY = "Phosphor DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the phosphor YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit pkgconfig meson
inherit obmc-phosphor-utils
inherit phosphor-dbus-yaml
inherit python3native

DEPENDS += " \
        ${PYTHON_PN}-sdbus++-native \
        sdbusplus \
        systemd \
        "

SRC_URI = "git://github.com/openbmc/phosphor-dbus-interfaces;branch=master;protocol=https"
SRCREV = "aaba8259c7f5866fdd43bd51c32dab5f702920ca"

# Process OBMC_ORG_YAML_SUBDIRS to create Meson config options.
# ex. xyz/openbmc_project -> -Ddata_xyz_openbmc_project=true
def pdi_meson_config(d):
    return ' '.join([
        '-Ddata_' + x.replace('/', '_') + '=true' \
                for x in listvar_to_list(d, 'OBMC_ORG_YAML_SUBDIRS')
        ])
pdi_meson_config[vardeps] = "OBMC_ORG_YAML_SUBDIRS"
EXTRA_OEMESON += "${@pdi_meson_config(d)}"

# Remove all schemas by default regardless of the meson_options.txt config
do_write_config:append() {
    for intf in $(grep "^option('data_" ${S}/meson_options.txt | sed "s,^.*\(data_[^']*\).*$,\1,"); do
        sed -i "/^\[built-in options\]\$/a$intf = false" ${WORKDIR}/meson.cross
    done
}

# Markdown files are installed into /usr/share/phosphor-dbus-interfaces so
# add them to the 'doc' subpackage.
FILES:${PN}-doc += "${datadir}/${BPN}"
