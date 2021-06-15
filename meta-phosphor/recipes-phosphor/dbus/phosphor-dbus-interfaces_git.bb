SUMMARY = "Phosphor DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the phosphor YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson
inherit obmc-phosphor-utils
inherit phosphor-dbus-yaml
inherit python3native

DEPENDS += " \
        ${PYTHON_PN}-sdbus++-native \
        sdbusplus \
        systemd \
        "

SRC_URI = "git://github.com/openbmc/phosphor-dbus-interfaces"
SRCREV = "08baf48ad5f15774d393fbbf4e9479a0ef3e82d0"

# Process OBMC_ORG_YAML_SUBDIRS to create Meson config options.
# ex. xyz/openbmc_project -> -Ddata_xyz_openbmc_project=true
def pdi_meson_config(d):
    return ' '.join([
        '-Ddata_' + x.replace('/', '_') + '=true' \
                for x in listvar_to_list(d, 'OBMC_ORG_YAML_SUBDIRS')
        ])
pdi_meson_config[vardeps] = "OBMC_ORG_YAML_SUBDIRS"

# Markdown files are installed into /usr/share/phosphor-dbus-interfaces so
# add them to the 'doc' subpackage.
FILES_${PN}-doc += "${datadir}/${BPN}"

EXTRA_OEMESON_append = " \
        -Db_lto=true \
        ${@pdi_meson_config(d)}"
