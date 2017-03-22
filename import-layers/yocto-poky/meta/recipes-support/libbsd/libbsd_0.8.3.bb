# Copyright (C) 2013 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library of utility functions from BSD systems"
DESCRIPTION = "This library provides useful functions commonly found on BSD systems, \
               and lacking on others like GNU systems, thus making it easier to port \
               projects with strong BSD origins, without needing to embed the same \
               code over and over again on each project."

HOMEPAGE = "http://libbsd.freedesktop.org/wiki/"
# There seems to be more licenses used in the code, I don't think we want to list them all here, complete list:
# OE @ ~/projects/libbsd $ grep ^License: COPYING  | sort
# License: BSD-2-clause
# License: BSD-2-clause
# License: BSD-2-clause-NetBSD
# License: BSD-2-clause-author
# License: BSD-2-clause-verbatim
# License: BSD-3-clause
# License: BSD-3-clause
# License: BSD-3-clause
# License: BSD-3-clause-Peter-Wemm
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
LIC_FILES_CHKSUM = "file://COPYING;md5=0b9c89d861915b86655b96e5e32fa2aa"
SECTION = "libs"

SRC_URI = " \
    http://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
"

SRC_URI[md5sum] = "e935c1bb6cc98a4a43cb1da22795493a"
SRC_URI[sha256sum] = "934b634f4dfd865b6482650b8f522c70ae65c463529de8be907b53c89c3a34a8"

inherit autotools pkgconfig
