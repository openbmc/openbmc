require libgpiod.inc

PACKAGECONFIG ?= ""

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"

SRC_URI[md5sum] = "2aa1e1a80c3c919ae142ab9a55fb59ca"
SRC_URI[sha256sum] = "b773e557af1a497f786825462a776b7bf90168e67ee0a5bc5d2473a5674dc38c"
