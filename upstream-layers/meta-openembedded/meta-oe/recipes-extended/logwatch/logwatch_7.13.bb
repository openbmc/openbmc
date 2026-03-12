SUMMARY = "A log file analysis program"
DESCRIPTION = "\
Logwatch is a customizable, pluggable log-monitoring system. It will go \
through your logs for a given period of time and make a report in the areas \
that you wish with the detail that you wish. Easy to use - works right out of \
the package on many systems.\
"
SECTION = "devel"
HOMEPAGE = "http://www.logwatch.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ba882fa9b4b6b217a51780be3f4db9c8"
RDEPENDS:${PN} = "perl"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "0c9a10c2d8e5bc0cb10e16dc86c83be60d71d8a52b97bca785c64a30ed642839"

do_install() {
    install -m 0755 -d ${D}${sysconfdir}/logwatch/scripts/services
    install -m 0755 -d ${D}${datadir}/logwatch/dist.conf/logfiles
    install -m 0755 -d ${D}${datadir}/logwatch/dist.conf/services
    install -m 0755 -d ${D}${localstatedir}/cache/logwatch
    cp -r conf/ ${D}${datadir}/logwatch/default.conf
    cp -r scripts/ ${D}${datadir}/logwatch/scripts
    cp -r lib ${D}${datadir}/logwatch/lib
    chown -R root:root ${D}${datadir}/logwatch

    install -m 0755 -d ${D}${mandir}/man1
    install -m 0755 -d ${D}${mandir}/man5
    install -m 0755 -d ${D}${mandir}/man8
    install -m 0644 amavis-logwatch.1 ${D}${mandir}/man1
    install -m 0644 postfix-logwatch.1 ${D}${mandir}/man1
    install -m 0644 ignore.conf.5 ${D}${mandir}/man5
    install -m 0644 override.conf.5 ${D}${mandir}/man5
    install -m 0644 logwatch.conf.5 ${D}${mandir}/man5
    install -m 0644 logwatch.8 ${D}${mandir}/man8

    install -m 0755 -d ${D}${sysconfdir}/cron.daily
    install -m 0755 -d ${D}${sbindir}
    ln -sr  ${D}${datadir}/logwatch/scripts/logwatch.pl ${D}${sbindir}/logwatch
    cat > ${D}${sysconfdir}/cron.daily/0logwatch <<EOF
    DailyReport=\`grep -e "^[[:space:]]*DailyReport[[:space:]]*=[[:space:]]*" /usr/share/logwatch/default.conf/logwatch.conf | head -n1 | sed -e "s|^\s*DailyReport\s*=\s*||"\`
    if [ "\$DailyReport" != "No" ] && [ "\$DailyReport" != "no" ]
    then
            logwatch
    fi
EOF
    chmod 755 ${D}${sysconfdir}/cron.daily/0logwatch

    for i in scripts/logfiles/*; do
        if [ $(ls $i | wc -l) -ne 0 ]; then
            install -d ${D}${datadir}/logwatch/$i
            install -m 0644 $i/* ${D}${datadir}/logwatch/$i
        fi
    done

    install -m 0755 -d ${D}${sysconfdir}/logwatch/conf/logfiles
    install -m 0755 -d ${D}${sysconfdir}/logwatch/conf/services
    touch ${D}${sysconfdir}/logwatch/conf/logwatch.conf
    touch ${D}${sysconfdir}/logwatch/conf/ignore.conf
    touch ${D}${sysconfdir}/logwatch/conf/override.conf
    echo "# Local configuration options go here (defaults are in /usr/share/logwatch/default.conf/logwatch.conf)" > ${D}${sysconfdir}/logwatch/conf/logwatch.conf
    echo "###### REGULAR EXPRESSIONS IN THIS FILE WILL BE TRIMMED FROM REPORT OUTPUT #####" > ${D}${sysconfdir}/logwatch/conf/ignore.conf
    echo "# Configuration overrides for specific logfiles/services may be placed here." > ${D}${sysconfdir}/logwatch/conf/override.conf
}
