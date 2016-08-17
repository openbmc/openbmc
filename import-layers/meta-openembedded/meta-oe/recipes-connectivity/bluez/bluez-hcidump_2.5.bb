SUMMARY = "Linux Bluetooth Stack HCI Debugger Tool"
DESCRIPTION = "The hcidump tool reads raw HCI data coming from and going to a Bluetooth device \
and displays the commands, events and data in a human-readable form."

SECTION = "console"
# hcidump was integrated into bluez5
DEPENDS = "bluez4"
RCONFLICTS_${PN} = "bluez5"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://src/hcidump.c;beginline=1;endline=23;md5=3bee3a162dff43a5be7470710b99fbcf"
PR = "r1"

PNBLACKLIST[bluez-hcidump] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

SRC_URI = "http://www.kernel.org/pub/linux/bluetooth/bluez-hcidump-${PV}.tar.gz \
           file://obsolete_automake_macros.patch \
"

SRC_URI[md5sum] = "2eab54bbd2b59a2ed4274ebb9390cf18"
SRC_URI[sha256sum] = "9b7c52b375081883738cf049ecabc103b97d094b19c6544fb241267905d88881"
S = "${WORKDIR}/bluez-hcidump-${PV}"

inherit autotools
