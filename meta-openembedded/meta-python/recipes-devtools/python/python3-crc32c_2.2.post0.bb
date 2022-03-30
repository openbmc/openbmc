SUMMARY = "A python package implementing the crc32c algorithmin hardware and software"
HOMEPAGE = "https://github.com/ICRAR/crc32c"

LICENSE = "BSD-2-Clause & BSD-3-Clause & CRC32C-ADLER & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4fbd65380cdd255951079008b364516c \
    file://LICENSE.google-crc32c;md5=e9ed01b5e5ac9eae23fc2bb33701220c \
    file://LICENSE.slice-by-8;md5=6b3bc7709d6b2db6646ec2467310ff6b \
    file://crc32c_adler.c;startline=9;endline=24;md5=c60e6e55d0e5d95effa6fad27db0711a \
"

SRC_URI[sha256sum] = "3d058e7a5e37e4985d1a7ad4cb702bca56b490daa658d4851377d13ead8b435e"

inherit pypi setuptools3

do_compile:prepend() {
    if ! grep 'platform =' setup.cfg; then
        printf "[build_ext]\nplatform = ${TARGET_ARCH}" >> setup.cfg
    fi
}

RDEPENDS:${PN} += "python3-core"
