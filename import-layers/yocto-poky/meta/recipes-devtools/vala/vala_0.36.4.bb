require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
"

SRC_URI[md5sum] = "3c19014093f1a3d995357253b463082c"
SRC_URI[sha256sum] = "e9f23ce711c1a72ce664d10946fbc5953f01b0b7f2a3562e7a01e362d86de059"
