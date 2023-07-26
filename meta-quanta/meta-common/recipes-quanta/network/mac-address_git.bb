LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit cmake pkgconfig
inherit systemd

RDEPENDS:${PN} += " bash"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/quanta-bmc/mac-address.git;protocol=https;branch=master"
SRCREV = "93756c78539160a7c15e02c7c3ecf2d6941d56af"
DEPENDS += "systemd"

FILES:${PN} += "${bindir}/mac-address"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "mac-address.service"

EXTRA_OECMAKE:append = " \
    -DENABLE_TEST=OFF \
"
