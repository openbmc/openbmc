SUMMARY = "Generic client/server library for SASL authentication"
SECTION = "libs"
HOMEPAGE = "http://asg.web.cmu.edu/sasl/"
DEPENDS = "openssl db groff-native"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"

SRCREV = "7a6b45b177070198fed0682bea5fa87c18abb084"

SRC_URI = "git://github.com/cyrusimap/cyrus-sasl;protocol=https;branch=cyrus-sasl-2.1 \
           file://avoid-to-call-AC_TRY_RUN.patch \
           file://debian_patches_0014_avoid_pic_overwrite.diff \
           file://0001-sample-Rename-dprintf-to-cyrus_dprintf.patch \
           file://saslauthd.service \
           file://saslauthd.conf \
           file://CVE-2019-19906.patch \
	   file://CVE-2022-24407.patch \
           file://0001-Fix-time.h-check.patch \
           "

UPSTREAM_CHECK_URI = "https://github.com/cyrusimap/cyrus-sasl/archives"

S = "${WORKDIR}/git"

inherit autotools pkgconfig useradd systemd

EXTRA_OECONF += "--with-dblib=berkeley \
                 --with-plugindir='${libdir}/sasl2' \
                 andrew_cv_runpath_switch=none"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'ldap pam', d)} \
"
PACKAGECONFIG[gssapi] = "--enable-gssapi=yes,--enable-gssapi=no,krb5,"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[opie] = "--with-opie,--without-opie,opie,"
PACKAGECONFIG[des] = "--with-des,--without-des,,"
PACKAGECONFIG[ldap] = "--with-ldap=${STAGING_LIBDIR} --enable-ldapdb,--without-ldap --disable-ldapdb,openldap,"
PACKAGECONFIG[ntlm] = "--enable-ntlm=yes,--enable-ntlm=no,,"

CFLAGS += "-fPIC"

do_configure:prepend () {
    # make it be able to work with db 5.0 version
    local sed_files="sasldb/db_berkeley.c utils/dbconverter-2.c"
    for sed_file in $sed_files; do
        sed -i 's#DB_VERSION_MAJOR == 4.*#(&) || DB_VERSION_MAJOR == 5#' ${S}/$sed_file
    done
}

do_compile:prepend () {
    cd include
    ${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} ${S}/include/makemd5.c -o makemd5
    touch makemd5.o makemd5.lo makemd5
    cd ..
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/saslauthd.service ${D}${systemd_unitdir}/system

        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/saslauthd.service
        sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${systemd_unitdir}/system/saslauthd.service
        sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_unitdir}/system/saslauthd.service

        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/saslauthd/ - - - -" > ${D}${sysconfdir}/tmpfiles.d/saslauthd.conf

        install -d ${D}${sysconfdir}/default/
        install -m 0644 ${UNPACKDIR}/saslauthd.conf ${D}${sysconfdir}/default/saslauthd
        sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${sysconfdir}/default/saslauthd
    fi
}

USERADD_PACKAGES = "${PN}-bin"
GROUPADD_PARAM:${PN}-bin = "--system mail"
USERADD_PARAM:${PN}-bin = "--system --home=/var/spool/mail -g mail cyrus"

SYSTEMD_PACKAGES = "${PN}-bin"
SYSTEMD_SERVICE:${PN}-bin = "saslauthd.service"
SYSTEMD_AUTO_ENABLE = "disable"

SRC_URI[md5sum] = "a7f4e5e559a0e37b3ffc438c9456e425"
SRC_URI[sha256sum] = "8fbc5136512b59bb793657f36fadda6359cae3b08f01fd16b3d406f1345b7bc3"

PACKAGES =+ "${PN}-bin"

FILES:${PN}           += "${libdir}/sasl2/*.so*"
FILES:${PN}-bin       += "${bindir} \
                          ${sysconfdir}/default/saslauthd \
                          ${systemd_unitdir}/system/saslauthd.service \
                          ${sysconfdir}/tmpfiles.d/saslauthd.conf"
FILES:${PN}-dev       += "${libdir}/sasl2/*.la"
FILES:${PN}-dbg       += "${libdir}/sasl2/.debug"
FILES:${PN}-staticdev += "${libdir}/sasl2/*.a"

INSANE_SKIP:${PN} += "dev-so"
