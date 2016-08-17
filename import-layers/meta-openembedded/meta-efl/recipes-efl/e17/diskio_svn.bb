LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
                    file://COPYING-PLAIN;md5=68be76d8126face2fbbecdf1bcbe2b10"

PV = "0.0.1+svnr${SRCPV}"
PR = "${INC_PR}.0"

PNBLACKLIST[diskio] ?= "broken: switch to https://git.enlightenment.org/enlightenment/modules/diskio.git/ and fix 0.0.1+svnr82070-r0.0/E-MODULES-EXTRA/diskio/e-module-diskio.edc:58. invalid state name: 'off'. "default" state must always be first."

require e-module.inc
