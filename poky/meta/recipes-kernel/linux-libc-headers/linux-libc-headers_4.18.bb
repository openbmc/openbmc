require linux-libc-headers.inc

SRC_URI_append_libc-musl = "\
    file://0001-libc-compat.h-fix-some-issues-arising-from-in6.h.patch \
    file://0002-libc-compat.h-prevent-redefinition-of-struct-ethhdr.patch \
    file://0003-remove-inclusion-of-sysinfo.h-in-kernel.h.patch \
    file://0001-libc-compat.h-musl-_does_-define-IFF_LOWER_UP-DORMAN.patch \
    file://0001-if_ether-move-muslc-ethhdr-protection-to-uapi-file.patch \
    file://0001-include-linux-stddef.h-in-swab.h-uapi-header.patch \
   "

SRC_URI[md5sum] = "bee5fe53ee1c3142b8f0c12c0d3348f9"
SRC_URI[sha256sum] = "19d8bcf49ef530cd4e364a45b4a22fa70714b70349c8100e7308488e26f1eaf1"
