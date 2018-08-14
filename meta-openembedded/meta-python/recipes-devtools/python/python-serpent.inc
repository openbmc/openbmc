SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=acdea68f0e5a89eb6cbfdf54e38c356e"

SRC_URI[md5sum] = "d0c50f1943ff86d830eb3fe40f6dc1e3"
SRC_URI[sha256sum] = "b6855483d95a03b6aee358363684cd38a3a670240da5bc6da4253079cb07b8df"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    "    
