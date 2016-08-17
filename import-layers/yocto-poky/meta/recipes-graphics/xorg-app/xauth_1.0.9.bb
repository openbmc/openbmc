require xorg-app-common.inc
SUMMARY = "X authority utilities"
DESCRIPTION = "X application to edit and display the authorization \
information used in connecting to the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=5ec74dd7ea4d10c4715a7c44f159a40b"

DEPENDS += "libxau libxext libxmu"
PE = "1"

SRC_URI[md5sum] = "7d6003f32838d5b688e2c8a131083271"
SRC_URI[sha256sum] = "56ce1523eb48b1f8a4f4244fe1c3d8e6af1a3b7d4b0e6063582421b0b68dc28f"
