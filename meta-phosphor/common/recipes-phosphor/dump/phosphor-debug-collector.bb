SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various log files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"

DEBUG_COLLECTOR_PKGS = " \
    ${PN}-manager \
    ${PN}-monitor \
    ${PN}-dreport \
    ${PN}-scripts \
"
PACKAGES =+ "${DEBUG_COLLECTOR_PKGS}"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${DEBUG_COLLECTOR_PKGS}"
RDEPENDS_${PN}-staticdev = "${DEBUG_COLLECTOR_PKGS}"

DBUS_PACKAGES = "${PN}-manager"

SYSTEMD_PACKAGES = "${PN}-monitor"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative \
        phosphor-debug-collector

require phosphor-debug-collector.inc

DEPENDS += " \
        phosphor-dbus-interfaces \
        phosphor-dbus-interfaces-native \
        phosphor-logging \
        sdbusplus \
        sdbusplus-native \
        autoconf-archive-native \
"

RDEPENDS_${PN}-manager += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        ${PN}-dreport \
"
RDEPENDS_${PN}-monitor += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
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

FILES_${PN}-manager += "${sbindir}/phosphor-dump-manager"
FILES_${PN}-monitor += "${sbindir}/phosphor-dump-monitor"
FILES_${PN}-dreport += "${bindir}/dreport"
FILES_${PN}-scripts += "${dreport_dir}"

DBUS_SERVICE_${PN}-manager += "${MGR_SVC}"
SYSTEMD_SERVICE_${PN}-monitor += "obmc-dump-monitor.service"

EXTRA_OECONF = "BMC_DUMP_PATH=${bmc_dump_path}"

S = "${WORKDIR}/git"

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

# Parse the scripts in base directory, read config value
# Create user directories based on the dump type value in the config section
# Create softlinks for the base scripts in the user directories
python install_dreport_user_scripts() {
    from shutil import copyfile
    import stat
    import re
    import configparser

    #Read the user types from the dreport.conf file
    configure = configparser.ConfigParser()
    conf_dir  = d.getVar('D', True) + d.getVar('dreport_conf_dir', True)
    confsource = os.path.join(conf_dir, "dreport.conf")
    configure.read(confsource)
    section = "DumpType"
    options = configure.options(section)

    #open the script from base dir and read the config value
    source = d.getVar('S', True)
    source_path = os.path.join(source, "tools", "dreport.d", "plugins.d")
    scripts = os.listdir(source_path)
    dreport_dir= d.getVar('D', True) + d.getVar('dreport_dir', True)
    config = ("config:")
    for script in scripts:
        srcname = os.path.join(source_path, script)
        srclink = os.path.join(d.getVar('dreport_plugin_dir', True), script)
        file = open(srcname, "r")
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
}

#Enable ubifs-workaround by MACHINE_FEATURE obmc-ubi-fs.
PACKAGECONFIG_append = "${@mf_enabled(d, 'obmc-ubi-fs', 'ubifs-workaround')}"
PACKAGECONFIG[ubifs-workaround] = " \
       --enable-ubifs-workaround, \
       --disable-ubifs-workaround \
"

do_install[postfuncs] += "install_dreport"
do_install[postfuncs] += "install_dreport_conf_file"
do_install[postfuncs] += "install_dreport_plugins_scripts"
do_install[postfuncs] += "install_dreport_include_scripts"
do_install[postfuncs] += "install_dreport_user_scripts"
