SUMMARY = "functools.singledispatch from Python 3.4"
DESCRIPTION = "PEP 443 proposed to expose a mechanism in the functools standard library module \
in Python 3.4 that provides a simple form of generic programming known as single-dispatch \
generic functions.  This library is a backport of this functionality to Python 2.6 - 3.3"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=ee3cd67264adc7eb07981f3644dc17dc"

SRC_URI[md5sum] = "af2fc6a3d6cc5a02d0bf54d909785fcb"
SRC_URI[sha256sum] = "5b06af87df13818d14f08a028e42f566640aef80805c3b50c5056b086e3c2b9c"

inherit pypi setuptools
