require qemu.inc

inherit native

EXTRA_OEMAKE:append = " LD='${LD}' AR='${AR}' OBJCOPY='${OBJCOPY}' LDFLAGS='${LDFLAGS}'"

LDFLAGS:append = " -fuse-ld=bfd"

do_install:append() {
     ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}
