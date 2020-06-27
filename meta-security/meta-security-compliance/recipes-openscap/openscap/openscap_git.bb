# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "NIST Certified SCAP 1.2 toolkit with OE changes"

include openscap.inc

SRCREV = "a85943eee400fdbe59234d1c4a02d8cf710c4625"
SRC_URI = "git://github.com/akuster/openscap.git;branch=oe-1.3 \
"

PV = "1.3.3+git${SRCPV}"
