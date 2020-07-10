SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=ee7987010dadc17745d623f406b500ec"

inherit pypi setuptools3

SRC_URI[md5sum] = "a991f73b0ccfe8f6e9a99bd1bc9071b1"
SRC_URI[sha256sum] = "e9b64e609d37438f7d6e68c2546d2cb8062f3adb27e6336bc129b51be20773ac"

BBCLASSEXTEND = "native nativesdk"
