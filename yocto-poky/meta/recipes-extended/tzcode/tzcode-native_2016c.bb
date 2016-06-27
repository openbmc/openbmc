# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=76ae2becfcb9a685041c6f166b44c2c2"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata"

SRC_URI[tzcode.md5sum] = "ffb82ab0b588138759902b4627a6a80d"
SRC_URI[tzcode.sha256sum] = "344b1bd486935bca2b7baa47db3b99b32211c45f31ec0d1ead8bacd103c5a416"
SRC_URI[tzdata.md5sum] = "0330ccd16140d3b6438a18dae9b34b93"
SRC_URI[tzdata.sha256sum] = "8700d981e6f2007ac037dabb5d2b12f390e8629bbc30e564bc21cf0c069a2d48"

S = "${WORKDIR}"

inherit native

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
