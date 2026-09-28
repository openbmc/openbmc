SUMMARY = "The GNU oSIP library is an implementation of SIP - rfc3261"
DESCRIPTION = "SIP stands for the Session Initiation Protocol and is described \
by the rfc3261 (wich deprecates rfc2543). This library aims to provide multimedia \
and telecom software developers an easy and powerful interface to initiate and \
control SIP based sessions in their applications. SIP is a open standard \
replacement from IETF for H323."
HOMEPAGE = "http://www.gnu.org/software/osip/osip.html"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://src/osip2/osip.c;beginline=1;endline=19;md5=22ca0da0e41276c50c81b733953c208d"

SRC_URI = "${GNU_MIRROR}/osip/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "16186f6f5540936b62c3aaca6e8409e1af25cd22abc3882b393be215f49d3b00"

inherit autotools
