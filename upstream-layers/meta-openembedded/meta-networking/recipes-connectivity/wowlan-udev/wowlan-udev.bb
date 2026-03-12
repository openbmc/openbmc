SUMMARY = "Enable WoWLAN via udev on Wi-Fi PHY registration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://99-wowlan.rules"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-wowlan.rules \
        ${D}${sysconfdir}/udev/rules.d/
}

INHIBIT_DEFAULT_DEPS = "1"
RDEPENDS:${PN} = "iw udev"
