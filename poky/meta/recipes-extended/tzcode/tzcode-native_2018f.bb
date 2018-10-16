# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzdata.md5sum] = "e5e84f00f9d18bd6ebc8b1affec91b15"
SRC_URI[tzdata.sha256sum] = "0af6a85fc4ea95832f76524f35696a61abb3992fd3f8db33e5a1f95653e043f2"
SRC_URI[tzcode.md5sum] = "011d394b70e6ee3823fd77010b99737f"
SRC_URI[tzcode.sha256sum] = "4ec74f8a84372570135ea4be16a042442fafe100f5598cb1017bfd30af6aaa70"

S = "${WORKDIR}"

inherit native

EXTRA_OEMAKE += "cc='${CC}'"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
