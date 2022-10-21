# Copyright (C) 2018 Krzysztof Kozlowski <krzk@kernel.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Broadcom Bluetooth firmware files"
DESCRIPTION = "Firmware for Broadcom Bluetooth devices. Note that in case of BT+WiFi devices, separate WiFi firmware might be needed."
HOMEPAGE = "https://github.com/winterheart/broadcom-bt-firmware"

LICENSE = "Firmware-Broadcom-WIDCOMM"
NO_GENERIC_LICENSE[Firmware-Broadcom-WIDCOMM] = "LICENSE.broadcom_bcm20702"

LIC_FILES_CHKSUM = "file://LICENSE.broadcom_bcm20702;md5=c0d5ea0502b00df74173d0f8a48b619d"
SRC_URI = "git://github.com/winterheart/broadcom-bt-firmware.git;branch=master;protocol=https"
SRCREV = "a0eb4805dbb232f02f156b9351a23790c1a4cec7"

PE = "1"

S = "${WORKDIR}/git"

inherit allarch

CLEANBROKEN = "1"

do_compile() {
    :
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm/
    cp brcm/*.hcd ${D}${nonarch_base_libdir}/firmware/brcm/

    # For license package:
    install -m 0644 LICENSE.broadcom_bcm20702 ${D}${nonarch_base_libdir}/firmware/brcm/

    # For main package:
    install -m 0644 DEVICES.md ${D}${nonarch_base_libdir}/firmware/brcm/
}

PACKAGES =+ " \
    ${PN}-bcm20702a1 \
    ${PN}-bcm20702b0 \
    ${PN}-bcm20703a1 \
    ${PN}-bcm43142a0 \
    ${PN}-bcm4335c0 \
    ${PN}-bcm4350c5 \
    ${PN}-bcm4356a2 \
    ${PN}-bcm4371c2 \
    ${PN}-license \
"

RDEPENDS:${PN}-bcm20702a1 = "${PN}-license"
RDEPENDS:${PN}-bcm20702b0 = "${PN}-license"
RDEPENDS:${PN}-bcm20703a1 = "${PN}-license"
RDEPENDS:${PN}-bcm43142a0 = "${PN}-license"
RDEPENDS:${PN}-bcm4335c0 = "${PN}-license"
RDEPENDS:${PN}-bcm4350c5 = "${PN}-license"
RDEPENDS:${PN}-bcm4356a2 = "${PN}-license"
RDEPENDS:${PN}-bcm4371c2 = "${PN}-license"

FILES:${PN}-bcm20702a1 = "${nonarch_base_libdir}/firmware/brcm/BCM20702A1*hcd"
FILES:${PN}-bcm20702b0 = "${nonarch_base_libdir}/firmware/brcm/BCM20702B0*hcd"
FILES:${PN}-bcm20703a1 = "${nonarch_base_libdir}/firmware/brcm/BCM20703A1*hcd"
FILES:${PN}-bcm43142a0 = "${nonarch_base_libdir}/firmware/brcm/BCM43142A0*hcd"
FILES:${PN}-bcm4335c0 = "${nonarch_base_libdir}/firmware/brcm/BCM4335C0*hcd"
FILES:${PN}-bcm4350c5 = "${nonarch_base_libdir}/firmware/brcm/BCM4350C5*hcd"
FILES:${PN}-bcm4356a2 = "${nonarch_base_libdir}/firmware/brcm/BCM4356A2*hcd"
FILES:${PN}-bcm4371c2 = "${nonarch_base_libdir}/firmware/brcm/BCM4371C2*hcd"
FILES:${PN}-license += "${nonarch_base_libdir}/firmware/brcm/LICENSE.broadcom_bcm20702"

FILES:${PN} += "${nonarch_base_libdir}/firmware/brcm/*"
RDEPENDS:${PN} += "${PN}-license"

# Make broadcom-bt-firmware depend on all of the split-out packages.
python populate_packages:prepend () {
    firmware_pkgs = oe.utils.packages_filter_out_system(d)
    d.appendVar('RDEPENDS:broadcom-bt-firmware', ' ' + ' '.join(firmware_pkgs))
}
