#
# Copyright (C) 2007-2008 OpenedHand Ltd.
#

SUMMARY = "Sato desktop"
DESCRIPTION = "Packagegroups provide a convenient mechanism of bundling a collection of packages."
HOMEPAGE = "https://www.yoctoproject.org/"
PR = "r33"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup features_check
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES = "${PN} ${PN}-base ${PN}-apps ${PN}-games"

RDEPENDS:${PN} = "\
    ${PN}-base \
    ${PN}-apps \
    ${PN}-games \
    "

NETWORK_MANAGER ?= "connman-gnome"

SUMMARY:${PN}-base = "Sato desktop - base packages"
RDEPENDS:${PN}-base = "\
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
GSTEXAMPLES:riscv64 = ""

SUMMARY:${PN}-apps = "Sato desktop - applications"
RDEPENDS:${PN}-apps = "\
    l3afpad \
    matchbox-terminal \
    sato-screenshot \
    ${FILEMANAGER} \
    ${GSTEXAMPLES} \
    ${WEB} \
    "

SUMMARY:${PN}-games = "Sato desktop - games"
RDEPENDS:${PN}-games = "\
    puzzles \
    "
