SUMMARY = "iCal and scheduling (RFC 2445, 2446, 2447) library"
HOMEPAGE = "https://github.com/libical/libical"
BUGTRACKER = "https://github.com/libical/libical/issues"
LICENSE = "LGPLv2.1 | MPL-1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4fc58309d8ed46587ac63bb449d82f8 \
                    file://LICENSE;md5=d1a0891cd3e582b3e2ec8fe63badbbb6"
SECTION = "libs"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "f4b8e33ae5efb2f025eb43ce69682a36"
SRC_URI[sha256sum] = "0072e83834092315772e6719b85fc8b11530b1ff53f4d108315fb38cddbce8c2"

inherit autotools
