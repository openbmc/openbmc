require quagga.inc

SRC_URI += " \
            file://0001-ospf6d-check-ospf6-before-using-it-in-ospf6_clean.patch \
"

SRC_URI[md5sum] = "e73d6e527fb80240f180de420cfe8042"
SRC_URI[sha256sum] = "21ffb7bad0ef5f130f18dd299d219ea1cb4f5c03d473b6b32c83c340cd853263"

QUAGGASUBDIR = ""
