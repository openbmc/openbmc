SUMMARY = "OpenPOWER OCC controller"
DESCRIPTION = "Application to control the OpenPOWER On-Chip-Controller"
HOMEPAGE = "https://github.com/openbmc/openpower-occ-control"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        python3native \
        phosphor-dbus-yaml

require ${BPN}.inc

SRC_URI += "file://occ-active.sh"
do_install:append() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/occ-active.sh \
            ${D}${bindir}/occ-active.sh
}

DBUS_SERVICE:${PN} += "org.open_power.OCC.Control.service"
SYSTEMD_SERVICE:${PN} += "op-occ-enable@.service"
SYSTEMD_SERVICE:${PN} += "op-occ-disable@.service"

DEPENDS += "virtual/${PN}-config-native"
DEPENDS += " \
        sdbusplus \
        ${PYTHON_PN}-sdbus++-native \
        phosphor-logging \
        phosphor-dbus-interfaces \
        autoconf-archive-native \
        systemd \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-setuptools-native \
        ${PYTHON_PN}-mako-native \
        "

RDEPENDS:${PN} += "phosphor-state-manager-obmc-targets"

EXTRA_OECONF = " \
             YAML_PATH=${STAGING_DATADIR_NATIVE}/${PN} \
             PS_DERATING_FACTOR=${POWER_SUPPLY_DERATING_FACTOR} \
             "
EXTRA_OECONF:append = "${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'i2c-occ', ' --enable-i2c-occ', '', d)}"

OCC_ENABLE = "enable"
OCC_DISABLE = "disable"
HOST_START = "startmin"
HOST_STOP = "stop"

# Ensure host-stop and host-startmin targets wants needed occ states
OCC_TMPL = "op-occ-{0}@.service"
HOST_TGTFMT = "obmc-host-{1}@{2}.target"
OCC_INSTFMT = "op-occ-{0}@{2}.service"
HOST_OCC_FMT = "../${OCC_TMPL}:${HOST_TGTFMT}.wants/${OCC_INSTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_OCC_FMT', 'OCC_ENABLE', 'HOST_START', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_OCC_FMT', 'OCC_DISABLE', 'HOST_STOP', 'OBMC_HOST_INSTANCES')}"

# Set the occ disable service to be executed on host error
HOST_ERROR_TARGETS = "crash timeout"

OCC_DISABLE_TMPL = "op-occ-disable@.service"
HOST_ERROR_TGTFMT = "obmc-host-{0}@{1}.target"
OCC_DISABLE_INSTFMT = "op-occ-disable@{1}.service"
HOST_ERROR_FMT = "../${OCC_DISABLE_TMPL}:${HOST_ERROR_TGTFMT}.wants/${OCC_DISABLE_INSTFMT}"

SYSTEMD_LINK:${PN} += "${@compose_list(d, 'HOST_ERROR_FMT', 'HOST_ERROR_TARGETS', 'OBMC_HOST_INSTANCES')}"

S = "${WORKDIR}/git"

# Remove packages not required for native build
DEPENDS:remove:class-native = " \
        phosphor-logging \
        systemd \
        sdbusplus \
        virtual/${PN}-config-native \
        "
RDEPENDS:${PN}:remove:class-native += "phosphor-state-manager-obmc-targets"

# Remove packages not required for native SDK build
DEPENDS:remove:class-nativesdk = " \
        phosphor-logging \
        systemd \
        sdbusplus \
        virtual/${PN}-config-native \
        "
RDEPENDS:${PN}:remove:class-nativesdk += "phosphor-state-manager-obmc-targets"

# Provide a means to enable/disable install_error_yaml feature
PACKAGECONFIG ??= "install_error_yaml"
PACKAGECONFIG[install_error_yaml] = "\
        --enable-install_error_yaml,\
        --disable-install_error_yaml,\
        ,\
        "

# Enable install_error_yaml during native and native SDK build
PACKAGECONFIG:add:class-native = "install_error_yaml"
PACKAGECONFIG:add:class-nativesdk = "install_error_yaml"

# Disable install_error_yaml during target build
PACKAGECONFIG:remove:class-target = "install_error_yaml"

BBCLASSEXTEND += "native nativesdk"
