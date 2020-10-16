SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[md5sum] = "2e56a15d28aa1fa06eeb31031c16c1f3"
SRC_URI[sha256sum] = "69dfa6714dee976e2425a9aab84b622675b7b1742873041e3db8a8e86132a4af"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
