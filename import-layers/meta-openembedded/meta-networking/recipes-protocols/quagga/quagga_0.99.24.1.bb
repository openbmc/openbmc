require quagga.inc

SRC_URI += "file://babel-close-the-stdout-stderr-as-in-other-daemons.patch \
            file://0001-ospf6d-check-ospf6-before-using-it-in-ospf6_clean.patch \
"

SRC_URI[md5sum] = "7986bdc2fe6027d4c9216f7f5791e718"
SRC_URI[sha256sum] = "84ae1a47df085119a8fcab6c43ccea9efb9bc3112388b1dece5a9f0a0262754f"

QUAGGASUBDIR = ""
