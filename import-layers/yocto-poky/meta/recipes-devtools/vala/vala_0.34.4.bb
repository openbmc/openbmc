require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
"

SRC_URI[md5sum] = "a856989d749fc5e472a3592b96f9ca48"
SRC_URI[sha256sum] = "6b17bd339414563ebc51f64b0b837919ea7552d8a8ffa71cdc837d25c9696b83"
