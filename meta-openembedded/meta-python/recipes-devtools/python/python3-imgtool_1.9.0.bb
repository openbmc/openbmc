SUMMARY = "MCUboot's image signing and key management tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://imgtool/main.py;beginline=3;endline=18;md5=9965f09ae52c04886a1f04e13ccdce57"

SRC_URI[sha256sum] = "7fa7f6bd6bf069b5ffa07ea47478e16b6206bd2fc5b7607def999de0e9134a43"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-cbor2 python3-click python3-cryptography python3-intelhex"

BBCLASSEXTEND = "native nativesdk"
