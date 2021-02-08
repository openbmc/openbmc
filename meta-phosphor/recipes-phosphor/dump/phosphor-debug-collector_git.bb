SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various log files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"
PV = "1.0+git${SRCPV}"

DEBUG_COLLECTOR_PKGS = " \
    ${PN}-manager \
    ${PN}-monitor \
    ${PN}-dreport \
    ${PN}-scripts \
"
PACKAGE_BEFORE_PN += "${DEBUG_COLLECTOR_PKGS}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${PN}-manager"

SYSTEMD_PACKAGES = "${PN}-monitor"

inherit meson \
        obmc-phosphor-dbus-service \
        python3native \
        phosphor-debug-collector

require phosphor-debug-collector.inc

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
"

RDEPENDS_${PN}-manager += " \
        ${PN}-dreport \
"
RDEPENDS_${PN}-dreport += " \
        systemd \
        ${VIRTUAL-RUNTIME_base-utils} \
        bash \
        xz \
"
RDEPENDS_${PN}-scripts += " \
        bash \
"

MGR_SVC ?= "xyz.openbmc_project.Dump.Manager.service"

SYSTEMD_SUBSTITUTIONS += "BMC_DUMP_PATH:${bmc_dump_path}:${MGR_SVC}"

FILES_${PN}-manager +=  " \
    ${bindir}/phosphor-dump-manager \
    ${exec_prefix}/lib/tmpfiles.d/coretemp.conf \
    ${datadir}/dump/ \
    "
FILES_${PN}-monitor += "${bindir}/phosphor-dump-monitor"
FILES_${PN}-dreport += "${bindir}/dreport"
FILES_${PN}-scripts += "${dreport_dir}"

DBUS_SERVICE_${PN}-manager += "${MGR_SVC}"
SYSTEMD_SERVICE_${PN}-monitor += "obmc-dump-monitor.service"

EXTRA_OEMESON = " \
    -DBMC_DUMP_PATH=${bmc_dump_path} \
    -DERROR_MAP_YAML=${STAGING_DIR_NATIVE}/${datadir}/dump/errors_watch.yaml \
    "

S = "${WORKDIR}/git"
SRC_URI += "file://coretemp.conf"

do_install_append() {
    install -d ${D}${exec_prefix}/lib/tmpfiles.d
    install -m 644 ${WORKDIR}/coretemp.conf ${D}${exec_prefix}/lib/tmpfiles.d/
}

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

# Make the links for a single user plugin script
# Create user directories based on the dump type value in the config section
# Create softlinks for the base scripts in the user directories
def install_dreport_user_script(script_path, d):
    import re
    import configparser

    #Read the user types from the dreport.conf file
    configure = configparser.ConfigParser()
    conf_dir  = d.getVar('D', True) + d.getVar('dreport_conf_dir', True)
    confsource = os.path.join(conf_dir, "dreport.conf")
    configure.read(confsource)

    config = ("config:")
    section = "DumpType"
    dreport_dir = d.getVar('D', True) + d.getVar('dreport_dir', True)

    script = os.path.basename(script_path)
    srclink = os.path.join(d.getVar('dreport_plugin_dir', True), script)

    file = open(script_path, "r")

    for line in file:
        if not config in line:
            continue
        revalue = re.search('[0-9]+.[0-9]+', line)
        if not revalue:
            bb.warn("Invalid format for config value =%s" % line)
            continue
        parse_value = revalue.group(0)
        config_values = re.split('\W+', parse_value, 1)
        if(len(config_values) != 2):
            bb.warn("Invalid config value=%s" % parse_value)
            break;
        priority = config_values[1]
        types = [int(d) for d in str(config_values[0])]
        for type in types:
            if not configure.has_option(section, str(type)):
                bb.warn("Invalid dump type id =%s" % (str(type)))
                continue
            typestr = configure.get(section, str(type))
            destdir = os.path.join(dreport_dir, ("pl_" + typestr + ".d"))
            if not os.path.exists(destdir):
                os.makedirs(destdir)
            linkname = "E" + priority + script
            destlink = os.path.join(destdir, linkname)
            os.symlink(srclink, destlink)

#Make the links for all the plugins
python install_dreport_user_scripts() {

    source = d.getVar('S', True)
    source_path = os.path.join(source, "tools", "dreport.d", "plugins.d")
    scripts = os.listdir(source_path)

    for script in scripts:
        srcname = os.path.join(source_path, script)
        install_dreport_user_script(srcname, d)
}

PACKAGECONFIG ??= "${@bb.utils.contains_any('DISTRO_FEATURES', \
         'obmc-ubi-fs phosphor-mmc', '', 'jffs-workaround', d)}"
PACKAGECONFIG[jffs-workaround] = "-Djffs-workaround=enabled, \
        -Djffs-workaround=disabled"

PACKAGECONFIG[host-dump-transport-pldm] = " \
        -Dhost-transport=pldm,, \
        pldm \
        "

PACKAGECONFIG[openpower-dumps-extension] = " \
       -Dopenpower-dumps-extension=enabled, \
       -Dopenpower-dumps-extension=disabled  \
"

do_install[postfuncs] += "install_dreport"
do_install[postfuncs] += "install_dreport_conf_file"
do_install[postfuncs] += "install_dreport_plugins_scripts"
do_install[postfuncs] += "install_dreport_include_scripts"
do_install[postfuncs] += "install_dreport_user_scripts"
