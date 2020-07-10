SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[md5sum] = "1594eab5b5aef37ef1fd6d36ce4f8f4c"
SRC_URI[sha256sum] = "63ef7a6d3eb39f80d6b36e4867566b3d8e5f1fe3d6cb50c5e9ede2b3198ba7b7"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
