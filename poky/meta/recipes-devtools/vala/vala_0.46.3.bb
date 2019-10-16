require ${BPN}.inc

SRC_URI += "file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
           file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
           "

SRC_URI[md5sum] = "809ddac69b039ef509b61993c848613f"
SRC_URI[sha256sum] = "e29c2b1f108dc22c91bb501975a77c938aef079ca7875e1fbf41191e22cc57e3"
