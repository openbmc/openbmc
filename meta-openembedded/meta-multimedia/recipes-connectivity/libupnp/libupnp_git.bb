SUMMARY = "Portable SDK for UPnP* Devices"
DESCRIPTION = "The Portable SDK for UPnP Devices is an SDK for development of \
UPnP device and control point applications. It consists of the core UPnP \
protocols along with a UPnP-specific eXtensible Markup Language (XML) parser \
supporting the Document Object Model (DOM) Level 2 API and an optional, \
integrated mini web server for serving UPnP related documents."
HOMEPAGE = "http://pupnp.sourceforge.net/"
LICENSE = "BSD"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b3190d5244e08e78e4c8ee78544f4863"

PV = "1.8.2+git${SRCPV}"
SRCREV = "56d6042abae861e8838a4e6b6b5b575b99e38f34"
SRC_URI = "git://github.com/mrjimenez/pupnp.git;protocol=https"

S="${WORKDIR}/git"

inherit autotools

EXTRA_OECONF += "--enable-reuseaddr"