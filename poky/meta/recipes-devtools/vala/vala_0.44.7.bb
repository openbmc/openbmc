require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "27fd30535c51af5b87b0e7ffdbd906ef"
SRC_URI[sha256sum] = "bf1ff4f59d5de2d626e98e98ef81cb75dc1e6a27610a7de4133597c430f1bd7c"
