require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
	     file://disable-graphviz.patch \
	     file://0001-Disable-valadoc.patch \
"

SRC_URI[md5sum] = "37edd0467d056fd9e3937d0bbceda80b"
SRC_URI[sha256sum] = "2fa746b51cd66e43577d1da06a80b708c2875cadaafee77e9700ea35cf23882c"
