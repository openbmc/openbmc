# Copyright (C) 2014-2015 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SUMMARY = "Set of Bluetooth related tools for inclusion in images"
DESCRIPTION = "Includes bluetooth specific tools for this version of BlueZ.\
These tools are used at runtime. \
Supports BlueZ4 and BlueZ5."

inherit packagegroup
inherit bluetooth

RDEPENDS_bluez4 = " \
    obexftp \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'libasound-module-bluez', '', d)} \
"

RDEPENDS_bluez5 = " \
    bluez5-noinst-tools \
    bluez5-obex \
    bluez5-testtools  \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', \
        'pulseaudio-module-bluetooth-discover \
         pulseaudio-module-bluetooth-policy \
         pulseaudio-module-bluez5-discover \
         pulseaudio-module-bluez5-device \
         pulseaudio-module-switch-on-connect \
         pulseaudio-module-loopback', \
        '', d)} \
"

# Install bluez4 tools or bluez5 tools depending on what is specified in the distro.
# Otherwise install nothing.
RDEPENDS_${PN} = "${RDEPENDS_${BLUEZ}}"
