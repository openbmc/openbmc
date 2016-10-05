SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit pythonnative
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
#inherit obmc-phosphor-systemd
#inherit setuptools

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"

DBUS_SERVICE_${PN} += "org.openbmc.ObjectMapper.service"
SYSTEMD_SERVICE_${PN} = "mapper-wait@.service"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        "
#SRC_URI += "git://github.com/openbmc/phosphor-objmgr"
SRC_URI += "git:///home/msbarth/openbmc/phosphor-objmgr;branch=i380"
#SRCREV = "a562704d9b9c3f77deb1b957d0f586f7f5b9ca3e"
SRCREV = "df2fa38011e65eb9c6e7ea2d299cf4c3118cc080"

S = "${WORKDIR}/git"


#do_compile_append() {
#        oe_runmake -C libmapper
#}

#do_install_append() {
#        oe_runmake -C libmapper install DESTDIR=${D}
#}

#python populate_packages_prepend () {
#    mapperlibdir = d.getVar("libdir", True)
#    do_split_packages(d, mapperlibdir, '^lib(.*)\.so\.*', 'lib%s', 'Phosphor mapper %s library', extra_depends='', allow_links=True)
#}
#PACKAGES_DYNAMIC += "^libmapper.*"
#FILES_${PN}_remove = "${libdir}/lib*.so.* ${libdir}/*"
