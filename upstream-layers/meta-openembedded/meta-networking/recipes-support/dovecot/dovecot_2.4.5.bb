SUMMARY = "Dovecot is an open source IMAP and POP3 email server"
HOMEPAGE = "https://www.dovecot.org/"
DESCRIPTION = "Dovecot is an open source IMAP and POP3 email \
server for Linux/UNIX-like systems, written with security primarily \
in mind. Dovecot is an excellent choice for both small and large \
installations. It's fast, simple to set up, requires no special \
administration and it uses very little memory."
SECTION = "mail"
LICENSE = "BSD-3-Clause AND LGPL-2.1-only AND MIT AND Unicode-3.0 AND LicenseRef-PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=6c8f8f4ffb5ada82caf5dd7004cbfe95"

SRC_URI = "https://dovecot.org/releases/2.4/dovecot-${PV}.tar.gz \
           file://0001-configure.ac-convert-AC_TRY_RUN-to-AC_TRY_LINK-state.patch \
           file://dovecot.service \
           file://dovecot.socket \
           file://0001-m4-Check-for-libunwind-instead-of-libunwind-generic.patch \
           file://0001-adapt-lua_newstate-to-new-api.patch \
           "
SRC_URI[sha256sum] = "868c2686a61b5f8e00a3e4721789b1ab46e6528fd773a5fbed07a6ecba7731e6"

DEPENDS = "openssl xz zlib bzip2 libcap icu libtirpc bison-native"
CFLAGS += "-I${STAGING_INCDIR}/tirpc"
# clang 23 takes hours to compile the lib-json tests, drop when
# https://github.com/llvm/llvm-project/issues/221391 is fixed
CFLAGS:append:toolchain-clang = " -mllvm -enable-dfa-jump-thread=false"
LDFLAGS += "-ltirpc"

inherit autotools pkgconfig systemd useradd gettext multilib_header

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ldap pam systemd', d)}"

PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd,"
PACKAGECONFIG[ldap] = "--with-ldap=plugin,--without-ldap,openldap,"
PACKAGECONFIG[lua] = "--with-lua=yes, --without-lua, lua"
PACKAGECONFIG[lz4] = "--with-lz4,--without-lz4,lz4,"

# From native build in armv7a-hf/eglibc
CACHED_CONFIGUREVARS += "i_cv_signed_size_t=no \
                         i_cv_gmtime_max_time_t=32 \
                         i_cv_signed_time_t=yes \
                         i_cv_mmap_plays_with_write=yes \
                         i_cv_fd_passing=yes \
                         i_cv_c99_vsnprintf=yes \
                         lib_cv___va_copy=yes \
                         lib_cv_va_copy=yes \
                         lib_cv_va_val_copy=yes \
                        "

# hardcode epoll() to avoid running unsafe tests
# BSD needs kqueue and uclibc poll()
EXTRA_OECONF = " --with-ioloop=epoll"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "dovecot.service dovecot.socket"
SYSTEMD_AUTO_ENABLE = "disable"

do_install:append () {
    rm -rf ${D}${libdir}/dovecot/dovecot-config
    install -d 755 ${D}/etc/dovecot
    chmod 644 ${D}/etc/dovecot/dovecot.conf
    touch ${D}/etc/dovecot/ssl-key.pem
    touch ${D}/etc/dovecot/ssl-cert.pem
    chmod 600 ${D}/etc/dovecot/ssl-key.pem
    chmod 600 ${D}/etc/dovecot/ssl-cert.pem
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -m 0644 ${UNPACKDIR}/dovecot.service ${D}${systemd_unitdir}/system
        sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_unitdir}/system/dovecot.service
        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/dovecot.service
    fi
    oe_multilib_header dovecot/config.h
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -d ${libexecdir} -M -s ${base_sbindir}/nologin -g dovecot dovecot; \
                      -r -d ${libexecdir} -M -s ${base_sbindir}/nologin -g dovenull dovenull"
GROUPADD_PARAM:${PN} = "-f -r dovecot;-f -r dovenull"

FILES:${PN} += "${libdir}/dovecot/*plugin.so \
                ${libdir}/dovecot/libfs_compress.so \
                ${libdir}/dovecot/libssl_iostream_openssl.so"
FILES:${PN}-staticdev += "${libdir}/dovecot/*/*.a"
FILES:${PN}-dev += "${libdir}/dovecot/libdovecot*.so"
FILES:${PN}-dbg += "${libdir}/dovecot/*/.debug"

CVE_STATUS[CVE-2016-4983] = "not-applicable-platform: Affects only postinstall script on specific distribution."
CVE_STATUS[CVE-2025-59031] = "fixed-version: fixed since v2.4.2"
CVE_STATUS[CVE-2026-0394] = "fixed-version: fixed since v2.4.1"
CVE_STATUS[CVE-2026-24031] = "fixed-version: fixed since v2.4.3"
CVE_STATUS[CVE-2026-27855] = "fixed-version: fixed since v2.4.3"
CVE_STATUS[CVE-2026-27860] = "fixed-version: fixed since v2.4.3"
