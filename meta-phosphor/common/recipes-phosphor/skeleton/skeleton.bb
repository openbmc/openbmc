SUMMARY = "Temp placeholder for skeleton function"
DESCRIPTION = "Temp placeholder for skeleton repository"
HOMEPAGE = "http://github.com/openbmc/skeleton"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit obmc-phosphor-chassis-mgmt
inherit obmc-phosphor-event-mgmt
inherit obmc-phosphor-fan-mgmt
inherit obmc-phosphor-flash-mgmt
inherit obmc-phosphor-policy-mgmt
inherit obmc-phosphor-sensor-mgmt
inherit obmc-phosphor-system-mgmt

DEPENDS += "glib-2.0"
RDEPENDS_${PN} += "python-subprocess python-tftpy"
SRC_URI += "git://github.com/openbmc/skeleton"
#SRC_URI += "file:///home/njames/openbmc/skeleton"

SRCREV = "b4914ad2a185f8febfe79f1b794bb816d6b12621"

S = "${WORKDIR}/git"
#S = "${WORKDIR}/home/njames/skeleton/"

do_compile() {
        oe_runmake all
}

do_install() {
        install -d ${D}/${sbindir} ${D}${libdir}
        for i in ${S}/bin/*.py ${S}/bin/*.exe; do
                install $i ${D}/${sbindir}
        done
        install ${S}/bin/libopenbmc_intf.so ${D}/${libdir}
        install ${S}/bin/console ${D}/${sbindir}
        install ${S}/bin/gpio2num ${D}/${sbindir}
        install ${S}/bin/obmcutil ${D}/${sbindir}
}
