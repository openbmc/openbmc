SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[sha256sum] = "14e34563cdad5f01d193f1ef7cd859a0fbdaa846726d44b0b68f4451a7458458"

RDEPENDS:${PN} += " \
	libpulse \
	python3-ctypes \
"

inherit pypi setuptools3
