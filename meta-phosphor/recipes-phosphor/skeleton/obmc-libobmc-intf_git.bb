SUMMARY = "OpenBMC gdbus library"
DESCRIPTION = "libopenbmc_intf provides a set of gpio access \
methods and the GDBUS skeleton code for the org.openbmc DBUS API."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit skeleton
inherit pkgconfig

DEPENDS += "glib-2.0"
DEPENDS += "cjson"

SKELETON_DIR = "libopenbmc_intf"

SRC_URI += "file://gpio_defs.json"

do_install() {
        oe_runmake install DESTDIR=${D}

        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${WORKDIR}/gpio_defs.json ${D}/${sysconfdir}/default/obmc/gpio/
}
