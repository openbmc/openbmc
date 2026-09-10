SUMMARY = "Extremely fast, in memory, JSON and reflection library for modern C++. BEVE, CBOR, CSV, MessagePack, TOML, EETF "
HOMEPAGE = "https://stephenberry.github.io/glaze/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4d29875d83fbbf50485c846dbbbed8"

SRC_URI = "git://github.com/stephenberry/glaze;protocol=https;branch=main;tag=v${PV}"

SRCREV = "dce5e0f7ec572725ec369a0bc59a00eeb2270a9a"

inherit cmake

EXTRA_OECMAKE = " \
    -Dglaze_BUILD_EXAMPLES=OFF \
    -DBUILD_TESTING=OFF \
    -Dglaze_ENABLE_FUZZING=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${prefix} \
    -DDESTDIR=${D} \
"

# Glaze is a header-only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native"
