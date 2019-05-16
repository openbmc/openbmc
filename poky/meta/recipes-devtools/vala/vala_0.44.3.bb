require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "25f97c1b46ae0b60b5cc49cbc044eca2"
SRC_URI[sha256sum] = "8553b422484af88be1685d8b47f7b0df36ae4477c3e77e89ab22276ffed1eae9"
