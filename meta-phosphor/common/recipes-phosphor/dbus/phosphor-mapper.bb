SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.ObjectMapper.service"
SYSTEMD_SERVICE_${PN} += " \
        mapper-wait@.service \
        mapper-subtree-remove@.service \
        "
RDEPENDS_libmapper += "libsystemd"
RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        pyphosphor-dbus \
        sdbusplus \
        phosphor-logging \
        "
SRC_URI += "git://github.com/openbmc/phosphor-objmgr"

SRCREV = "8b633b7ea4ab671a73c1f57d878041f83f672a7e"

S = "${WORKDIR}/git"

python populate_packages_prepend () {
    mapperlibdir = d.getVar("libdir", True)
    do_split_packages(d, mapperlibdir, '^lib(.*)\.so\.*', 'lib%s', 'Phosphor mapper %s library', extra_depends='', allow_links=True)
}
PACKAGES_DYNAMIC += "^libmapper.*"
FILES_${PN}_remove = "${libdir}/lib*.so.* ${libdir}/*"
