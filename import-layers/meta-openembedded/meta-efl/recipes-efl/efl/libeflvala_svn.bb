SUMMARY = "Vala meets the Enlightenment Foundation Libraries"
AUTHOR = "Michael 'Mickey' Lauer <mlauer@vanille-media.de>"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SECTION = "devel"
DEPENDS = "vala-native glib-2.0 dbus dbus-glib eina eet evas ecore edje elementary tiff"
PV = "2011.01.13.1+svnr${SRCPV}"
PE = "1"
PR = "r2"
SRCREV = "${EFL_SRCREV}"
SRCNAME = "vala"

inherit e-base autotools-brokensep pkgconfig vala

SRC_URI = "${E_SVN}/trunk/BINDINGS;module=${SRCNAME};protocol=http \
    file://disable.eina.test.patch \
"
S = "${WORKDIR}/${SRCNAME}"

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${datadir}/libeflvala ${bindir}/*"
