# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ef1a352b901ee7b75a75df8171d6aca7"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata"
UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "f89867013676e3cb9544be2df7d36a91"
SRC_URI[tzcode.sha256sum] = "1ff90b47ad7986140a513b5287b1851c40f80fd44fd636db5cc5b46d06f9fa2b"
SRC_URI[tzdata.md5sum] = "3c7e97ec8527211104d27cc1d97a23de"
SRC_URI[tzdata.sha256sum] = "3c7137b2bc47323b0de47b77786bacf81ed503d4b2c693ff8ada2fbd1281ebd1"

S = "${WORKDIR}"

inherit native

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
