SUMMARY = "Postfix Mail Transport Agent"
DESCRIPTION = "Postfix is Wietse Venema's mail server that started life at \
IBM research as an alternative to the widely-used Sendmail program. \
Postfix attempts to be fast, easy to administer, and secure. The outsidei \
has a definite Sendmail-ish flavor, but the inside is completely different."

HOMEPAGE= "http://www.postfix.org"
SECTION = "mail"
DEPENDS = "virtual/db icu libpcre openssl postfix-native \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'openldap', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', 'cyrus-sasl', '', d)} \
"
DEPENDS_class-native = "virtual/db-native icu-native openssl-native libpcre-native"

LICENSE = "IPL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=64375f37431336ea1b1b3005fe3fa354"

SRC_URI = "ftp://ftp.porcupine.org/mirrors/postfix-release/official/postfix-${PV}.tar.gz \
    file://makedefs.patch \
    file://install.patch \
    file://main.cf_2.0 \
    file://postfix \
    file://internal_recipient \
    file://postfix.service \
    file://aliasesdb \
    file://check_hostname.sh \
"

S = "${WORKDIR}/postfix-${PV}"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native"

inherit update-rc.d useradd update-alternatives systemd

INITSCRIPT_NAME = "postfix"
INITSCRIPT_PARAMS = "start 58 3 4 5 . stop 13 0 1 6 ."
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = \
"-d /var/spool/postfix -r -g postfix --shell /bin/false postfix; \
 -d /var/spool/vmail -r -g vmail --shell /bin/false vmail \
"
GROUPADD_PARAM_${PN} = "--system postfix;--system postdrop;--system vmail"

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
export CCARGS-ldap_class-native = ""
export AUXLIBS-ldap_class-native = ""

export CCARGS-nonis_libc-musl = "-DNO_NIS"
export CCARGS-nonis = ""

# SASL support -DUSE_LDAP_SASL -DUSE_SASL_AUTH
# current openldap didn't enable SASL
export CCARGS-sasl  = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', '-DUSE_SASL_AUTH -DUSE_CYRUS_SASL -I${STAGING_INCDIR}/sasl', '', d)}"
export AUXLIBS-sasl = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'sasl', '-lsasl2', '', d)}"
export CCARGS-sasl_class-native = ""
export AUXLIBS-sasl_class-native = ""

# PCRE, TLS support default
export CCARGS  = "${CFLAGS} -DHAS_PCRE -DUSE_TLS -I${STAGING_INCDIR}/openssl ${CCARGS-ldap} ${CCARGS-sasl} ${CCARGS-nonis}"
export AUXLIBS = "-lpcre -lssl -lcrypto ${AUXLIBS-sasl} ${AUXLIBS-ldap}"
export POSTCONF = "${STAGING_DIR_NATIVE}${sbindir_native}/postconf"

export CCARGS-nonis_libc-musl = "-DNO_NIS"
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
        export BUILD_SYSROOT_NSL_PATH="${STAGING_BASELIBDIR} \
                                       ${STAGING_LIBDIR}"
    else
        # native build
        export BUILD_SYSROOT="${STAGING_DIR_NATIVE}"

        # ubuntu x86 host: /lib/x86_64-linux-gnu(64) /lib/i386-linux-gnu (32)
        #             on 64 bits, 32 libs in i386-linux-gnu
        # let makedefs finds nsl and resolv libs, host CC will link
        # the correct libraries
        BUILD_SYSROOT_NSL_PATH="$(${CC} -print-search-dirs 2>/dev/null | \
                      sed -n '/^libraries: =/s/libraries: =//p' | \
                      sed -e 's/:/\n/g' | xargs -n1 readlink -f | \
                      grep -v 'gcc\|/[0-9.]\+$' | sort -u)"
        if [ -z "$BUILD_SYSROOT_NSL_PATH" ]; then
            BUILD_SYSROOT_NSL_PATH="/usr/lib64 /lib64 \
                                    /lib/x86_64-linux-gnu \
                                    /usr/lib   /lib \
                                    /lib/i386-linux-gnu"
        fi
        export BUILD_SYSROOT_NSL_PATH
    fi

    oe_runmake makefiles
    oe_runmake
}

