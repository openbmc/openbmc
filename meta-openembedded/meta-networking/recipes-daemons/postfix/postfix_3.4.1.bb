require postfix.inc

SRC_URI += "file://0001-makedefs-Use-native-compiler-to-build-makedefs.test.patch \
            file://postfix-install.patch \
            file://icu-config.patch \
            file://0001-makedefs-add-lnsl-and-lresolv-to-SYSLIBS-by-default.patch \
            file://linux_5.x.patch \
           "
SRC_URI[md5sum] = "d292bb49a1c79ff6d2eb9c5e88c51425"
SRC_URI[sha256sum] = "8c9763f7a8ec70d499257b7f25bb50e1f3da8a4c43e59826ba8a26c4a778d0ce"

UPSTREAM_CHECK_REGEX = "postfix\-(?P<pver>3\.3(\.\d+)+).tar.gz"
