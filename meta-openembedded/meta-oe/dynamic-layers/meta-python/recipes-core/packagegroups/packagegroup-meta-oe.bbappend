RDEPENDS:packagegroup-meta-oe-devtools += "\
    python3-distutils-extra \
    rwmem \
"

RDEPENDS:packagegroup-meta-oe-connectivity += "\
    lirc \
"

RDEPENDS:packagegroup-meta-oe-extended += "\
    lcdproc \
"
RDEPENDS:packagegroup-meta-oe-support += "\
    nvmetcli \
    smem \
"
RDEPENDS:packagegroup-meta-oe-extended:remove:libc-musl = "lcdproc"

