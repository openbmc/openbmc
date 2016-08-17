SUMMARY = "Crontab entry to provide weekly updates of the GeoIP free databases."
DESCRIPTION = "update databases for GeoIP"

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "net"

DEPENDS = "zlib curl"

SRC_URI = "https://github.com/maxmind/geoipupdate/releases/download/v2.2.1/geoipupdate-2.2.1.tar.gz \
           file://GeoIP.conf \
           file://geoipupdate.cron \
          "

SRC_URI[md5sum] = "abfd4bb9dd7fd489c103888dde5f2a57"
SRC_URI[sha256sum] = "9547c42cc8620b2c3040fd8df95e8ee45c8b6ebcca7737d641f9526104d5f446"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "\
file://ChangeLog.md;md5=5ca3d911ed549b0756b03410ff77fa62 \
"

inherit autotools

do_install_append() {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${sysconfdir}/cron.d
    install ${WORKDIR}/GeoIP.conf ${D}/${sysconfdir}/
    install ${WORKDIR}/geoipupdate.cron ${D}/${sysconfdir}/cron.d/
}
