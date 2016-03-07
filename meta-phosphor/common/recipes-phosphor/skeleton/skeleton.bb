SUMMARY = "Temp placeholder for skeleton function"
DESCRIPTION = "Temp placeholder for skeleton repository"
HOMEPAGE = "http://github.com/openbmc/skeleton"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit obmc-phosphor-chassis-mgmt
inherit obmc-phosphor-fan-mgmt
inherit obmc-phosphor-flash-mgmt
inherit obmc-phosphor-policy-mgmt
inherit obmc-phosphor-sensor-mgmt
inherit obmc-phosphor-system-mgmt

DEPENDS += "glib-2.0 systemd"
RDEPENDS_${PN} += "python-subprocess python-compression libsystemd"
SRC_URI += "git://github.com/openbmc/skeleton"

SRCREV = "27ca44ad1a96f90d42dcb50183700e5ca5358642"

S = "${WORKDIR}"

do_compile() {
        oe_runmake -C git
}

do_install() {
        source=${S}/git

        install -d ${D}/${sbindir} ${D}${libdir}
        for i in ${source}/bin/*; do
                install $i ${D}/${sbindir}
        done
        for i in ${source}/lib/*; do
                install $i ${D}/${libdir}
        done
}
