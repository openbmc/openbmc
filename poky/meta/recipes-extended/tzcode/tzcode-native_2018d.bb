# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "96612b4f5d7e8804fd9a0981c021be90"
SRC_URI[tzcode.sha256sum] = "7de44e85baad748d217e3fd60706f599f9aec68bce6356b163f52b0dbd40a8d9"
SRC_URI[tzdata.md5sum] = "871a7ef808eb42ebc551acdb3d661554"
SRC_URI[tzdata.sha256sum] = "5106eddceb5f1ae3a91dbd3960e1b8b11ba0dc08579a31cf0724a7691b10c054"

S = "${WORKDIR}"

inherit native

EXTRA_OEMAKE += "cc='${CC}'"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
