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

SRC_URI[md5sum] = "4cbe1c1def7a5e1b0ed5fce8e512f4c6"
SRC_URI[sha256sum] = "e501a079b5dfaef0897c56152770c77e05e362065cec58910289aa567277ee2e"
