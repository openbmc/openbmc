require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "b79bbaf8929ec8ed35911b3571f5a248"
SRC_URI[sha256sum] = "4bb9b60fc0230b0db2c8a0e2a80ec29f1c10b43dc78355abba78adedbc2e03a1"
