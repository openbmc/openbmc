SUMMARY = "Crontab entry to provide weekly updates of the GeoIP free databases."
DESCRIPTION = "update databases for GeoIP"

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "net"

DEPENDS = "zlib curl"

SRC_URI = "https://github.com/maxmind/geoipupdate/releases/download/v3.1.1/geoipupdate-3.1.1.tar.gz \
           file://GeoIP.conf \
           file://geoipupdate.cron \
          "
SRC_URI[sha256sum] = "3de22e3fe3282024288a00807bbea9a1ffa2d1e8fe9c611f4b14a5b4d8ebe08a"

LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "\
file://ChangeLog.md;md5=30029632df335cb696f68ecc2a428987 \
"
FILES:${PN} = "/usr/share/GeoIP \
              /etc/GeoIP.conf \
             /etc/cron.d/geoipupdate.cron \
             /usr/bin/geoipupdate \
"
inherit autotools

do_install:append() {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${sysconfdir}/cron.d
    install ${UNPACKDIR}/GeoIP.conf ${D}/${sysconfdir}/
    install ${UNPACKDIR}/geoipupdate.cron ${D}/${sysconfdir}/cron.d/
}
