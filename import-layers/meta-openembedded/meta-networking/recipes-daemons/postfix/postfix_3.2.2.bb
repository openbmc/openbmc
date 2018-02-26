require postfix.inc

SRC_URI = "ftp://ftp.porcupine.org/mirrors/postfix-release/official/postfix-${PV}.tar.gz \
           file://makedefs.patch \
           file://install.patch \
           file://main.cf_2.0 \
           file://postfix \
           file://internal_recipient \
           file://postfix.service \
           file://aliasesdb \
           file://check_hostname.sh \
           file://0001-Check-for-glibc-before-setting-CANT_USE_SEND_RECV_MS.patch \
           file://0001-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
           file://postfix-install.patch \
           file://icu-config.patch \
           "
SRC_URI[md5sum] = "aea073a9b0bea5bdb590460a270a4aa0"
SRC_URI[sha256sum] = "d06849418d119d09366997b2b481bb23f737629769b4e4a52da42fb3ad8b0576"
