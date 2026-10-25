DESCRIPTION = "A helper library to generate run-time configuration for the Raspberry Pi \
ISP (PiSP), consisting of the Frontend and Backend hardware components."
HOMEPAGE = "https://github.com/raspberrypi/libpisp"
LICENSE = "BSD-2-Clause & GPL-2.0-only & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3417a46e992fdf62e5759fba9baef7a7 \
                    file://LICENSES/GPL-2.0-only.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSES/GPL-2.0-or-later.txt;md5=fed54355545ffd980b814dab4a3b312c"

SRC_URI = "git://github.com/raspberrypi/libpisp.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "9ba67e6680f03f31f2b1741a53e8fd549be82cbe"

DEPENDS = "nlohmann-json"

inherit meson pkgconfig
