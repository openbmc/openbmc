SUMMARY = "A lightweight Terminal Emulator based on libvte, written in Vala"
SECTION = "x11/applications"
DEPENDS = "vte intltool-native"
SRCREV = "0fefa38087581f85fa0631b40500b9428369c146"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
PV = "1.3+gitr${SRCPV}"
PE = "1"
PR = "r1"

PNBLACKLIST[vala-terminal] ?= "BROKEN: Doesn't work with B!=S, touch: cannot touch `src/.stamp': No such file or directory"

inherit autotools perlnative vala

SRC_URI = "${FREESMARTPHONE_GIT}/vala-terminal.git;branch=master"
S = "${WORKDIR}/git"

RDEPENDS_${PN} = "ttf-liberation-mono"
RREPLACES_${PN} = "openmoko-terminal2"
RPROVIDES_${PN} = "openmoko-terminal2"
