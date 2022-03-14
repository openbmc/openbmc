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
            file://0006-correct-signature-of-closefrom-API.patch \
           "
SRC_URI[sha256sum] = "8de0619dcf2fa7c215a80cf84b82ab71631d4d4722cba0949725ce3e18031d4e"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.6(\.\d+)+).tar.gz"
