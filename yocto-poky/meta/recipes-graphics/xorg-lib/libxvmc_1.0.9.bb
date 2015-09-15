SUMMARY = "XvMC: X Video Motion Compensation extension library"

DESCRIPTION = "XvMC extends the X Video extension (Xv) and enables \
hardware rendered motion compensation support."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0a207f08d4961489c55046c9a5e500da \
                    file://wrapper/XvMCWrapper.c;endline=26;md5=5151daa8172a3f1bb0cb0e0ff157d9de"

DEPENDS += "libxext libxv videoproto"

PE = "1"

XORG_PN = "libXvMC"

SRC_URI[md5sum] = "eba6b738ed5fdcd8f4203d7c8a470c79"
SRC_URI[sha256sum] = "0703d7dff6ffc184f1735ca5d4eb9dbb402b522e08e008f2f96aee16c40a5756"
