SUMMARY = "A pair of tools for reading the EDID from a monitor"
DESCRIPTION = "read-edid is a set of two tools - get-edid, which gets the raw \
               EDID information from the monitor, and parse-edid, which turns \
	       the raw binary information into a xorg.conf-compatible monitor \
	       section."
AUTHOR = "Stefan Wiehler <stefan.wiehler@missinglinkelectronics.com>"
HOMEPAGE = "http://www.polypux.org/projects/read-edid/"
SECTION = "console/utils"
LICENSE = "read-edid"
LIC_FILES_CHKSUM = "file://LICENSE;md5=292c42e2aedc4af636636bf7af0e2b26"

SRC_URI = "http://polypux.org/projects/read-edid/read-edid-${PV}.tar.gz \
           file://0001-Do-not-install-license-file.patch \
           file://0001-get-edid-define-quiet-once.patch \
	   "
SRC_URI[md5sum] = "016546e438bf6c98739ff74061df9854"
SRC_URI[sha256sum] = "c7c6d8440f5b90f98e276829271ccea5b2ff5a3413df8a0f87ec09f834af186f"

EXTRA_OECMAKE = "-DCLASSICBUILD=OFF"

inherit cmake
