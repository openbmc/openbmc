#
# Copyright (C) 2007-2008 OpenedHand Ltd.
#

SUMMARY = "Sato desktop"
PR = "r33"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup features_check
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES = "${PN} ${PN}-base ${PN}-apps ${PN}-games"

RDEPENDS_${PN} = "\
    ${PN}-base \
    ${PN}-apps \
    ${PN}-games \
    "

NETWORK_MANAGER ?= "connman-gnome"

SUMMARY_${PN}-base = "Sato desktop - base packages"
RDEPENDS_${PN}-base = "\
    matchbox-desktop \
    matchbox-session-sato \
    matchbox-keyboard \
    matchbox-keyboard-applet \
    matchbox-keyboard-im \
    matchbox-config-gtk \
    xcursor-transparent-theme \
    adwaita-icon-theme \
    settings-daemon \
    shutdown-desktop \
    ${NETWORK_MANAGER} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', 'udev-extraconf', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio-server pulseaudio-client-conf-sato pulseaudio-misc', '', d)} \
    "

FILEMANAGER ?= "pcmanfm"

WEB ?= ""
#WEB = "epiphany"

GSTEXAMPLES ?= "gst-examples"
GSTEXAMPLES_riscv64 = ""

SUMMARY_${PN}-apps = "Sato desktop - applications"
RDEPENDS_${PN}-apps = "\
    l3afpad \
    matchbox-terminal \
    sato-screenshot \
    ${FILEMANAGER} \
    ${GSTEXAMPLES} \
    ${WEB} \
    "

SUMMARY_${PN}-games = "Sato desktop - games"
RDEPENDS_${PN}-games = "\
    puzzles \
    "
