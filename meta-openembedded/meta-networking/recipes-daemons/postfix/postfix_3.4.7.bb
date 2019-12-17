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
SRC_URI[md5sum] = "b29ab85e8f6ef7fae132b004e777671b"
SRC_URI[sha256sum] = "fe3253121d3ba8836a23774225518560b35e40497951ad5bec154afa8205f967"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
