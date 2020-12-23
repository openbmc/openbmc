SUMMARY = "A simple, fast and secure HTTP server"
HOMEPAGE = "http://www.nazgul.ch/dev_nostromo.html"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/nhttpd/main.c;beginline=2;endline=14;md5=0bb3711a867b9704d3bfabcf5529b64e"

SRC_URI = "http://www.nazgul.ch/dev/${BPN}-${PV}.tar.gz \
           file://0001-GNUmakefile-add-possibility-to-override-variables.patch \
           file://nhttpd.conf \
           file://volatiles \
           file://tmpfiles.conf \
           file://nostromo \
"

SRC_URI[md5sum] = "a054eb8e09560082793aaad676e33bd3"
SRC_URI[sha256sum] = "1fad0a28d934835b582cedc98857e12e5adb991b14b87b52b39e6a501f4a9486"

TARGET_CC_ARCH += "${LDFLAGS}"

DEPENDS = "openssl groff-native base-passwd virtual/crypt"

inherit update-rc.d

INITSCRIPT_NAME = "nostromo"
INITSCRIPT_PARAMS = "defaults 70"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}/${sbindir}
    install -m 0755 src/nhttpd/nhttpd ${D}/${sbindir}/nhttpd
    install -m 0755 src/tools/crypt ${D}/${sbindir}/crypt
    install -d ${D}/${mandir}/man8
    install -m 0444 src/nhttpd/nhttpd.8 ${D}/${mandir}/man8/nhttpd.8
    install -d ${D}${localstatedir}/nostromo/conf
    install -d ${D}${localstatedir}/nostromo/htdocs/cgi-bin
    install -d ${D}${localstatedir}/nostromo/icons
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 conf/mimes ${D}${localstatedir}/nostromo/conf/mimes
    install -m 0644 ${WORKDIR}/nhttpd.conf ${D}${sysconfdir}
    install -m 0755 ${WORKDIR}/nostromo ${D}${sysconfdir}/init.d
    install -D -m 0644 ${WORKDIR}/volatiles ${D}${sysconfdir}/default/volatiles/nostromo
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0644 ${WORKDIR}/tmpfiles.conf ${D}${sysconfdir}/tmpfiles.d/nostromo.conf
    fi
    install -m 0644 htdocs/index.html ${D}${localstatedir}/nostromo/htdocs/index.html
    install -m 0644 htdocs/nostromo.gif ${D}${localstatedir}/nostromo/htdocs/nostromo.gif
    install -m 0644 icons/dir.gif ${D}${localstatedir}/nostromo/icons/dir.gif
    install -m 0644 icons/file.gif ${D}${localstatedir}/nostromo/icons/file.gif
    chown -R www-data:www-data ${D}/${localstatedir}/nostromo
}

CONFFILES_${PN} += "/var/nostromo/conf/mimes ${sysconfdir}/nhttpd.conf"

pkg_postinst_${PN} () {
    if [ -z "$D" ]; then
        if [ -e /sys/fs/cgroup/systemd ]; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/nostromo.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}
