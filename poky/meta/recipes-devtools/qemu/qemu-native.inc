inherit native

require qemu.inc

SRC_URI_append = " \
            file://0012-fix-libcap-header-issue-on-some-distro.patch \
            file://0013-cpus.c-Add-error-messages-when-qemi_cpu_kick_thread-.patch \
            "

EXTRA_OEMAKE_append = " LD='${LD}' AR='${AR}' OBJCOPY='${OBJCOPY}' LDFLAGS='${LDFLAGS}'"

LDFLAGS_append = " -fuse-ld=bfd"

do_install_append() {
     ${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}
