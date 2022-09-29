SUMMARY = "Library and tool for installing Python wheels"
HOMEPAGE = "https://gitlab.com/rossburton/picobuild"
LICENSE = "MIT & ( Apache-2.0 | BSD-2-Clause)"

LIC_FILES_CHKSUM = "file://LICENSE;md5=6c811a9fbdf5641ff0b0d43fbbb440e5 \
                    file://picobuild/vendored/packaging/LICENSE.APACHE;md5=2ee41112a44fe7014dce33e26468ba93 \
                    file://picobuild/vendored/packaging/LICENSE.BSD;md5=7bef9bf4a8e4263634d0597e7ba100b8 \
                    file://picobuild/vendored/pep517/LICENSE;md5=aad69c93f605003e3342b174d9b0708c \
                    file://picobuild/vendored/pyparsing/LICENSE;md5=657a566233888513e1f07ba13e2f47f1 \
                    file://picobuild/vendored/tomli/LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5 \
                    "

SRC_URI = "git://gitlab.com/rossburton/picobuild.git;protocol=https;branch=main"
SRCREV = "ed3b16ce48d91df181e5f5d77b9bbc2577b3fd9d"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

inherit python_flit_core

DEPENDS:remove:class-native = "python3-picobuild-native"

BBCLASSEXTEND = "native nativesdk"
