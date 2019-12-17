SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[md5sum] = "8e79ef002856c1858058ca0b08bf1a01"
SRC_URI[sha256sum] = "41db4dd19e7cd28e2609baf2b551f34991f1890024be119b6075a286abfb65d3"

RDEPENDS_${PN} += "pulseaudio"

inherit pypi setuptools3
