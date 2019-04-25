require xorg-app-common.inc

SUMMARY = "Print out X-Video extension adaptor information"

DESCRIPTION = "xvinfo prints out the capabilities of any video adaptors \
associated with the display that are accessible through the X-Video \
extension."

LIC_FILES_CHKSUM = "file://COPYING;md5=b664101ad7a1dc758a4c4109bf978e68"
DEPENDS += " libxv"
PE = "1"

SRC_URI[md5sum] = "b13afec137b9b331814a9824ab03ec80"
SRC_URI[sha256sum] = "0353220d6606077ba42363db65f50410759f9815352f77adc799e2adfa76e73f"
