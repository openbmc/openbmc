require postfix.inc

SRC_URI += "ftp://ftp.porcupine.org/mirrors/postfix-release/official/postfix-${PV}.tar.gz \
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
           file://0006-makedefs-Account-for-linux-6.x-version.patch \
           "
SRC_URI[sha256sum] = "d22f3d37ef75613d5d573b56fc51ef097f2c0d0b0e407923711f71c1fb72911b"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.6(\.\d+)+).tar.gz"
