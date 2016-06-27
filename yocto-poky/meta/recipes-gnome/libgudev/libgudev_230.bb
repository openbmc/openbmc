SUMMARY = "GObject wrapper for libudev"

SRC_URI[archive.md5sum] = "e4dee8f3f349e9372213d33887819a4d"
SRC_URI[archive.sha256sum] = "a2e77faced0c66d7498403adefcc0707105e03db71a2b2abd620025b86347c18"

DEPENDS = "glib-2.0 udev"

RCONFLICTS_${PN} = "systemd (<= 220)"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit gnomebase gobject-introspection

