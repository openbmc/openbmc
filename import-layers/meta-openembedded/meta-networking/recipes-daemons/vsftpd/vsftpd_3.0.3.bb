SUMMARY = "Very Secure FTP server"
HOMEPAGE = "https://security.appspot.com/vsftpd.html"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6067ad950b28336613aed9dd47b1271"

DEPENDS = "libcap openssl"

SRC_URI = "https://security.appspot.com/downloads/vsftpd-${PV}.tar.gz \
           file://makefile-destdir.patch \
           file://makefile-libs.patch \
           file://makefile-strip.patch \
           file://init \
           file://vsftpd.conf \
           file://vsftpd.user_list \
           file://vsftpd.ftpusers \
           file://change-secure_chroot_dir.patch \
           file://volatiles.99_vsftpd \
           file://vsftpd.service \
           file://vsftpd-2.1.0-filter.patch \
           file://0001-vsftpd-allow-sysinfo-in-the-seccomp-sandbox.patch \
           ${@bb.utils.contains('PACKAGECONFIG', 'tcp-wrappers', 'file://vsftpd-tcp_wrappers-support.patch', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '', '${NOPAM_SRC}', d)} \
           file://0001-sysdeputil.c-Fix-with-musl-which-does-not-have-utmpx.patch \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=a6067ad950b28336613aed9dd47b1271 \
                        file://COPYRIGHT;md5=04251b2eb0f298dae376d92454f6f72e \
                        file://LICENSE;md5=654df2042d44b8cac8a5654fc5be63eb"
SRC_URI[md5sum] = "da119d084bd3f98664636ea05b5bb398"
SRC_URI[sha256sum] = "9d4d2bf6e6e2884852ba4e69e157a2cecd68c5a7635d66a3a8cf8d898c955ef7"


PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = ",,tcp-wrappers"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
RDEPENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-listfile', '', d)}"
PAMLIB = "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '-L${STAGING_BASELIBDIR} -lpam', '', d)}"
WRAPLIB = "${@bb.utils.contains('PACKAGECONFIG', 'tcp-wrappers', '-lwrap', '', d)}"
NOPAM_SRC ="${@bb.utils.contains('PACKAGECONFIG', 'tcp-wrappers', 'file://nopam-with-tcp_wrappers.patch', 'file://nopam.patch', d)}"

inherit update-rc.d useradd systemd

CONFFILES_${PN} = "${sysconfdir}/vsftpd.conf"
LDFLAGS_append =" -lcrypt -lcap"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_configure() {
    # Fix hardcoded /usr, /etc, /var mess.
    cat tunables.c|sed s:\"/usr:\"${prefix}:g|sed s:\"/var:\"${localstatedir}:g \
    |sed s:\"/etc:\"${sysconfdir}:g > tunables.c.new
    mv tunables.c.new tunables.c
}

do_compile() {
   oe_runmake "LIBS=-L${STAGING_LIBDIR} -lcrypt -lcap ${PAMLIB} ${WRAPLIB}"
}

do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man8
    install -d ${D}${mandir}/man5
    oe_runmake 'DESTDIR=${D}' install
    install -d ${D}${sysconfdir}
    install -m 600 ${WORKDIR}/vsftpd.conf ${D}${sysconfdir}/vsftpd.conf
    install -d ${D}${sysconfdir}/init.d/
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/vsftpd
    install -d ${D}/${sysconfdir}/default/volatiles
    install -m 644 ${WORKDIR}/volatiles.99_vsftpd ${D}/${sysconfdir}/default/volatiles/99_vsftpd

    install -m 600 ${WORKDIR}/vsftpd.ftpusers ${D}${sysconfdir}/
    install -m 600 ${WORKDIR}/vsftpd.user_list ${D}${sysconfdir}/
    if ! test -z "${PAMLIB}" ; then
        install -d ${D}${sysconfdir}/pam.d/
        cp ${S}/RedHat/vsftpd.pam ${D}${sysconfdir}/pam.d/vsftpd
        sed -i "s:/lib/security:${base_libdir}/security:" ${D}${sysconfdir}/pam.d/vsftpd
        sed -i "s:ftpusers:vsftpd.ftpusers:" ${D}${sysconfdir}/pam.d/vsftpd
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/run/vsftpd/empty 0755 root root -" \
        > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi

    # Install systemd unit files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/vsftpd.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/vsftpd.service
}

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "vsftpd"
INITSCRIPT_PARAMS_${PN} = "defaults 80"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home-dir /var/lib/ftp --no-create-home -g ftp \
                       --shell /bin/false ftp "
GROUPADD_PARAM_${PN} = "-r ftp"

SYSTEMD_SERVICE_${PN} = "vsftpd.service"

pkg_postinst_${PN}() {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}
