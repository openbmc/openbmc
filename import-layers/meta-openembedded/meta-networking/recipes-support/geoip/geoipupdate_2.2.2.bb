SUMMARY = "Crontab entry to provide weekly updates of the GeoIP free databases."
DESCRIPTION = "update databases for GeoIP"

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "net"

DEPENDS = "zlib curl"

SRC_URI = "https://github.com/maxmind/geoipupdate/releases/download/v2.2.2/geoipupdate-2.2.2.tar.gz \
           file://GeoIP.conf \
           file://geoipupdate.cron \
          "

SRC_URI[md5sum] = "06284bd7bcb298d078d794eb630dae55"
SRC_URI[sha256sum] = "156ab7604255a9c62c4a442c76d48d024ac813c6542639bffa93b28e2a781621"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "\
file://ChangeLog.md;md5=8ebf6f27a39125c3d600c90914b4034a \
"

inherit autotools

do_install_append() {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${sysconfdir}/cron.d
    install ${WORKDIR}/GeoIP.conf ${D}/${sysconfdir}/
    install ${WORKDIR}/geoipupdate.cron ${D}/${sysconfdir}/cron.d/
}
