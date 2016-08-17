SUMMARY = "A behavior-driven development framework, Python style"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d950439e8ea6ed233e4288f5e1a49c06"

SRC_URI[md5sum] = "3af4bc2886d2be982e41bfaba61da3bc"
SRC_URI[sha256sum] = "81b731ac5187e31e4aad2594944fa914943683a9818320846d037c5ebd6d5d0b"

RDEPENDS_${PN} += "python-difflib python-setuptools python-pkgutil python-misc python-argparse python-xml python-importlib python-enum34 python-parse python-parse-type python-six"

inherit pypi setuptools
