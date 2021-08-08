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
           file://0007-correct-signature-of-closefrom-API.patch \
           "
SRC_URI[sha256sum] = "507323d20d7b3f705f49cf8c07d437c6d8090bed07e15a3c0ec405edad54a7d4"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.6(\.\d+)+).tar.gz"
