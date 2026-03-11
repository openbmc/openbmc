SUMMARY = "A python package implementing the crc32c algorithmin hardware and software"
HOMEPAGE = "https://github.com/ICRAR/crc32c"

LICENSE = "BSD-2-Clause & BSD-3-Clause & Zlib & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4fbd65380cdd255951079008b364516c \
    file://LICENSE.google-crc32c;md5=e9ed01b5e5ac9eae23fc2bb33701220c \
    file://LICENSE.slice-by-8;md5=6b3bc7709d6b2db6646ec2467310ff6b \
    file://crc32c_adler.c;beginline=9;endline=24;md5=9c8bd2afd2d340fd37c038759cd4eff8 \
"

SRC_URI[sha256sum] = "17ce6c596ad0d53df52dcd72defb66984aeabd98fbefea7ba848a6b6bdece36a"

inherit pypi setuptools3

do_compile:prepend() {
    if ! grep 'platform =' setup.cfg; then
        printf "[build_ext]\nplatform = ${TARGET_ARCH}" >> setup.cfg
    fi
}

RDEPENDS:${PN} += "python3-core"
