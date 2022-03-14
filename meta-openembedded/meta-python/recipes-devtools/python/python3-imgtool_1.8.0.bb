SUMMARY = "MCUboot's image signing and key management tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://imgtool/main.py;beginline=3;endline=18;md5=9965f09ae52c04886a1f04e13ccdce57"

SRC_URI += "file://cbor2.patch;striplevel=2"

SRC_URI[sha256sum] = "4858f5c5f48cfa4a0bb99e60d00aa291172a7ecc2afe4fb77ed96121f59b6e2f"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-cbor2 python3-click python3-cryptography python3-intelhex"

BBCLASSEXTEND = "native nativesdk"
