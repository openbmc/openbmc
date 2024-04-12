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

LIC_FILES_CHKSUM = "file://COPYING;md5=9b087a0981a1fcad42efbba6d4925a0f"
SECTION = "libs"

SRC_URI = "https://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz"

SRC_URI[sha256sum] = "b88cc9163d0c652aaf39a99991d974ddba1c3a9711db8f1b5838af2a14731014"

inherit autotools pkgconfig

DEPENDS += "libmd"

BBCLASSEXTEND = "native nativesdk"
