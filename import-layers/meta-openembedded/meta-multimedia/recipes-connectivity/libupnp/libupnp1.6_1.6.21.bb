SUMMARY = "Portable SDK for UPnP* Devices"
DESCRIPTION = "The Portable SDK for UPnP Devices is an SDK for development of \
UPnP device and control point applications. It consists of the core UPnP \
protocols along with a UPnP-specific eXtensible Markup Language (XML) parser \
supporting the Document Object Model (DOM) Level 2 API and an optional, \
integrated mini web server for serving UPnP related documents."
HOMEPAGE = "http://pupnp.sourceforge.net/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3190d5244e08e78e4c8ee78544f4863"

SRC_URI = "${SOURCEFORGE_MIRROR}/pupnp/libupnp-${PV}.tar.bz2 \
           file://sepbuildfix.patch \
"

SRC_URI[md5sum] = "513adadb07fa039a8aeb0ceb7b7b0f6e"
SRC_URI[sha256sum] = "af3f3c0846a1d75baeadae4aa5a2bda427567e2a1fb4559bf73ccff0a4f9a39b"

S = "${WORKDIR}/libupnp-${PV}"

inherit autotools
