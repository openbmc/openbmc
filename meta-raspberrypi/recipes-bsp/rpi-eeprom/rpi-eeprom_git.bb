SUMMARY = "Installation scripts and binaries for the Raspberry Pi 4 EEPROM"
DESCRIPTION = "This repository contains the rpi4 bootloader and scripts \
for updating it in the spi eeprom"
LICENSE = "BSD-3-Clause & Broadcom-RPi"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f546ed4f47e9d4c1fe954ecc9d3ef4f3"

SRC_URI = " \
    git://github.com/raspberrypi/rpi-eeprom.git;protocol=https;branch=master \
"

SRCREV = "f13b5789f56f65112e2b8aa58be43ebfcbedfe1d"
PV = "v2023.10.18-2712"

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
    install -d ${D}${base_libdir}/firmware/raspberrypi/bootloader/default
    install -d ${D}${base_libdir}/firmware/raspberrypi/bootloader/latest

    install -m 644 ${S}/firmware-2711/default/* ${D}${base_libdir}/firmware/raspberrypi/bootloader/default
    install -m 644 ${S}/firmware-2711/latest/* ${D}${base_libdir}/firmware/raspberrypi/bootloader/latest

    ln -s default ${D}${base_libdir}/firmware/raspberrypi/bootloader/critical
    ln -s latest ${D}${base_libdir}/firmware/raspberrypi/bootloader/stable
    ln -s latest ${D}${base_libdir}/firmware/raspberrypi/bootloader/beta

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
