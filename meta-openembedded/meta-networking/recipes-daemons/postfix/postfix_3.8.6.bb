SUMMARY = "Postfix Mail Transport Agent"
DESCRIPTION = "Postfix is Wietse Venema's mail server that started life at \
IBM research as an alternative to the widely-used Sendmail program. \
Postfix attempts to be fast, easy to administer, and secure. The outsidei \
has a definite Sendmail-ish flavor, but the inside is completely different."

HOMEPAGE= "http://www.postfix.org"
SECTION = "mail"
DEPENDS = "db icu libpcre libnsl2 m4-native openssl postfix-native \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'openldap', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', 'cyrus-sasl', '', d)} \
"

LICENSE = "IPL-1.0 | EPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b181651ad99a7dc4cc8c4ce2f491ed1a"

SRC_URI = "ftp://ftp.porcupine.org/mirrors/postfix-release/official/postfix-${PV}.tar.gz \
           file://main.cf \
           file://postfix \
           file://internal_recipient \
           file://postfix.service \
           file://aliasesdb \
           file://check_hostname.sh \
           file://0001-Fix-makedefs.patch \
           file://0002-Change-fixed-postconf-to-a-variable-for-cross-compil.patch \
           file://0003-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
           file://0004-Fix-icu-config.patch \
           file://0005-makedefs-add-lnsl-and-lresolv-to-SYSLIBS-by-default.patch \
           "

SRC_URI[sha256sum] = "4b6e17c826cc438cc3016a9c0a55ea7e77c6cbafba7dd57241d81b690b0e9774"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.8(\.\d+)+).tar.gz"

S = "${WORKDIR}/postfix-${PV}"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native"

inherit pkgconfig update-rc.d useradd update-alternatives systemd lib_package

INITSCRIPT_NAME = "postfix"
INITSCRIPT_PARAMS = "start 58 3 4 5 . stop 13 0 1 6 ."
USERADD_PACKAGES = "${PN}-bin"
USERADD_PARAM:${PN}-bin = \
"-d /var/spool/postfix -r -g postfix --shell /bin/false postfix; \
 -d /var/spool/vmail -r -g vmail --shell /bin/false vmail \
"
GROUPADD_PARAM:${PN}-bin = "--system postfix;--system postdrop;--system vmail"

export SYSLIBS = "${LDFLAGS}"

# CCARGS specifies includes, defines
# AUXLIBS specifies libraries
# Linux2/Linux3 has HAS_DB defined
# makedefs will make CC to be CC+CCARGS

# ldap support
export CCARGS-ldap  = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', '-DHAS_LDAP', '', d)}"
export AUXLIBS-ldap = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', '-lldap -llber', '', d)}"

# no native openldap
export CCARGS-ldap:class-native = ""
export AUXLIBS-ldap:class-native = ""

export CCARGS-nonis:libc-musl = "-DNO_NIS"
export CCARGS-nonis = ""

# SASL support -DUSE_LDAP_SASL -DUSE_SASL_AUTH
# current openldap didn't enable SASL
export CCARGS-sasl  = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', '-DUSE_SASL_AUTH -DUSE_CYRUS_SASL -I${STAGING_INCDIR}/sasl', '', d)}"
export AUXLIBS-sasl = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', '-lsasl2', '', d)}"
export CCARGS-sasl:class-native = ""
export AUXLIBS-sasl:class-native = ""

# PCRE, TLS support default
export CCARGS  = "${CFLAGS} -DHAS_PCRE -DUSE_TLS -I${STAGING_INCDIR}/openssl ${CCARGS-ldap} ${CCARGS-sasl} ${CCARGS-nonis}"
export AUXLIBS = "-lpcre -lssl -lcrypto ${AUXLIBS-sasl} ${AUXLIBS-ldap}"
export POSTCONF = "${STAGING_DIR_NATIVE}${sbindir_native}/postconf"

# OPT,DEBUG is aready in CFLAGS
# ignore the OPTS="CC=$CC" in Makefile it will not use the CC=$CC $CCARGS
EXTRA_OEMAKE += "OPT= DEBUG= OPTS= "

do_compile () {
    unset CFLAGS CPPFLAGS CXXFLAGS
    local native_build

    native_build="${@['0', '1'][bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d)]}"

    # if not native build, then pass SYSTEM and RELEASE to makedefs
    if [ "${native_build}" != "1" ]; then
        # uname -s for target
        SYSTEM="Linux"

        # uname -r, use 2.6 as bottomline, even target kernel ver > 2.6
        RELEASE="2.6.34"
        sed -i -e \
            "s:\$(SHELL) makedefs):\$(SHELL) makedefs $SYSTEM $RELEASE):" \
            ${S}/Makefile.in
        export BUILD_SYSROOT="${STAGING_DIR_HOST}"
    else
        # native build
        export BUILD_SYSROOT="${STAGING_DIR_NATIVE}"
    fi

    oe_runmake makefiles
    oe_runmake
}

do_install:prepend:class-native() {
    export POSTCONF="bin/postconf"
}

SYSTEMD_SERVICE:${PN} = "postfix.service"

