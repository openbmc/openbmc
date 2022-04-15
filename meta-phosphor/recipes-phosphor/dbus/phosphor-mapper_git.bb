SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd
inherit phosphor-mapperdir

DEPENDS += "systemd"
DEPENDS += "boost"
DEPENDS += "libtinyxml2"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "${PN}-config-native"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.ObjectMapper.service"
SYSTEMD_SERVICE:${PN} += " \
        mapper-wait@.service \
        mapper-subtree-remove@.service \
        "
SRC_URI += "git://github.com/openbmc/phosphor-objmgr;branch=master;protocol=https"

SRCREV = "af3d797b011f3f0dfc6ad7eae44e5b312f5a3d6e"

S = "${WORKDIR}/git"

EXTRA_OEMESON += "-Dtests=disabled"

python populate_packages:prepend () {
    mapperlibdir = d.getVar("libdir", True)
    do_split_packages(d, mapperlibdir, '^lib(.*)\.so\.*', 'lib%s', 'Phosphor mapper %s library', extra_depends='', allow_links=True)
}
PACKAGES_DYNAMIC += "^libmapper.*"
FILES:${PN}:remove = "${libdir}/lib*.so.* ${libdir}/*"

# Construct a systemd environment file with mapper commandline
# from the native sysroot /usr/share/phosphor-mapper filesystem.
python do_emit_env() {
    path = d.getVar('STAGING_DIR_NATIVE', True) + \
        d.getVar('service_dir', True)
    services = []
    for s in os.listdir(path):
        services.append('.'.join(s.split('-')))

    path = d.getVar('STAGING_DIR_NATIVE', True) + \
        d.getVar('interface_dir', True)
    interfaces = []
    for i in os.listdir(path):
        interfaces.append('.'.join(i.split('-')))

    path = d.getVar('STAGING_DIR_NATIVE', True) + \
        d.getVar('serviceblacklist_dir', True)
    service_blacklists = []
    for x in os.listdir(path):
        service_blacklists.append('.'.join(x.split('-')))

    path = [d.getVar('D', True) + d.getVar('envfiledir', True)]
    path.append('obmc')
    path.append('mapper')
    parent = os.path.join(*path[:-1])
    path = os.path.join(*path)

    if not os.path.exists(parent):
        os.makedirs(parent)
    with open(path, 'w+') as fd:
        fd.write('MAPPER_SERVICES="{}"'.format(' '.join(services)))
        fd.write('\n')
        fd.write('MAPPER_INTERFACES="{}"'.format(' '.join(interfaces)))
        fd.write('\n')
        fd.write('MAPPER_SERVICEBLACKLISTS="{}"'.format(' '.join(service_blacklists)))
        fd.write('\n')
}

do_install[postfuncs] += "do_emit_env"
