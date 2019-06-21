require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "1e8b8595168446c529b11236cf75e328"
SRC_URI[sha256sum] = "bb8f8185b805411511786733c4b769c3ee6af8bc879609bffb6c46b8999bc27f"
