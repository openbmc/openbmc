SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[md5sum] = "90d02204d65f684af2fd77a77d50ec01"
SRC_URI[sha256sum] = "4a97a4f7fcd8dbe56380980e8c9519ff4e26552604776cbc7555e6a14ff0202f"

RDEPENDS_${PN} += "pulseaudio"

inherit pypi setuptools3
