SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[md5sum] = "07d7a5fddc49f5da22634464aa008003"
SRC_URI[sha256sum] = "fca9ed501bef2efd551b35773fd24bba36bbd21bc448f402cf8ee13c12423c19"

RDEPENDS_${PN} += "pulseaudio"

inherit pypi setuptools3
