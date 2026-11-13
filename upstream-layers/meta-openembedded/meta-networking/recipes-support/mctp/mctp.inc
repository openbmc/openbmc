HOMEPAGE = "http://www.github.com/CodeConstruct/mctp"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4cc91856b08b094b4f406a29dc61db21"

SRC_URI = "git://github.com/CodeConstruct/mctp;branch=stable/2.6.x;protocol=https;tag=v${PV}"
SRCREV = "ea1dfa9e59681c42d02ed3bf91cb150161481403"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=false"
