SUMMARY = "MCUboot's image signing and key management tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://imgtool/main.py;beginline=3;endline=18;md5=0645fb61e2f961a110302fbfdb227446"

SRC_URI[sha256sum] = "226b5029beb0cdb490095edd59ccfa32edfa7b31450c2683b44b1abb772e80d9"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-cbor2 python3-click python3-cryptography python3-intelhex"

BBCLASSEXTEND = "native nativesdk"
