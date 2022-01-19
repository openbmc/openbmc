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
SRC_URI[sha256sum] = "0f1241d456a0158e0c418abf62c52c2ff83f8f1dcf2fbdd4c40765b67789b1bc"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.6(\.\d+)+).tar.gz"
