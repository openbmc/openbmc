require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "The  xkbevd event daemon listens for specified XKB \
events and executes requested commands if they occur. "

LIC_FILES_CHKSUM = "file://COPYING;md5=208668fa9004709ba22c2b748140956c"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "c747faf1f78f5a5962419f8bdd066501"
SRC_URI[sha256sum] = "2430a2e5302a4cb4a5530c1df8cb3721a149bbf8eb377a2898921a145197f96a"
