require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
"

SRC_URI[md5sum] = "755881770bffac020b5ea5f625fbe528"
SRC_URI[sha256sum] = "dd0d47e548a34cfb1e4b04149acd082a86414c49057ffb79902eb9a508a161a9"
