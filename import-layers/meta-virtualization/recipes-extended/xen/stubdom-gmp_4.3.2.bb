# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "GMP library for Xen vTPM's."
HOMEPAGE = "http://gmp"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

SRC_URI = "\
    https://gmplib.org/download/gmp/archive/gmp-${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "dd60683d7057917e34630b4a787932e8"
SRC_URI[sha256sum] = "936162c0312886c21581002b79932829aa048cfaf9937c6265aeaa14f1cd1775"

S="${WORKDIR}/gmp-${PV}"
B="${S}"

require stubdom-gmp.inc