do_install_prepend_class-native() {
    export POSTCONF="bin/postconf"
}

SYSTEMD_SERVICE_${PN} = "postfix.service"

do_install () {
    sh ./postfix-install 'install_root=${D}' \
        'config_directory=${sysconfdir}/postfix' \
        'daemon_directory=${libexecdir}' \
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
    mv ${D}${sysconfdir}/postfix/main.cf ${D}${sysconfdir}/postfix/sample-main.cf
    install -m 755 ${S}/bin/smtp-sink ${D}/${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 644 ${WORKDIR}/main.cf_2.0 ${D}${sysconfdir}/postfix/main.cf
    sed -i 's#@LIBEXECDIR@#${libexecdir}#' ${D}${sysconfdir}/postfix/main.cf

    install -m 755 ${WORKDIR}/check_hostname.sh ${D}${sbindir}/

    install -m 755 ${WORKDIR}/postfix ${D}${sysconfdir}/init.d/postfix
    install -m 644 ${WORKDIR}/internal_recipient ${D}${sysconfdir}/postfix/internal_recipient

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/postfix.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@LIBEXECDIR@#${libexecdir}#g' ${D}${systemd_unitdir}/system/postfix.service
    sed -i -e 's#@LOCALSTATEDIR@#${localstatedir}#g' ${D}${systemd_unitdir}/system/postfix.service
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/postfix.service

    install -m 0755 ${WORKDIR}/aliasesdb ${D}${libexecdir}

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
}

do_install_append_class-native() {
    ln -sf ../sbin/sendmail.postfix ${D}${bindir}/newaliases
    ln -sf ../sbin/sendmail.postfix ${D}${bindir}/mailq
}

do_install_append_class-target() {
    # Remove references to buildmachine paths in target makedefs.out
    sed -i 's:-fdebug-prefix-map[^ ]*::g; s:--sysroot=${STAGING_DIR_TARGET}::g' ${D}/etc/postfix/makedefs.out
}

NATIVE_INSTALL_WORKS = "1"

ALTERNATIVE_${PN} = "sendmail"
ALTERNATIVE_TARGET[sendmail] = "${sbindir}/sendmail.postfix"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_PRIORITY = "120"

ALTERNATIVE_${PN}-doc += "mailq.1 newaliases.1 sendmail.1"
ALTERNATIVE_LINK_NAME[mailq.1] = "${mandir}/man1/mailq.1"
ALTERNATIVE_LINK_NAME[newaliases.1] = "${mandir}/man1/newaliases.1"
ALTERNATIVE_LINK_NAME[sendmail.1] = "${mandir}/man1/sendmail.1"

pkg_postinst_${PN} () {
    if [ "x$D" = "x" ]; then
        touch /etc/aliases
        newaliases

        # generate virtual_alias, default is hash
        touch /etc/postfix/virtual_alias
        postmap /etc/postfix/virtual_alias
    else
        touch $D/etc/aliases
        newaliases -C $D/etc/postfix/main.cf -oA$D/etc/aliases
        touch $D/etc/postfix/virtual_alias
        postmap -c $D/etc/postfix $D/etc/postfix/virtual_alias

        if ${@'true' if 'linuxstdbase' in d.getVar('DISTROOVERRIDES', False) else 'false'}; then
            # /usr/lib/sendmial is required by LSB core test
            [ ! -L $D/usr/lib/sendmail ] && ln -sf ${sbindir}/sendmail $D/usr/lib/
        fi
    fi
}

# Exclude .debug directories from the main package
FILES_${PN} = "${sysconfdir} ${localstatedir} ${bindir}/* ${sbindir}/* \
               ${libexecdir}/* ${systemd_unitdir}/*"
FILES_${PN}-dbg += "${libexecdir}/.debug"
