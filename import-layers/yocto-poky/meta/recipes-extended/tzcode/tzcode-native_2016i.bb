# note that we allow for us to use data later than our code version
#
SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"
LICENSE = "PD & BSD & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ef1a352b901ee7b75a75df8171d6aca7"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata"
UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "8fae14cba9396462955b7859cf04ba48"
SRC_URI[tzcode.sha256sum] = "411e8adcb6288b17d6c2624fde65e7d82654ca69b813ae121504ff66f0cfba7b"
SRC_URI[tzdata.md5sum] = "73912ecfa6a9a8048ddf2e719d9bc39d"
SRC_URI[tzdata.sha256sum] = "b6966ec982ef64fe48cebec437096b4f57f4287519ed32dde59c86d3a1853845"

S = "${WORKDIR}"

inherit native

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
