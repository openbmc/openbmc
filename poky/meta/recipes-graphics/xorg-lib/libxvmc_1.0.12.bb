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

SRC_URI[md5sum] = "3569ff7f3e26864d986d6a21147eaa58"
SRC_URI[sha256sum] = "6b3da7977b3f7eaf4f0ac6470ab1e562298d82c4e79077765787963ab7966dcd"
