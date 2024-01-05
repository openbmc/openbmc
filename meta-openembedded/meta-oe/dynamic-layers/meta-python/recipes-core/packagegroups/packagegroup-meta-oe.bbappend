RDEPENDS:packagegroup-meta-oe-devtools += "\
    python3-distutils-extra \
    rwmem \
    mongodb \
"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv64 = "mongodb"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv32 = "mongodb"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mipsarch = "mongodb"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc = "mongodb"

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

