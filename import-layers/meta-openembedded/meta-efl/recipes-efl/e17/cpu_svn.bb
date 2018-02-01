LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
                    file://COPYING-PLAIN;md5=c910b645eda0005ccec46f75203dc96e"

PV = "0.0.1+svnr${SRCREV}"
PR = "${INC_PR}.0"

require e-module.inc

SRC_URI += "file://configure.patch"

PNBLACKLIST[cpu] ?= "Depends on blacklisted e-wm - the recipe will be removed on 2017-09-01 unless the issue is fixed"
