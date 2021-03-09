SUMMARY = "Phosphor OpenBMC event and error logging"
DESCRIPTION = "An error and event log daemon application, and \
               supporting tools for OpenBMC."
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit python3native
inherit obmc-phosphor-dbus-service
inherit phosphor-logging
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "virtual/phosphor-logging-callouts"
DEPENDS += "libcereal"
DEPENDS += "sdeventplus"
DEPENDS_append_class-target = " packagegroup-obmc-yaml-providers"

PACKAGE_BEFORE_PN = "${PN}-test"
FILES_${PN}-test = "${bindir}/*-test"

PACKAGE_BEFORE_PN += "${PN}-elog"
FILES_${PN}-elog += "${elog_dir}"

# Package configuration
LOGGING_PACKAGES = " \
        ${PN}-base \
        phosphor-rsyslog-config \
"

ALLOW_EMPTY_${PN} = "1"
PACKAGE_BEFORE_PN += "${LOGGING_PACKAGES}"
SYSTEMD_PACKAGES = "${LOGGING_PACKAGES}"
DBUS_PACKAGES = "${LOGGING_PACKAGES}"

FILES_${PN}-base += " \
        ${bindir}/phosphor-log-manager \
        ${libdir}/libphosphor_logging.so.* \
"
DBUS_SERVICE_${PN}-base += "xyz.openbmc_project.Logging.service"

DBUS_SERVICE_phosphor-rsyslog-config += "xyz.openbmc_project.Syslog.Config.service"
FILES_phosphor-rsyslog-config += " \
        ${bindir}/phosphor-rsyslog-conf \
"

SRC_URI += "git://github.com/openbmc/phosphor-logging"
SRCREV = "25d986c1e7d749d3f78da5a5e507d1f2bcbc7874"

S = "${WORKDIR}/git"

# Do not DEPEND on the specified packages for native build
# as they will not be available in host machine
DEPENDS_remove_class-native = " \
        virtual/phosphor-logging-callouts \
        sdbusplus \
        systemd \
        libcereal \
        sdeventplus \
        "

# Do not DEPEND on the specified packages for native SDK build
# as they will not be available in host machine
DEPENDS_remove_class-nativesdk = " \
        virtual/phosphor-logging-callouts \
        sdbusplus \
        libcereal \
        systemd \
        phosphor-dbus-interfaces \
        sdeventplus \
        "

PACKAGECONFIG ??= "metadata-processing install_scripts"

PACKAGECONFIG[metadata-processing] = " \
        --enable-metadata-processing, \
        --disable-metadata-processing, , \
        "

# Provide a means to enable/disable install_scripts feature
PACKAGECONFIG[install_scripts] = " \
        --enable-install_scripts, \
        --disable-install_scripts, ,\
        "

PACKAGECONFIG[openpower-pels] = " \
        --enable-openpower-pel-extension, \
        --disable-openpower-pel-extension, \
        nlohmann-json nlohmann-fifo cli11 pldm, \
        python3, \
        "

# Enable install_scripts during native and native SDK build
PACKAGECONFIG_add_class-native = "install_scripts"
PACKAGECONFIG_add_class-nativesdk = "install_scripts"

# Disable install_scripts during target build
PACKAGECONFIG_remove_class-target = "install_scripts"

EXTRA_OECONF = " \
        YAML_DIR=${STAGING_DIR_TARGET}${yaml_dir} \
        CALLOUTS_YAML=${STAGING_DIR_NATIVE}${callouts_datadir}/callouts.yaml \
        "

BBCLASSEXTEND += "native nativesdk"
