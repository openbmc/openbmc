DESCRIPTION = "Emprint is a utility for taking screenshots of the entire screen, a specific window, or a specific region."
LICENSE = "MIT & BSD"
DEPENDS = "imlib2 virtual/libx11 ecore evas edje eina"
PV = "0.0.1+svnr${SRCPV}"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35"
SRCREV = "${EFL_SRCREV}"

inherit e
SRC_URI = "${E_SVN}/trunk;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

FILES_${PN}-dbg += "${libdir}/${PN}/modules/.debug"

PNBLACKLIST[emprint] ?= "if you want to use these modules with E18, then you need to update it to git recipe fetching newer sources from http://git.enlightenment.org/apps/emprint.git/"
