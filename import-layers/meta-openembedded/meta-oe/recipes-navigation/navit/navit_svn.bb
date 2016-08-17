require navit.inc

SRCREV = "5310"
PV = "0.2.0+svnr${SRCPV}"
PR = "${INC_PR}.3"

S = "${WORKDIR}/${BPN}"
SRC_URI += "svn://anonymous@navit.svn.sourceforge.net/svnroot/navit/trunk;module=navit;protocol=http \
    file://freetype-include-path.patch \
    file://configure.add.imlib2.option.patch \
"
