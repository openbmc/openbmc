SUMMARY = "Natural Language Toolkit"
DESCRIPTION = "NLTK is a leading platform for building Python programs to work \
               with human language data. It provides easy-to-use interfaces to \
               over 50 corpora and lexical resources such as WordNet"
HOMEPAGE = "https://www.nltk.org"
BUGTRACKER = "https://github.com/nltk/nltk/issues"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

CVE_PRODUCT = "nltk"

RDEPENDS:${PN} = "\
    python3-click \
    python3-joblib \
    python3-tqdm \
    python3-regex \
    python3-xmlschema \
"

RRECOMMENDS:${PN} = "\
    python3-numpy \
"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "cb5945d6424a98d694c2b9a0264519fab4363711065a46aa0ae7a2195b92e71f"
