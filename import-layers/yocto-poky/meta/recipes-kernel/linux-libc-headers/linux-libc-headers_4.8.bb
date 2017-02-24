require linux-libc-headers.inc

PV = "4.8"

SRC_URI_append_libc-musl = "\
    file://0001-libc-compat.h-fix-some-issues-arising-from-in6.h.patch \
    file://0002-libc-compat.h-prevent-redefinition-of-struct-ethhdr.patch \
    file://0003-remove-inclusion-of-sysinfo.h-in-kernel.h.patch \
   "

SRC_URI[md5sum] = "c1af0afbd3df35c1ccdc7a5118cd2d07"
SRC_URI[sha256sum] = "3e9150065f193d3d94bcf46a1fe9f033c7ef7122ab71d75a7fb5a2f0c9a7e11a"
