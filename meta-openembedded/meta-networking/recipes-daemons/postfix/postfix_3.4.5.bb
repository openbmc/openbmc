require postfix.inc

SRC_URI += "file://0001-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
            file://postfix-install.patch \
            file://icu-config.patch \
            file://0001-makedefs-add-lnsl-and-lresolv-to-SYSLIBS-by-default.patch \
           "
SRC_URI[md5sum] = "093109941095390562166de766d4720d"
SRC_URI[sha256sum] = "8b2ba54f9d2a049582a0ed3ee2dbe96ba57e278feea9cb4f80e1a61844e6319f"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
