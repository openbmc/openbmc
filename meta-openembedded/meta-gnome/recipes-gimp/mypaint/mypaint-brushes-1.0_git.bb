SUMMARY = "MyPaint brushes"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=65d3616852dbf7b1a6d4b53b00626032"

inherit autotools allarch

SRC_URI = "git://github.com/mypaint/mypaint-brushes.git;protocol=https;branch=v1.3.x"
SRCREV = "8a0124ac0675103eae8fa41fad533851768ae1ce"
PV = "1.3.1"
S = "${WORKDIR}/git"

FILES:${PN} += "${datadir}/mypaint-data"
