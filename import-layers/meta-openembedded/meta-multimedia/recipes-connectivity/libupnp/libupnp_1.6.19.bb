SUMMARY = "Portable SDK for UPnP* Devices"
DESCRIPTION = "The Portable SDK for UPnP Devices is an SDK for development of \
UPnP device and control point applications. It consists of the core UPnP \
protocols along with a UPnP-specific eXtensible Markup Language (XML) parser \
supporting the Document Object Model (DOM) Level 2 API and an optional, \
integrated mini web server for serving UPnP related documents."
HOMEPAGE = "http://pupnp.sourceforge.net/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3190d5244e08e78e4c8ee78544f4863"

SRC_URI = "${SOURCEFORGE_MIRROR}/pupnp/${BP}.tar.bz2 \
           file://avoid-redefining-strnlen-and-strndup.patch \
           file://sepbuildfix.patch \
"

SRC_URI[md5sum] = "ee16e5d33a3ea7506f38d71facc057dd"
SRC_URI[sha256sum] = "b3142b39601243b50532eec90f4a27dba85eb86f58d4b849ac94edeb29d9b22a"

inherit autotools
