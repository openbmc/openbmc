require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
	     file://disable-graphviz.patch \
	     file://0001-Disable-valadoc.patch \
"

SRC_URI[md5sum] = "d204eb4fa210995e731e2a9a01c8c772"
SRC_URI[sha256sum] = "8c33b4abc0573d364781bbfe54a1668ed34956902e471191a31cf05dc87c6e12"
