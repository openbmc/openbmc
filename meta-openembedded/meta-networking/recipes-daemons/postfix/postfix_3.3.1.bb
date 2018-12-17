require postfix.inc

SRC_URI += "file://0001-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
            file://postfix-install.patch \
            file://icu-config.patch \
            file://0001-makedefs-add-lnsl-and-lresolv-to-SYSLIBS-by-default.patch \
           "

SRC_URI[md5sum] = "4381c6492f415e4a69cf5099d4acea76"
SRC_URI[sha256sum] = "54f514dae42b5275cb4bc9c69283f16c06200b71813d0bb696568c4ba7ae7e3b"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
