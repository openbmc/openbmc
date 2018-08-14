# Augments upstream linux-firmware with additional and updated images
# from Raspbian:
# https://github.com/RPi-Distro/firmware-nonfree
# https://github.com/RPi-Distro/bluez-firmware

LICENSE_append_rpi = "\
    & Firmware-cypress \
"

LIC_FILES_CHKSUM_append_rpi = "\
    file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7 \
"
NO_GENERIC_LICENSE[Firmware-cypress] = "LICENCE.cypress"

SRC_URI_append_rpi = " \
    git://github.com/RPi-Distro/firmware-nonfree;destsuffix=raspbian-nf;name=raspbian-nf \
    git://github.com/RPi-Distro/bluez-firmware;destsuffix=raspbian-bluez;name=raspbian-bluez \
"

SRCREV_raspbian-nf = "86e88fbf0345da49555d0ec34c80b4fbae7d0cd3"
SRCREV_raspbian-bluez = "e28cd7ee8615de33aa7ec2b41d556af61a4a2707"
SRCREV_FORMAT_rpi = "default+raspbian-nf+raspbian-bluez"

do_install_append_rpi() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm/

    # Replace outdated linux-firmware files with updated ones from
    # raspbian firmware-nonfree. Raspbian adds blobs and nvram
    # definitions that are also necessary so copy those too.
    for fw in brcmfmac43430-sdio brcmfmac43455-sdio ; do
        install -m 0644 ${WORKDIR}/raspbian-nf/brcm/${fw}.* ${D}${nonarch_base_libdir}/firmware/brcm/
    done

    # Add missing Cypress Bluetooth files from raspbian bluez-firmware
    for fw in BCM43430A1.hcd BCM4345C0.hcd ; do
        install -m 0644 ${WORKDIR}/raspbian-bluez/broadcom/${fw} ${D}${nonarch_base_libdir}/firmware/brcm/
    done
}

# NB: Must prepend, else these become empty and their content is left in
# the roll-up package which precedes them.
PACKAGES_prepend_rpi = "\
    ${PN}-bcm43455 \
    ${PN}-cypress-license \
    ${PN}-bcm43430a1-hcd \
    ${PN}-bcm4345c0-hcd \
"

# For additional Broadcom
LICENSE_${PN}-bcm43455 = "Firmware-broadcom_bcm43xx"

FILES_${PN}-bcm43430_append_rpi = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.txt \
"
FILES_${PN}-bcm43455 = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.* \
"

RDEPENDS_${PN}-bcm43455 += "${PN}-broadcom-license"

# For additional Cypress
FILES_${PN}-cypress-license = "\
  ${nonarch_base_libdir}/firmware/LICENCE.cypress \
"

LICENSE_${PN}-bcm43430a1-hcd = "Firmware-cypress"
LICENSE_${PN}-bcm4345c0-hcd = "Firmware-cypress"

FILES_${PN}-bcm43430a1-hcd = " \
  ${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd \
"
FILES_${PN}-bcm4345c0-hcd = " \
  ${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd \
"

RDEPENDS_${PN}-bcm43430a1-hcd += "${PN}-cypress-license"
RDEPENDS_${PN}-bcm4345c0-hcd += "${PN}-cypress-license"
