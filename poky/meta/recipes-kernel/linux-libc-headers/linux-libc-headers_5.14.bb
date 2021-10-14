require linux-libc-headers.inc

SRC_URI:append:libc-musl = "\
    file://0001-libc-compat.h-fix-some-issues-arising-from-in6.h.patch \
    file://0003-remove-inclusion-of-sysinfo.h-in-kernel.h.patch \
    file://0001-libc-compat.h-musl-_does_-define-IFF_LOWER_UP-DORMAN.patch \
    file://0001-include-linux-stddef.h-in-swab.h-uapi-header.patch \
   "

SRC_URI:append = "\
    file://0001-scripts-Use-fixed-input-and-output-files-instead-of-.patch \
    file://0001-kbuild-install_headers.sh-Strip-_UAPI-from-if-define.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI[md5sum] = "a082ef5748b813abca0649dab8be5f52"
SRC_URI[sha256sum] = "7e068b5e0d26a62b10e5320b25dce57588cbbc6f781c090442138c9c9c3271b2"


