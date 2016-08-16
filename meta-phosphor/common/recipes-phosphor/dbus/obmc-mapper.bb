SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd
inherit setuptools

DEPENDS += "systemd"

DBUS_SERVICE_${PN} += "org.openbmc.ObjectMapper.service"
RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        "
SRC_URI += "git://github.com/openbmc/phosphor-objmgr"

SRCREV = "22ff24a6e668d00557ef6465f96d664fe33226f3"

S = "${WORKDIR}/git"

do_compile_append() {
        oe_runmake -C libmapper
}

do_install_append() {
        oe_runmake -C libmapper install DESTDIR=${D}
}

python populate_packages_prepend () {
    mapperlibdir = d.getVar("libdir", True)
    do_split_packages(d, mapperlibdir, '^lib(.*)\.so\.*', 'lib%s', 'Phosphor mapper %s library', extra_depends='', allow_links=True)
}
PACKAGES_DYNAMIC += "^libmapper.*"
FILES_${PN}_remove = "${libdir}/lib*.so.* ${libdir}/*"
