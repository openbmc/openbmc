require asio.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=fede5286a78559dd646e355ab0cc8f04"

SRC_URI[md5sum] = "85d014a356a6e004cd30ccd4c9b6a5c2"
SRC_URI[sha256sum] = "e0d71c40a7b1f6c1334008fb279e7361b32a063e020efd21e40d9d8ff037195e"

SRC_URI += "\
    file://0001-Automatically-handle-glibc-variant-of-strerror_r-wit.patch \
    file://0001-use-POSIX-poll.h-instead-of-sys-poll.h.patch \
"
