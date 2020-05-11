require postfix.inc

SRC_URI += "ftp://ftp.porcupine.org/mirrors/postfix-release/official/postfix-${PV}.tar.gz \
           file://makedefs.patch \
           file://install.patch \
           file://main.cf \
           file://postfix \
           file://internal_recipient \
           file://postfix.service \
           file://aliasesdb \
           file://check_hostname.sh \
           file://0001-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
           file://postfix-install.patch \
           file://icu-config.patch \
           file://0001-makedefs-add-lnsl-and-lresolv-to-SYSLIBS-by-default.patch \
           file://0001-Fixed-build-failure-with-glibc-2.30-due-to-dropped-R.patch \
           "
SRC_URI[md5sum] = "6944b946887077a28e3dcb375ff53701"
SRC_URI[sha256sum] = "8d5d429737e0c64514028a82fc006cbb273d2cb98dc40eb1dbbfe102f29a8943"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
