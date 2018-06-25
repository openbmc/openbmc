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
LIC_FILES_CHKSUM = "file://COPYING;md5=08fc4e66be4526715dab09c5fba5e9e8"
SECTION = "libs"

SRC_URI = " \
    http://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
    file://0001-src-libbsd-overlay.pc.in-Set-Cflags-to-use-I-instead.patch \
"
SRC_URI_append_libc-musl  = " \
    file://0001-Replace-__BEGIN_DECLS-and-__END_DECLS.patch \
    file://0002-Remove-funopen.patch \
"

SRC_URI[md5sum] = "d9e6980fbfe44f94fd92b89a33cce67d"
SRC_URI[sha256sum] = "f548f10e5af5a08b1e22889ce84315b1ebe41505b015c9596bad03fd13a12b31"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
