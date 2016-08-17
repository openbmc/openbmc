SUMMARY = "Fast and portable XML parser and Jabber protocol library"
AUTHOR = "Gurer Ozen <meduketto at gmail.com>"
HOMEPAGE = "http://iksemel.googlecode.com"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"
DEPENDS = "gnutls"

PR = "r1"

# http://errors.yoctoproject.org/Errors/Details/25825/
PNBLACKLIST[iksemel] ?= "Not compatible with gnutls version 3.4 currently in oe-core"

SRC_URI = "http://iksemel.googlecode.com/files/${BP}.tar.gz;name=archive \
           file://r25.diff"
SRC_URI[archive.md5sum] = "532e77181694f87ad5eb59435d11c1ca"
SRC_URI[archive.sha256sum] = "458c1b8fb3349076a6cecf26c29db1d561315d84e16bfcfba419f327f502e244"

inherit autotools pkgconfig
