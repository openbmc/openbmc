# Copyright (C) 2013 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library of utility functions from BSD systems"
DESCRIPTION = "This library provides useful functions commonly found on BSD systems, \
               and lacking on others like GNU systems, thus making it easier to port \
               projects with strong BSD origins, without needing to embed the same \
               code over and over again on each project."

HOMEPAGE = "https://libbsd.freedesktop.org/wiki/"
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
LICENSE = "BSD-3-Clause & BSD-4-Clause & ISC & PD"
LICENSE:${PN} = "BSD-3-Clause & ISC & PD"
LICENSE:${PN}-dbg = "BSD-3-Clause & ISC & PD"
LICENSE:${PN}-dev = "BSD-3-Clause & ISC & PD"
LICENSE:${PN}-doc = "BSD-3-Clause & BSD-4-Clause & ISC & PD"
LICENSE:${PN}-locale = "BSD-3-Clause & ISC & PD"
LICENSE:${PN}-src = "BSD-3-Clause & ISC & PD"
LICENSE:${PN}-staticdev = "BSD-3-Clause & ISC & PD"

LIC_FILES_CHKSUM = "file://COPYING;md5=75e85c5a558f86d10fbd5abc567ea5e6"
SECTION = "libs"

SRC_URI = "https://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://0001-funopen-Replace-off64_t-with-off_t-in-funopen_seek.patch \
           "

SRC_URI[sha256sum] = "9baa186059ebbf25c06308e9f991fda31f7183c0f24931826d83aa6abd8a0261"

inherit autotools pkgconfig

DEPENDS += "libmd"

BBCLASSEXTEND = "native nativesdk"
