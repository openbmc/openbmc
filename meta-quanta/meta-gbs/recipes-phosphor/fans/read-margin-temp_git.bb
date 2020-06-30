PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://github.com/quanta-bmc/read-margin-temp.git"
SRCREV = "1a74891c996725dd4c94397d2588d840da07c548"
S = "${WORKDIR}/git"

SRC_URI += " file://config-margin.json \
             file://read-margin-temp-wait.sh \
           "

inherit autotools pkgconfig
inherit meson

DEPENDS += " nlohmann-json"
DEPENDS += " sdbusplus"
DEPENDS += " sdbusplus-native"
DEPENDS += " sdeventplus"
DEPENDS += " phosphor-dbus-interfaces"

RDEPENDS_${PN} += " bash"

FILES_${PN} = "${bindir}/read-margin-temp"
FILES_${PN} += " ${bindir}/read-margin-temp-wait.sh"
FILES_${PN} += " ${datadir}/read-margin-temp/config-margin.json"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 read-margin-temp ${D}${bindir}
    install -m 0755 ${WORKDIR}/read-margin-temp-wait.sh ${D}/${bindir}

    install -d ${D}${datadir}/read-margin-temp
    install -m 0644 -D ${WORKDIR}/config-margin.json \
        ${D}${datadir}/read-margin-temp/config-margin.json

}
