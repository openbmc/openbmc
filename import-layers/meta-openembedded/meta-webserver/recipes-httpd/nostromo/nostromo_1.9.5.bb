SUMMARY = "A simple, fast and secure HTTP server"
HOMEPAGE = "http://www.nazgul.ch/dev_nostromo.html"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/nhttpd/main.c;beginline=2;endline=14;md5=e5ec3fa723b29b7d59d205afd8d36938"

SRC_URI = "http://www.nazgul.ch/dev/${BPN}-${PV}.tar.gz \
           file://0001-GNUmakefile-add-possibility-to-override-variables.patch \
           file://nhttpd.conf \
           file://volatiles \
           file://tmpfiles.conf \
           file://nostromo \
"

SRC_URI[md5sum] = "dc6cfd6b5aae04c370c7f818fa7bde55"
SRC_URI[sha256sum] = "5f62578285e02449406b46cf06a7888fe3dc4a90bedf58cc18523bad62f6b914"

TARGET_CC_ARCH += "${LDFLAGS}"

DEPENDS = "openssl"

inherit update-rc.d useradd

INITSCRIPT_NAME = "nostromo"
INITSCRIPT_PARAMS = "defaults 70"

do_compile() {
    oe_runmake
}

# we need user/group www-data to exist when we install
#
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system -g www-data www-data"

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