do_install () {
    sh ./postfix-install 'install_root=${D}' \
        'config_directory=${sysconfdir}/postfix' \
        'daemon_directory=${libexecdir}/postfix' \
        'command_directory=${sbindir}' \
        'queue_directory=${localstatedir}/spool/postfix' \
        'sendmail_path=${sbindir}/sendmail.postfix' \
        'newaliases_path=${bindir}/newaliases' \
        'mailq_path=${bindir}/mailq' \
        'manpage_directory=${mandir}' \
        'readme_directory=${datadir}/doc/postfix' \
        'data_directory=${localstatedir}/lib/postfix' \
        -non-interactive
    rm -rf ${D}${localstatedir}/spool/postfix
    mv ${D}${sysconfdir}/postfix/main.cf ${D}${sysconfdir}/postfix/${MLPREFIX}sample-main.cf
    install -m 755 ${S}/bin/smtp-sink ${D}/${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 644 ${WORKDIR}/main.cf ${D}${sysconfdir}/postfix/main.cf
    sed -i 's#@LIBEXECDIR@#${libexecdir}#' ${D}${sysconfdir}/postfix/main.cf

    install -m 755 ${WORKDIR}/check_hostname.sh ${D}${sbindir}/

    install -m 755 ${WORKDIR}/postfix ${D}${sysconfdir}/init.d/postfix
    install -m 644 ${WORKDIR}/internal_recipient ${D}${sysconfdir}/postfix/internal_recipient

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/postfix.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@LIBEXECDIR@#${libexecdir}#g' ${D}${systemd_unitdir}/system/postfix.service
    sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${systemd_unitdir}/system/postfix.service
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/postfix.service

    install -m 0755 ${WORKDIR}/aliasesdb ${D}${libexecdir}/postfix

    install -m 770 -d ${D}${localstatedir}/spool/postfix
    chown postfix:postfix ${D}${localstatedir}/spool/postfix

    install -m 0755 -d ${D}${localstatedir}/lib/postfix
    chown postfix:nogroup ${D}${localstatedir}/lib/postfix
    install -m 0755 -d ${D}${localstatedir}/spool/postfix
    chown root:postfix ${D}${localstatedir}/spool/postfix
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/active
    chown postfix:root ${D}${localstatedir}/spool/postfix/active
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/bounce
    chown postfix:root ${D}${localstatedir}/spool/postfix/bounce
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/corrupt
    chown postfix:root ${D}${localstatedir}/spool/postfix/corrupt
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/defer
    chown postfix:root ${D}${localstatedir}/spool/postfix/defer
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/deferred
    chown postfix:root ${D}${localstatedir}/spool/postfix/deferred
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/flush
    chown postfix:root ${D}${localstatedir}/spool/postfix/flush
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/hold
    chown postfix:root ${D}${localstatedir}/spool/postfix/hold
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/incoming
    chown postfix:root ${D}${localstatedir}/spool/postfix/incoming
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/saved
    chown postfix:root ${D}${localstatedir}/spool/postfix/saved
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/trace
    chown postfix:root ${D}${localstatedir}/spool/postfix/trace
    install -m 0730 -d ${D}${localstatedir}/spool/postfix/maildrop
    chown postfix:postdrop ${D}${localstatedir}/spool/postfix/maildrop
    install -m 0755 -d ${D}${localstatedir}/spool/postfix/pid
    chown root:root ${D}${localstatedir}/spool/postfix/pid
    install -m 0700 -d ${D}${localstatedir}/spool/postfix/private
    chown postfix:root ${D}${localstatedir}/spool/postfix/private
    install -m 0710 -d ${D}${localstatedir}/spool/postfix/public
    chown postfix:postdrop ${D}${localstatedir}/spool/postfix/public
    install -m 0755 -d ${D}${localstatedir}/spool/vmail
    chown vmail:vmail ${D}${localstatedir}/spool/vmail

    chown :postdrop ${D}${sbindir}/postqueue
    chown :postdrop ${D}${sbindir}/postdrop
    chmod g+s ${D}${sbindir}/postqueue
    chmod g+s ${D}${sbindir}/postdrop

    rm -rf ${D}/etc/postfix/makedefs.out
}

do_install:append:class-native() {
    ln -sf ../sbin/sendmail.postfix ${D}${bindir}/newaliases
    ln -sf ../sbin/sendmail.postfix ${D}${bindir}/mailq
}

ALTERNATIVE:${PN}-bin = "sendmail mailq newaliases"
# /usr/lib/sendmial is required by LSB core test
ALTERNATIVE:${PN}-bin:linuxstdbase = "sendmail mailq newaliases usr-lib-sendmail"
ALTERNATIVE_TARGET[mailq] = "${bindir}/mailq"
ALTERNATIVE_TARGET[newaliases] = "${bindir}/newaliases"
ALTERNATIVE_TARGET[sendmail] = "${sbindir}/sendmail.postfix"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_TARGET[usr-lib-sendmail] = "${sbindir}/sendmail.postfix"
ALTERNATIVE_LINK_NAME[usr-lib-sendmail] = "/usr/lib/sendmail"

ALTERNATIVE_PRIORITY = "120"

ALTERNATIVE:${PN}-doc += "mailq.1 newaliases.1 sendmail.1"
ALTERNATIVE_LINK_NAME[mailq.1] = "${mandir}/man1/mailq.1"
ALTERNATIVE_LINK_NAME[newaliases.1] = "${mandir}/man1/newaliases.1"
ALTERNATIVE_LINK_NAME[sendmail.1] = "${mandir}/man1/sendmail.1"

pkg_postinst_ontarget:${PN}-cfg () {
    touch /etc/aliases
    newaliases

    # generate virtual_alias, default is hash
    touch /etc/postfix/virtual_alias
    postmap /etc/postfix/virtual_alias
}

PACKAGES =+ "${PN}-cfg"
RDEPENDS:${PN}-cfg:class-target += "${PN}-bin"
RDEPENDS:${PN}:class-target += "${PN}-cfg"
# Exclude .debug directories from the main package
FILES:${PN}-bin += "${localstatedir} ${bindir}/* ${sbindir}/* \
               ${libexecdir}/* ${systemd_unitdir}/*"
FILES:${PN}-cfg = "${sysconfdir}"
FILES:${PN}-dbg += "${libexecdir}/postfix/.debug"
ALLOW_EMPTY:${PN} = "1"
