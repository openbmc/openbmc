SUMMARY = "MyPaint brushes"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=65d3616852dbf7b1a6d4b53b00626032"

inherit autotools allarch

SRC_URI = "git://github.com/mypaint/mypaint-brushes.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "0df6d130152a94c3bd67709941978074a1303cc5"

FILES:${PN} += "${datadir}/mypaint-data"
