SUMMARY = "MCUboot's image signing and key management tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://imgtool/main.py;beginline=3;endline=18;md5=0645fb61e2f961a110302fbfdb227446"

SRC_URI[sha256sum] = "7a541255e69eec1f12aa38dce1f1d4fe20d8212677c68a9b6ec634ca91a06612"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-cbor2 python3-click python3-cryptography python3-intelhex"

BBCLASSEXTEND = "native nativesdk"
