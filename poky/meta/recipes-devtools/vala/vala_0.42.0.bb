require ${BPN}.inc

SRC_URI += " file://0001-git-version-gen-don-t-append-dirty-if-we-re-not-in-g.patch \
             file://0001-vapigen.m4-use-PKG_CONFIG_SYSROOT_DIR.patch \
	     file://disable-graphviz.patch \
	     file://0001-Disable-valadoc.patch \
"

SRC_URI[md5sum] = "aa6eb8097d25b5847ad3fab34c0ff865"
SRC_URI[sha256sum] = "62a55986da23cf3aaafd7624c32db2a1af11c8419e0bb0751727d10f1f7ab7be"
