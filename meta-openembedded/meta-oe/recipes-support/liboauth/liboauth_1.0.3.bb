# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "C library implementing OAuth Core RFC 5849"
HOMEPAGE = "http://liboauth.sourceforge.net"
LICENSE = "MIT|GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=6266718a5241c045c8099d9be48817df \
                    file://COPYING.GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"
SECTION = "libs"
DEPENDS = "curl openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BP}.tar.gz \
           file://0001-Support-OpenSSL-1.1.0.patch \
           "
SRC_URI[md5sum] = "689b46c2b3ab1a39735ac33f714c4f7f"
SRC_URI[sha256sum] = "0df60157b052f0e774ade8a8bac59d6e8d4b464058cc55f9208d72e41156811f"

inherit autotools pkgconfig
