SUMMARY = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74c98abe0de8f798ca609137f9cef4a"

inherit pypi setuptools3

SRC_URI[md5sum] = "06ca91d993de2d04c7ee6df0cbb44ea2"
SRC_URI[sha256sum] = "f82e61c7408ed0dce1862100db55595481911f159d6ddec0b375d35b6449509b"

# Work-around for broken make clean. Note this is fixed in v4.0.0.
# https://github.com/aio-libs/multidict/issues/194
CLEANBROKEN = "1"
