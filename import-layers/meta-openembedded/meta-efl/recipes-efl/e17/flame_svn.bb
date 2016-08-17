LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
                    file://COPYING-PLAIN;md5=c910b645eda0005ccec46f75203dc96e"

PV = "0.0.3+svnr${SRCPV}"
PR = "${INC_PR}.0"

require e-module.inc

do_configure_prepend() {
    sed -i -e /po/d ${S}/configure.ac
    sed -i -e s:\ po::g ${S}/Makefile.am
}

SRC_URI += "file://configure.patch"
