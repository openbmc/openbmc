# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "b48f0282b80bb7dbe16e35626f446ae9"
SRC_URI[tzcode.sha256sum] = "aa53f4fb6570f02081be61dc11ade19ea5a280c23822a5b4016ce0c6be23c427"
SRC_URI[tzdata.md5sum] = "e71cb1f9d8d53c43904d79d7aeeedc1b"
SRC_URI[tzdata.sha256sum] = "02dfde534872f6513ae4553a3388fdae579441e31b862ea99170dfc447f46a16"

S = "${WORKDIR}"

inherit native

EXTRA_OEMAKE += "cc='${CC}'"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
