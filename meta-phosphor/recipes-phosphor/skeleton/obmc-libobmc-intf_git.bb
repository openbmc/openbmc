SUMMARY = "OpenBMC gdbus library"
DESCRIPTION = "libopenbmc_intf provides a set of gpio access \
methods and the GDBUS skeleton code for the org.openbmc DBUS API."
DEPENDS += "glib-2.0"
DEPENDS += "cjson"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "file://gpio_defs.json"

SKELETON_DIR = "libopenbmc_intf"

inherit skeleton
inherit pkgconfig

do_install() {
        oe_runmake install DESTDIR=${D}
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/gpio_defs.json ${D}/${sysconfdir}/default/obmc/gpio/
}

# Ensure the library is not in the dev package
FILES_SOLIBSDEV = ""
# Now add the link to the production package
FILES:${PN} += "${libdir}/libopenbmc_intf.so"

# Users of libopenbmc_intf use custom makefiles that do not
# support the proper library version detection and linking.
# Because of that, a link must be created within the
# rootfs for applicaitons to use this library.
# This next line tells bitbake to skip the check which
# ensures no links are put in the rootfs
INSANE_SKIP:${PN} += "dev-so"
