RDEPENDS_packagegroup-meta-oe-devtools += "\
    python3-distutils-extra \
    rwmem \
    speedtest-cli \
    mongodb \
"

RDEPENDS_packagegroup-meta-oe-connectivity += "\
    lirc \
"

RDEPENDS_packagegroup-meta-oe-extended += "\
    lcdproc \
    mozjs \
"
RDEPENDS_packagegroup-meta-oe-support += "\
    smem \
"
RDEPENDS_packagegroup-meta-oe-extended_remove_libc-musl = "lcdproc"

