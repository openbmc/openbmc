# Augments upstream linux-firmware with additional and updated images
# from Raspbian:
# https://github.com/RPi-Distro/firmware-nonfree
# https://github.com/RPi-Distro/bluez-firmware

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
    ${PN}-bcm43430a1-hcd \
    ${PN}-bcm4345c0-hcd \
"

# ${PN}-bcm43455 package and brcmfmac43455-sdio from linux-firmware
# is already included in the oe-core recipe, so don't add it to PACKAGES
# again, the version from raspbian-nf seems a bit newer:
# $ strings ./1_0.0+gitAUTOINC+d114732723+86e88fbf03+e28cd7ee86-r0/git/brcm/brcmfmac43455-sdio.bin | grep Ver
# Version: 7.45.18.0 CRC: d7226371 Date: Sun 2015-03-01 07:31:57 PST Ucode Ver: 1026.2 FWID: 01-6a2c8ad4
# $ strings ./1_0.0+gitAUTOINC+d114732723+86e88fbf03+e28cd7ee86-r0/raspbian-nf/brcm/brcmfmac43455-sdio.bin | grep Ver
# Version: 7.45.154 (r684107 CY) CRC: b1f79383 Date: Tue 2018-02-27 03:18:17 PST Ucode Ver: 1043.2105 FWID 01-4fbe0b04

# For additional Broadcom
LICENSE_${PN}-bcm43455 = "Firmware-broadcom_bcm43xx"

FILES_${PN}-bcm43430_append_rpi = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.txt \
"
FILES_${PN}-bcm43455 = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.* \
"

RDEPENDS_${PN}-bcm43455 += "${PN}-broadcom-license"

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
