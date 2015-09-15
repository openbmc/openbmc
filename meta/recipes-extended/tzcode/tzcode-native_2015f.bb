# note that we allow for us to use data later than our code version
#
DESCRIPTION = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD"

LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=d0ff93a73dd5bc3c6e724bb4343760f6"

SRC_URI =" ftp://ftp.iana.org/tz/releases/tzcode${PV}.tar.gz;name=tzcode \
           ftp://ftp.iana.org/tz/releases/tzdata2015f.tar.gz;name=tzdata"

SRC_URI[tzcode.md5sum] = "19578d432ba8b92f73406a17a9bc268d"
SRC_URI[tzcode.sha256sum] = "0c95e0a42bb61141f790f4f5f204b954d7654c894aa54a594a215d6f38de84ae"
SRC_URI[tzdata.md5sum] = "e3b82732d20e973e48af1c6f13df9a1d"
SRC_URI[tzdata.sha256sum] = "959f81b541e042ecb13c50097d264ae92ff03a57979c478dbcf24d5da242531d"

S = "${WORKDIR}"

inherit native

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
