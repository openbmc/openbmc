SUMMARY = "Crontab entry to provide weekly updates of the GeoIP free databases."
DESCRIPTION = "update databases for GeoIP"

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "net"

DEPENDS = "zlib curl"

SRC_URI = "https://github.com/maxmind/geoipupdate/releases/download/v2.4.0/geoipupdate-2.4.0.tar.gz \
           file://GeoIP.conf \
           file://geoipupdate.cron \
          "
SRC_URI[md5sum] = "02f9712fb80e8e979d3d54cda7f7704f"
SRC_URI[sha256sum] = "8b4e88ce8d84e9c75bc681704d19ec5c63c54f01e945f7669f97fb0df7e13952"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "\
file://ChangeLog.md;md5=334337b6ecbb65093bae66b3ae21c8c2 \
"
FILES_${PN} = "/usr/share/GeoIP \
              /etc/GeoIP.conf \
             /etc/cron.d/geoipupdate.cron \
             /usr/bin/geoipupdate \
"
inherit autotools

do_install_append() {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${sysconfdir}/cron.d
    install ${WORKDIR}/GeoIP.conf ${D}/${sysconfdir}/
    install ${WORKDIR}/geoipupdate.cron ${D}/${sysconfdir}/cron.d/
}
