SUMMARY = "Python (3.x and 2.x) high-level interface and ctypes-based bindings for PulseAudio (libpulse), mostly focused on mixer-like controls and introspection-related operations (as opposed to e.g. submitting sound samples to play, player-like client)."
HOMEPAGE = "https://github.com/mk-fg/python-pulse-control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1d10048469ff90123263eb5e214061d"

SRC_URI[sha256sum] = "3f782b3adf6d91b2398504002d248b36aa047bd04d5acd0ae5d4e2d3c8e746a0"

RDEPENDS_${PN} += " \
	libpulse \
	python3-ctypes \
"

inherit pypi setuptools3
