SUMMARY = "EFL based browser"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"
DEPENDS = "evas ecore edje eina elementary webkit-efl sqlite3"

PV = "0.0.1+gitr${SRCPV}"
SRCREV = "b41fd0bc9514ff61bb2a50d957efe4155fa3e6a8"

# webkit-efl isn't available for < armv7a
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

inherit e gettext cmake

EXTRA_OECMAKE = "-DDATA_INSTALL_DIR=${datadir}"

SRC_URI = " \
    git://github.com/bunhere/elbow.git;protocol=https \
"
S = "${WORKDIR}/git"

FILES_${PN} += "${datadir}/themes"

PNBLACKLIST[elbow] ?= "Depends on blacklisted webkit-efl"
