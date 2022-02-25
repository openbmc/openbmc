SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[sha256sum] = "f28fe4b881dd2cc144d2d94f83ec60d8c59a52642a0ad3635cc4d0f8406f4858"

RDEPENDS:${PN} += " \
	libpulse \
	python3-ctypes \
"

inherit pypi setuptools3
