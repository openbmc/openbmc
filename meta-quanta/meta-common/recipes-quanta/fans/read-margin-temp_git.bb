PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://github.com/quanta-bmc/read-margin-temp.git;branch=master;protocol=https"
SRCREV = "cadbe4801db76799e21b7bc9c405e5df9988dee0"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit meson

DEPENDS += " nlohmann-json"
DEPENDS += " sdbusplus"
DEPENDS += " sdeventplus"
DEPENDS += " phosphor-dbus-interfaces"
RDEPENDS:${PN} += " bash"

FILES:${PN} = "${bindir}/read-margin-temp"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 read-margin-temp ${D}${bindir}
}

