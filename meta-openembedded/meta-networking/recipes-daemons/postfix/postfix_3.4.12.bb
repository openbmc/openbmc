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
           "
SRC_URI[sha256sum] = "18555183ae8b52a9e76067799279c86f9f2770cdef3836deb8462ee0a0855dec"
UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
