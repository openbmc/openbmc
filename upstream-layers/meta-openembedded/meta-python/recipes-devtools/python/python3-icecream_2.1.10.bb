SUMMARY = "Never use print() to debug again; inspect variables, expressions, and program execution with a single, simple function call."
HOMEPAGE = "https://github.com/gruns/icecream"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eca5ce1610d64ed40920efdce85ff8d1"

SRC_URI[sha256sum] = "15900126ba7dbe1f83819583cbe5ff79a2943224600878d89307e4633b32e528"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
        python3-asttokens \
        python3-colorama \
        python3-executing \
        python3-numbers \
        python3-pygments \
        python3-pprint \
"
