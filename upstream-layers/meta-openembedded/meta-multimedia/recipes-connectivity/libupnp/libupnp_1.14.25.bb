SUMMARY = "Portable SDK for UPnP* Devices"
DESCRIPTION = "The Portable SDK for UPnP Devices is an SDK for development of \
UPnP device and control point applications. It consists of the core UPnP \
protocols along with a UPnP-specific eXtensible Markup Language (XML) parser \
supporting the Document Object Model (DOM) Level 2 API and an optional, \
integrated mini web server for serving UPnP related documents."
HOMEPAGE = "http://pupnp.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=394a0f17b97f33426275571e15920434"

SRCREV = "da7cf950f22357545061961b7e5610e08690b0cf"
SRC_URI = "git://github.com/pupnp/pupnp.git;protocol=https;branch=branch-1.14.x;tag=release-${PV} \
          "


inherit autotools pkgconfig

EXTRA_OECONF += "--enable-reuseaddr"

# Enable LFS support ( for samples )
CFLAGS += "-D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 -D_GNU_SOURCE"
CXXFLAGS += "-D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 -D_GNU_SOURCE"
