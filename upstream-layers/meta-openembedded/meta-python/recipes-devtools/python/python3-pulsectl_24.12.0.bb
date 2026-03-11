SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[sha256sum] = "288d6715232ac6f3dcdb123fbecaa2c0b9a50ea4087e6e87c3f841ab0a8a07fc"

RDEPENDS:${PN} += " \
	libpulse \
	python3-ctypes \
"

inherit pypi setuptools3
