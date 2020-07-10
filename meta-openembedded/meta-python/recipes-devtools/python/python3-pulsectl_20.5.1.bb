SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[md5sum] = "8de84a0f4005ecad96d2c55a6d7c3a10"
SRC_URI[sha256sum] = "39b0a0e7974a7d6468d826a838822f78b00ac9c3803f0d7bfa9b1cad08ee22db"

RDEPENDS_${PN} += "pulseaudio"

inherit pypi setuptools3
