SUMMARY = "XvMC: X Video Motion Compensation extension library"

DESCRIPTION = "XvMC extends the X Video extension (Xv) and enables \
hardware rendered motion compensation support."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0a207f08d4961489c55046c9a5e500da \
                    file://wrapper/XvMCWrapper.c;endline=26;md5=5151daa8172a3f1bb0cb0e0ff157d9de"

DEPENDS += "libxext libxv xorgproto"

PE = "1"

XORG_PN = "libXvMC"

SRC_URI[md5sum] = "707175185a2e0490b8173686c657324f"
SRC_URI[sha256sum] = "4a2e34d444a683a7c010b01b23cefe2b8043a063ce4dc6a9b855836b5262622d"
