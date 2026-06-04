SUMMARY = "VR-NVL board udev rules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

S = "${UNPACKDIR}"

# Configure the I/O board bay straps and release the SMA MCUs from reset
# so the I/O board and BlueField SMAs can boot.
SRC_URI = "file://99-cp2112-cx9-gpio.rules"

RDEPENDS:${PN} += "libgpiod-tools"

do_install() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-cp2112-cx9-gpio.rules \
        ${D}${nonarch_base_libdir}/udev/rules.d/
}
