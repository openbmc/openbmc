SUMMARY = "Generic client/server library for SASL authentication"
SECTION = "libs"
HOMEPAGE = "http://asg.web.cmu.edu/sasl/"
DEPENDS = "openssl db groff-native"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"

SRCREV = "e41cfb986c1b1935770de554872247453fdbb079"

SRC_URI = "git://github.com/cyrusimap/cyrus-sasl;protocol=https;branch=master \
           file://avoid-to-call-AC_TRY_RUN.patch \
           file://Fix-hardcoded-libdir.patch \
           file://debian_patches_0014_avoid_pic_overwrite.diff \
           file://saslauthd.service \
           file://saslauthd.conf \
           file://0004-configure.ac-fix-condition-for-suppliment-snprintf-i.patch \
           file://0001-Allow-saslauthd-to-be-built-outside-of-source-tree-w.patch \
           file://0001-makeinit.sh-fix-parallel-build-issue.patch \
           file://CVE-2019-19906.patch \
           "

UPSTREAM_CHECK_URI = "https://github.com/cyrusimap/cyrus-sasl/archives"

S = "${WORKDIR}/git"

inherit autotools pkgconfig useradd systemd

EXTRA_OECONF += "--with-dblib=berkeley \
                 --with-plugindir='${libdir}/sasl2' \
                 andrew_cv_runpath_switch=none"

PACKAGECONFIG ??= "ntlm \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ldap pam', d)} \
"
PACKAGECONFIG[gssapi] = "--enable-gssapi=yes,--enable-gssapi=no,krb5,"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[opie] = "--with-opie,--without-opie,opie,"
PACKAGECONFIG[des] = "--with-des,--without-des,,"
PACKAGECONFIG[ldap] = "--with-ldap=${STAGING_LIBDIR} --enable-ldapdb,--without-ldap --disable-ldapdb,openldap,"
PACKAGECONFIG[ntlm] = "--enable-ntlm=yes,--enable-ntlm=no,,"

CFLAGS += "-fPIC"

do_configure_prepend () {
    # make it be able to work with db 5.0 version
    local sed_files="sasldb/db_berkeley.c utils/dbconverter-2.c"
    for sed_file in $sed_files; do
        sed -i 's#DB_VERSION_MAJOR == 4.*#(&) || DB_VERSION_MAJOR == 5#' ${S}/$sed_file
    done
}

do_compile_prepend () {
    cd include
    ${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} ${S}/include/makemd5.c -o makemd5
    touch makemd5.o makemd5.lo makemd5
    cd ..
}

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/saslauthd.service ${D}${systemd_unitdir}/system

        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/saslauthd.service
        sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${systemd_unitdir}/system/saslauthd.service
        sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_unitdir}/system/saslauthd.service

        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/saslauthd/ - - - -" > ${D}${sysconfdir}/tmpfiles.d/saslauthd.conf

        install -d ${D}${sysconfdir}/default/
        install -m 0644 ${WORKDIR}/saslauthd.conf ${D}${sysconfdir}/default/saslauthd
        sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${sysconfdir}/default/saslauthd
    fi
}

USERADD_PACKAGES = "${PN}-bin"
USERADD_PARAM_${PN}-bin = "--system --home=/var/spool/mail -g mail cyrus"

SYSTEMD_PACKAGES = "${PN}-bin"
SYSTEMD_SERVICE_${PN}-bin = "saslauthd.service"
SYSTEMD_AUTO_ENABLE = "disable"

SRC_URI[md5sum] = "a7f4e5e559a0e37b3ffc438c9456e425"
SRC_URI[sha256sum] = "8fbc5136512b59bb793657f36fadda6359cae3b08f01fd16b3d406f1345b7bc3"

PACKAGES =+ "${PN}-bin"

FILES_${PN}           += "${libdir}/sasl2/*.so*"
FILES_${PN}-bin       += "${bindir} \
                          ${sysconfdir}/default/saslauthd \
                          ${systemd_unitdir}/system/saslauthd.service \
                          ${sysconfdir}/tmpfiles.d/saslauthd.conf"
FILES_${PN}-dev       += "${libdir}/sasl2/*.la"
FILES_${PN}-dbg       += "${libdir}/sasl2/.debug"
FILES_${PN}-staticdev += "${libdir}/sasl2/*.a"

INSANE_SKIP_${PN} += "dev-so"

# CVE-2020-8032 affects only openSUSE
CVE_CHECK_WHITELIST += "CVE-2020-8032"
