require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "d9af125648505503b139ebc2d2c9eee5"
SRC_URI[sha256sum] = "ef31649932872f094971d46453b21c60a41661670f98afa334062425b4aec47a"
