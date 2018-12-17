# Copyright (C) 2013 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library of utility functions from BSD systems"
DESCRIPTION = "This library provides useful functions commonly found on BSD systems, \
               and lacking on others like GNU systems, thus making it easier to port \
               projects with strong BSD origins, without needing to embed the same \
               code over and over again on each project."

HOMEPAGE = "http://libbsd.freedesktop.org/wiki/"
# There seems to be more licenses used in the code, I don't think we want to list them all here, complete list:
# OE @ ~/projects/libbsd $ grep ^License: COPYING  | sort -u
# License: BSD-2-clause
# License: BSD-2-clause-NetBSD
# License: BSD-2-clause-author
# License: BSD-2-clause-verbatim
# License: BSD-3-clause
# License: BSD-3-clause-author
# License: BSD-3-clause-John-Birrell
# License: BSD-3-clause-Regents
# License: BSD-4-clause-Christopher-G-Demetriou
# License: BSD-4-clause-Niels-Provos
# License: BSD-5-clause-Peter-Wemm
# License: Beerware
# License: Expat
# License: ISC
# License: ISC-Original
# License: public-domain
# License: public-domain-Colin-Plumb
LICENSE = "BSD-4-Clause & ISC & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b552602fda69e34c753d26de383f33c5"
SECTION = "libs"

SRC_URI = " \
    http://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
    file://0001-flopen-Add-missing-fcntl.h-include.patch \
"

SRC_URI[md5sum] = "a74b80c4143afa032c90226a4518fffe"
SRC_URI[sha256sum] = "56d835742327d69faccd16955a60b6dcf30684a8da518c4eca0ac713b9e0a7a4"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
