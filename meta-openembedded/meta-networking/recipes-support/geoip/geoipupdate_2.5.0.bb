SUMMARY = "Crontab entry to provide weekly updates of the GeoIP free databases."
DESCRIPTION = "update databases for GeoIP"

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "net"

DEPENDS = "zlib curl"

SRC_URI = "https://github.com/maxmind/geoipupdate/releases/download/v2.5.0/geoipupdate-2.5.0.tar.gz \
           file://GeoIP.conf \
           file://geoipupdate.cron \
          "
SRC_URI[md5sum] = "28f633c49ec87ab01ad3c0fb0228a696"
SRC_URI[sha256sum] = "5119fd0e338cd083e886228b26679c64bcbaade8a815be092aecf865a610ab26"

LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "\
file://ChangeLog.md;md5=11d2e31df0de2be3ccc3e2286c4dafcb \
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
    install ${WORKDIR}/GeoIP.conf ${D}/${sysconfdir}/
    install ${WORKDIR}/geoipupdate.cron ${D}/${sysconfdir}/cron.d/
}
