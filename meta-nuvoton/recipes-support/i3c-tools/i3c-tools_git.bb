DESCRIPTION = "I3C Tools"
PR = "r1"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README.md;beginline=1;endline=2;md5=c9111daf206fae2fe457e0ee79f8bbbe"

SRC_URI = "git://github.com/vitor-soares-snps/i3c-tools.git;branch=master;protocol=https"
SRCREV = "5d752038c72af8e011a2cf988b1476872206e706"
S = "${WORKDIR}/git"

inherit meson pkgconfig

