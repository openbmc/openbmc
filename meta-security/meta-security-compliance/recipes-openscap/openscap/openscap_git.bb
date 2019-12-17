# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "NIST Certified SCAP 1.2 toolkit with OE changes"

include openscap.inc

SRCREV = "4bbdb46ff651f809d5b38ca08d769790c4bfff90"
SRC_URI = "git://github.com/akuster/openscap.git;branch=oe-1.3 \
"

PV = "1.3.1+git${SRCPV}"
