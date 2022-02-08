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
           file://0001-fix-build-with-glibc-2.34.patch \
           "
SRC_URI[sha256sum] = "1759e953bf7baccb533899845c17753bf57a99ebac9c21717626262966a122f9"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.4(\.\d+)+).tar.gz"
