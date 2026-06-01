SUMMARY = "Udev rule to fix INA230 shunt resistor value"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${UNPACKDIR}"

SRC_URI += " \
    file://99-ina230-shunt-fix.rules \
"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/99-ina230-shunt-fix.rules ${D}${sysconfdir}/udev/rules.d/99-ina230-shunt-fix.rules
}
