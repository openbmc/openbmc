SUMMARY = "Never use print() to debug again; inspect variables, expressions, and program execution with a single, simple function call."
HOMEPAGE = "https://github.com/gruns/icecream"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eca5ce1610d64ed40920efdce85ff8d1"

SRC_URI[sha256sum] = "0aa4a7c3374ec36153a1d08f81e3080e83d8ac1eefd97d2f4fe9544e8f9b49de"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
        python3-asttokens \
        python3-colorama \
        python3-executing \
        python3-numbers \
        python3-pygments \
        python3-pprint \
"
