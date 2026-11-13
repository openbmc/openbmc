HOMEPAGE = "http://www.github.com/CodeConstruct/mctp"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4cc91856b08b094b4f406a29dc61db21"

SRC_URI = "git://github.com/CodeConstruct/mctp;branch=main;protocol=https;tag=v${PV}"
SRCREV = "eca0e9a7f87ed478be206e4f52bcd51561da18aa"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=false"
