SUMMARY = "OpenBMC gdbus library"
DESCRIPTION = "libopenbmc_intf provides a set of gpio access \
methods and the GDBUS skeleton code for the org.openbmc DBUS API."
PR = "r1"

inherit skeleton
inherit pkgconfig

DEPENDS += "glib-2.0"

SKELETON_DIR = "libopenbmc_intf"

do_install() {
        oe_runmake install DESTDIR=${D}
}
