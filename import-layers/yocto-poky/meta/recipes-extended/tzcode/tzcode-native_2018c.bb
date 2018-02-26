# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "e6e0d4b2ce3fa6906f303157bed2612e"
SRC_URI[tzcode.sha256sum] = "31fa7fc0f94a6ff2d6bc878c0a35e8ab8b5aa0e8b01445a1d4a8f14777d0e665"
SRC_URI[tzdata.md5sum] = "c412b1531adef1be7a645ab734f86acc"
SRC_URI[tzdata.sha256sum] = "2825c3e4b7ef520f24d393bcc02942f9762ffd3e7fc9b23850789ed8f22933f6"

S = "${WORKDIR}"

inherit native

EXTRA_OEMAKE += "cc='${CC}'"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
