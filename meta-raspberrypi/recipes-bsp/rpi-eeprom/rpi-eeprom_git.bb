SUMMARY = "Installation scripts and binaries for the Raspberry Pi 4 EEPROM"
DESCRIPTION = "This repository contains the rpi4 bootloader and scripts \
for updating it in the spi eeprom"
LICENSE = "BSD-3-Clause & Broadcom-RPi"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f546ed4f47e9d4c1fe954ecc9d3ef4f3"

SRC_URI = " \
    git://github.com/raspberrypi/rpi-eeprom.git;protocol=https;branch=master \
    file://0001-Fix-rpi-eeprom-update-when-using-busybox-find.patch \
"

SRCREV = "6e79e995bbc75c5fdd5305bd7fe029758cfade2f"
PV = "v2022.12.07-138a1"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += " \
    coreutils \
    python3 \
    python3-pycryptodomex \
    openssl \
    xxd \
    pciutils \
"

inherit python3native

do_install() {
    install -d ${D}${bindir}

    # install executables
    install -m 0755 ${S}/tools/vl805 ${D}${bindir}
    install -m 0755 ${S}/rpi-eeprom-update ${D}${bindir}
    install -m 0755 ${S}/rpi-eeprom-config ${D}${bindir}
    install -m 0755 ${S}/rpi-eeprom-digest ${D}${bindir}

    # copy firmware files
    install -d ${D}${base_libdir}/firmware/raspberrypi/bootloader/critical
    install -d ${D}${base_libdir}/firmware/raspberrypi/bootloader/stable
    install -d ${D}${base_libdir}/firmware/raspberrypi/bootloader/beta

    install -m 644 ${S}/firmware/critical/* ${D}${base_libdir}/firmware/raspberrypi/bootloader/critical
    install -m 644 ${S}/firmware/stable/* ${D}${base_libdir}/firmware/raspberrypi/bootloader/stable
    install -m 644 ${S}/firmware/beta/* ${D}${base_libdir}/firmware/raspberrypi/bootloader/beta

    ln -s critical ${D}${base_libdir}/firmware/raspberrypi/bootloader/default
    ln -s stable ${D}${base_libdir}/firmware/raspberrypi/bootloader/latest

    # copy default config
    install -d ${D}${sysconfdir}/default
    install -D ${S}/rpi-eeprom-update-default ${D}${sysconfdir}/default/rpi-eeprom-update
}

FILES:${PN} += "${base_libdir}/firmware/raspberrypi/bootloader/*"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# vl805 tool sources are not available (yet), as it comes as a precompiled
# binary only. It has ARM architecture whereas target machine is Aarch64. We
# need to disable arch check for it otherwise it cannot packed.
QAPATHTEST[arch] = ""

COMPATIBLE_MACHINE = "raspberrypi4|raspberrypi4-64"
