require linux-libc-headers.inc

SRC_URI_append_libc-musl = "\
    file://0001-libc-compat.h-fix-some-issues-arising-from-in6.h.patch \
    file://0002-libc-compat.h-prevent-redefinition-of-struct-ethhdr.patch \
    file://0003-remove-inclusion-of-sysinfo.h-in-kernel.h.patch \
    file://0001-libc-compat.h-musl-_does_-define-IFF_LOWER_UP-DORMAN.patch \
    file://0001-if_ether-move-muslc-ethhdr-protection-to-uapi-file.patch \
   "


SRC_URI[md5sum] = "8186ce63c489199b58b6a58ad2a24a94"
SRC_URI[sha256sum] = "cd44df4b23a3e0edc14be63df95d768b9600b31c35be05fb89f93226907fc8c6"
