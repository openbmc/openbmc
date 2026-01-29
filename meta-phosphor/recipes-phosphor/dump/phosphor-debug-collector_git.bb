SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various log files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."
DEPENDS += " \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        ${PYTHON_PN}-sdbus++-native \
        autoconf-archive-native \
        virtual/phosphor-debug-errors \
        ${PYTHON_PN}-native \
        ${PYTHON_PN}-pyyaml-native \
        ${PYTHON_PN}-setuptools-native \
        ${PYTHON_PN}-mako-native \
        nlohmann-json \
"
PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', \
         'obmc-ubi-fs phosphor-mmc obmc-static-norootfs', '', 'jffs-workaround', d)}"
PACKAGECONFIG[jffs-workaround] = "-Djffs-workaround=enabled, \
        -Djffs-workaround=disabled"
PACKAGECONFIG[host-dump-transport-pldm] = " \
        -Dhost-transport=pldm,, \
        libpldm \
        "
PACKAGECONFIG[openpower-dumps-extension] = " \
       -Dopenpower-dumps-extension=enabled, \
       -Dopenpower-dumps-extension=disabled  \
"
PACKAGECONFIG ??= "xz"
PACKAGECONFIG[xz] = "-Ddump-compression-algorithm=xz,,,,gzip zstd"
PACKAGECONFIG[gzip] = "-Ddump-compression-algorithm=gzip,,,,xz zstd"
PACKAGECONFIG[zstd] = "-Ddump-compression-algorithm=zstd,,,zstd,xz gzip"

PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "file://coretemp.conf"

SYSTEMD_PACKAGES = "${PN}-monitor"
SYSTEMD_SUBSTITUTIONS += "BMC_DUMP_PATH:${bmc_dump_path}:${MGR_SVC}"
SYSTEMD_SERVICE:${PN}-monitor += "obmc-dump-monitor.service"
SYSTEMD_SERVICE:${PN}-monitor += "ramoops-monitor.service"
S = "${WORKDIR}/git"

inherit pkgconfig meson \
        obmc-phosphor-dbus-service \
        python3native \
        phosphor-debug-collector

EXTRA_OEMESON = " \
    -Dtests=disabled \
    -DBMC_DUMP_PATH=${bmc_dump_path} \
    -DERROR_MAP_YAML=${STAGING_DIR_NATIVE}/${datadir}/dump/errors_watch.yaml \
    "

do_install:append() {
    install -d ${D}${exec_prefix}/lib/tmpfiles.d
    install -m 644 ${UNPACKDIR}/coretemp.conf ${D}${exec_prefix}/lib/tmpfiles.d/
}
do_install[postfuncs] += "install_dreport"
do_install[postfuncs] += "install_dreport_conf_file"
do_install[postfuncs] += "install_dreport_plugins_scripts"
do_install[postfuncs] += "install_dreport_include_scripts"
do_install[postfuncs] += "install_dreport_user_scripts"

RDEPENDS:${PN}-manager += " \
        ${PN}-dreport \
"
RDEPENDS:${PN}-dreport += " \
        systemd \
        ${VIRTUAL-RUNTIME_base-utils} \
        bash \
        xz \
"
RDEPENDS:${PN}-scripts += " \
        bash \
"

FILES:${PN}-manager += " \
    ${libexecdir}/phosphor-debug-collector/phosphor-dump-manager \
    ${bindir}/phosphor-offload-handler \
    ${exec_prefix}/lib/tmpfiles.d/coretemp.conf \
    ${datadir}/dump/ \
    "
FILES:${PN}-monitor += "${libexecdir}/phosphor-debug-collector/phosphor-dump-monitor"
FILES:${PN}-monitor += "${libexecdir}/phosphor-debug-collector/phosphor-ramoops-monitor"
FILES:${PN}-dreport += "${bindir}/dreport"
FILES:${PN}-scripts += "${dreport_dir}"

require phosphor-debug-collector.inc

ALLOW_EMPTY:${PN} = "1"

DEBUG_COLLECTOR_PKGS = " \
    ${PN}-manager \
    ${PN}-monitor \
    ${PN}-dreport \
    ${PN}-scripts \
"
PACKAGE_BEFORE_PN += "${DEBUG_COLLECTOR_PKGS}"
DBUS_PACKAGES = "${PN}-manager"
MGR_SVC ?= "xyz.openbmc_project.Dump.Manager.service"
DBUS_SERVICE:${PN}-manager += "${MGR_SVC}"
# Install dreport script
# From tools/dreport.d/dreport to /usr/bin/dreport
install_dreport() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/tools/dreport.d/dreport \
                    ${D}${bindir}/dreport
}
# Install dreport sample configuration file
# From tools/dreport.d/sample.conf
# to /usr/share/dreport.d/conf.d/dreport.conf
install_dreport_conf_file() {
    install -d ${D}${dreport_conf_dir}
    install -m 0644 ${S}/tools/dreport.d/sample.conf \
                        ${D}${dreport_conf_dir}/dreport.conf
}
# Install dreport plugins
# From tools/dreport.d/plugins.d to /usr/share/dreport.d/plugins.d
install_dreport_plugins_scripts() {
    install -d ${D}${dreport_plugin_dir}
    install -m 0755 ${S}/tools/dreport.d/plugins.d/* ${D}${dreport_plugin_dir}/
}
# Install dreport utility functions
# From tools/dreport.d/include.d to /usr/share/dreport.d/include.d
install_dreport_include_scripts() {
    install -d ${D}${dreport_include_dir}
    install -m 0755 ${S}/tools/dreport.d/include.d/* \
                ${D}${dreport_include_dir}/
}

#Make the links for all the plugins
python install_dreport_user_scripts() {
    source = d.getVar('S', True)
    source_path = os.path.join(source, "tools", "dreport.d", "plugins.d")
    scripts = os.listdir(source_path)
    for script in scripts:
        srcname = os.path.join(source_path, script)
        install_dreport_user_script("dreport.conf", srcname, d)
}
