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

SRCREV = "47750bc1c06aebda189f2e8d7862c9d9b9ffe35b"

S = "${WORKDIR}"

do_compile() {
        oe_runmake -C git all
}

do_install() {
        source=${S}/git

        install -d ${D}/${sbindir} ${D}${libdir}
        for i in ${source}/bin/*.py ${source}/bin/*.exe; do
                install $i ${D}/${sbindir}
        done
        install ${source}/bin/libopenbmc_intf.so ${D}/${libdir}
}
