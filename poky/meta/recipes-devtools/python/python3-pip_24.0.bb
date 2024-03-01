SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.org/project/pip"
SECTION = "devel/python"
LICENSE = "MIT & Apache-2.0 & MPL-2.0 & LGPL-2.1-only & BSD-3-Clause & PSF-2.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=63ec52baf95163b597008bb46db68030 \
                    file://src/pip/_vendor/cachecontrol/LICENSE.txt;md5=6572692148079ebbbd800be4b9f36c6d \
                    file://src/pip/_vendor/certifi/LICENSE;md5=3c2b7404369c587c3559afb604fce2f2 \
                    file://src/pip/_vendor/chardet/LICENSE;md5=4fbd65380cdd255951079008b364516c \
                    file://src/pip/_vendor/colorama/LICENSE.txt;md5=b4936429a56a652b84c5c01280dcaa26 \
                    file://src/pip/_vendor/distlib/LICENSE.txt;md5=f6a11430d5cd6e2cd3832ee94f22ddfc \
                    file://src/pip/_vendor/distro/LICENSE;md5=d2794c0df5b907fdace235a619d80314 \
                    file://src/pip/_vendor/idna/LICENSE.md;md5=239668a7c6066d9e0c5382e9c8c6c0e1 \
                    file://src/pip/_vendor/msgpack/COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751 \
                    file://src/pip/_vendor/packaging/LICENSE;md5=faadaedca9251a90b205c9167578ce91 \
                    file://src/pip/_vendor/packaging/LICENSE.APACHE;md5=2ee41112a44fe7014dce33e26468ba93 \
                    file://src/pip/_vendor/pkg_resources/LICENSE;md5=141643e11c48898150daa83802dbc65f \
                    file://src/pip/_vendor/platformdirs/LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a \
                    file://src/pip/_vendor/pygments/LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592 \
                    file://src/pip/_vendor/pyparsing/LICENSE;md5=657a566233888513e1f07ba13e2f47f1 \
                    file://src/pip/_vendor/pyproject_hooks/LICENSE;md5=aad69c93f605003e3342b174d9b0708c \
                    file://src/pip/_vendor/requests/LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
                    file://src/pip/_vendor/resolvelib/LICENSE;md5=78e1c0248051c32a38a7f820c30bd7a5 \
                    file://src/pip/_vendor/rich/LICENSE;md5=b5f0b94fbc94f5ad9ae4efcf8a778303 \
                    file://src/pip/_vendor/six.LICENSE;md5=43cfc9e4ac0e377acfb9b76f56b8415d \
                    file://src/pip/_vendor/tenacity/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
                    file://src/pip/_vendor/tomli/LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5 \
                    file://src/pip/_vendor/typing_extensions.LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2 \
                    file://src/pip/_vendor/urllib3/LICENSE.txt;md5=c2823cb995439c984fd62a973d79815c \
                    file://src/pip/_vendor/webencodings/LICENSE;md5=81fb24cd7823cce23b69f721993dce4d \
                    "

inherit pypi python_setuptools_build_meta

SRC_URI += "file://no_shebang_mangling.patch"

SRC_URI[sha256sum] = "ea9bd1a847e8c5774a5777bb398c19e80bcd4e2aa16a4b301b718fe6f593aba2"

do_install:append() {
    rm -f ${D}/${bindir}/pip
}

RDEPENDS:${PN} = "\
  python3-compile \
  python3-io \
  python3-html \
  python3-json \
  python3-multiprocessing \
  python3-netserver \
  python3-setuptools \
  python3-unixadmin \
  python3-xmlrpc \
  python3-pickle \
  python3-image \
"

BBCLASSEXTEND = "native nativesdk"

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
