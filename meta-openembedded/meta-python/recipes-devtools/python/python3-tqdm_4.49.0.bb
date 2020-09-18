SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[md5sum] = "c2afde3f1d5aa108376fdd0f4b20821d"
SRC_URI[sha256sum] = "faf9c671bd3fad5ebaeee366949d969dca2b2be32c872a7092a1e1a9048d105b"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
