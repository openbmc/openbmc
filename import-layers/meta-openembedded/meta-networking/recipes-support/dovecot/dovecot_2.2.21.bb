SUMMARY = "Dovecot is an open source IMAP and POP3 email server"
DESCRIPTION = "Dovecot is an open source IMAP and POP3 email server for Linux/UNIX-like systems, written with security primarily in mind. Dovecot is an excellent choice for both small and large installations. It's fast, simple to set up, requires no special administration and it uses very little memory."
SECTION = "mail"
LICENSE = "LGPLv2.1 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a981379bd0f1c362f8d1d21515e5b30b"

SRC_URI = "http://dovecot.org/releases/2.2/dovecot-${PV}.tar.gz \
           file://0001-configure.ac-convert-AC_TRY_RUN-to-AC_TRY_LINK-state.patch \
           file://dovecot.service \
           file://dovecot.socket"

SRC_URI[md5sum] = "28c39ab78a20f00701c26960d9190cf0"
SRC_URI[sha256sum] = "7ab7139e59e1f0353bf9c24251f13c893cf1a6ef4bcc47e2d44de437108d0b20"

DEPENDS = "openssl xz zlib bzip2 libcap icu"

inherit autotools pkgconfig systemd useradd

PACKAGECONFIG ??= " \
                   ${@base_contains('DISTRO_FEATURES', 'ldap', 'ldap', '', d)} \
                   ${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                  "

PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[ldap] = "--with-ldap=plugin,--without-ldap,openldap,"
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
EXTRA_OECONF = " --with-ioloop=epoll \
                 --with-systemdsystemunitdir=${systemd_unitdir}/system"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "dovecot.service dovecot.socket"
SYSTEMD_AUTO_ENABLE = "disable"

do_install_append () {
    install -d 755 ${D}/etc/dovecot
    touch 644 ${D}/etc/dovecot/dovecot.conf
    install -m 0644 ${WORKDIR}/dovecot.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_unitdir}/system/dovecot.service
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/dovecot.service
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-r -d ${libexecdir} -M -s ${base_sbindir}/nologin -g dovecot dovecot; \
                      -r -d ${libexecdir} -M -s ${base_sbindir}/nologin -g dovenull dovenull"
GROUPADD_PARAM_${PN} = "-f -r dovecot;-f -r dovenull"

FILES_${PN} += "${libdir}/dovecot/*plugin.so \
                ${libdir}/dovecot/libfs_compress.so \
                ${libdir}/dovecot/libssl_iostream_openssl.so"
FILES_${PN}-staticdev += "${libdir}/dovecot/*/*.a"
FILES_${PN}-dev += "${libdir}/dovecot/libdovecot*.so"
FILES_${PN}-dbg += "${libdir}/dovecot/*/.debug"
