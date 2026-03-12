SUMMARY = "MCUboot's image signing and key management tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://imgtool/main.py;beginline=3;endline=18;md5=0645fb61e2f961a110302fbfdb227446"

SRC_URI[sha256sum] = "fff72e4e793ac0ec02a493c19548715ccc0a2358afaeeaa10b49ccc2e0bd1295"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-cbor2 python3-click python3-cryptography python3-intelhex"

BBCLASSEXTEND = "native nativesdk"
