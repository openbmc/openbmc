# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ef1a352b901ee7b75a75df8171d6aca7"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata"
UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "eef0bfac7a52dce6989a7d8b40d86fe0"
SRC_URI[tzcode.sha256sum] = "02f2c6b58b99edd0d47f0cad34075b359fd1a4dab71850f493b0404ded3b38ac"
SRC_URI[tzdata.md5sum] = "cb8274cd175f8a4d9d1b89895df876dc"
SRC_URI[tzdata.sha256sum] = "df3a5c4d0a2cf0cde0b3f35796ccf6c9acfd598b8e70f8dece5404cd7626bbd6"

S = "${WORKDIR}"

inherit native

EXTRA_OEMAKE += "cc=${CC}"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
