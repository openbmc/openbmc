require ttf.inc

SUMMARY = "Ubuntu Font Family - TTF Version"
HOMEPAGE = "http://font.ubuntu.com"
LICENSE = "LicenseRef-UFL"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=325a1a9029112a2405e743c7f816427b"

SHA1SUM = "0cef8205"

SRC_URI = "https://assets.ubuntu.com/v1/${SHA1SUM}-ubuntu-font-family-${PV}.zip"


SRC_URI[sha256sum] = "61a2b342526fd552f19fef438bb9211a8212de19ad96e32a1209c039f1d68ecf"

# assets.ubuntu.com has no browsable listing and design.ubuntu.com intermittently
# serves brotli-encoded HTML that the wget fetcher cannot decode, so check the
# Ubuntu archive pool. Match only plain releases, not +git snapshots.
UPSTREAM_CHECK_URI = "http://archive.ubuntu.com/ubuntu/pool/main/f/fonts-ubuntu/"
UPSTREAM_CHECK_REGEX = "fonts-ubuntu_(?P<pver>\d+(\.\d+)+)\.orig\.tar"

S = "${UNPACKDIR}/ubuntu-font-family-${PV}"

PACKAGES = "ttf-ubuntu-mono ttf-ubuntu-sans"
FONT_PACKAGES = "ttf-ubuntu-mono ttf-ubuntu-sans"

FILES:ttf-ubuntu-mono  = "${datadir}/fonts/truetype/*Mono*"
FILES:ttf-ubuntu-sans  = "${datadir}/fonts/truetype/Ubuntu-*"
