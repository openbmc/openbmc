SUMMARY = "Phosphor OpenBMC event and error logging"
DESCRIPTION = "An error and event log daemon application, and \
               supporting tools for OpenBMC."
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
PR = "r1"

inherit autotools pkgconfig
inherit pythonnative
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service
inherit phosphor-logging
inherit phosphor-dbus-yaml

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Logging.service"

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "python-mako-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"
DEPENDS += "virtual/phosphor-logging-callouts"
DEPENDS += "phosphor-logging-error-logs-native"
DEPENDS += "phosphor-logging-native"
DEPENDS += "cereal"
RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

PACKAGE_BEFORE_PN = "${PN}-test"
FILES_${PN}-test = "${bindir}/*-test"

PACKAGE_BEFORE_PN += "${PN}-elog"
FILES_${PN}-elog += "${elog_dir}"

SRC_URI += "git://github.com/openbmc/phosphor-logging"
SRCREV = "477b731ad0fd8c116ffcaa8265a508c9fb112479"

S = "${WORKDIR}/git"

# Do not DEPEND on the specified packages for native build
# as they will not be available in host machine
DEPENDS_remove_class-native = " \
        virtual/phosphor-logging-callouts \
        sdbus++ \
        systemd \
        cereal \
        "

# Do not DEPEND on the specified packages for native SDK build
# as they will not be available in host machine
DEPENDS_remove_class-nativesdk = " \
        virtual/phosphor-logging-callouts \
        sdbus++-native \
        cereal \
        systemd \
        phosphor-dbus-interfaces \
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

# Enable install_scripts during native and native SDK build
PACKAGECONFIG_add_class-native = "install_scripts"
PACKAGECONFIG_add_class-nativesdk = "install_scripts"

# Disable install_scripts during target build
PACKAGECONFIG_remove_class-target = "install_scripts"

EXTRA_OECONF = " \
        YAML_DIR=${STAGING_DIR_NATIVE}${yaml_dir} \
        CALLOUTS_YAML=${STAGING_DIR_NATIVE}${callouts_datadir}/callouts.yaml \
        "

BBCLASSEXTEND += "native nativesdk"
