SUMMARY = "Edje_Viewer is just that"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
                    file://COPYING-PLAIN;md5=e01359041001e8bf24c09acca556e792"

DEPENDS = "elementary"
PV = "0.0.0+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

inherit e

SRCNAME = "edje_viewer"
SRC_URI = "${E_SVN}/trunk;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

FILES_${PN} += "${datadir}"
