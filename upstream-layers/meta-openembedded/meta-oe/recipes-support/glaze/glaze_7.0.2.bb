SUMMARY = "Extremely fast, in memory, JSON and reflection library for modern C++. BEVE, CBOR, CSV, MessagePack, TOML, EETF "
HOMEPAGE = "https://stephenberry.github.io/glaze/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4d29875d83fbbf50485c846dbbbed8"

SRC_URI = "git://github.com/stephenberry/glaze;protocol=https;branch=main;tag=v${PV}"

SRCREV = "459946d325c497c274fa59d666bd7cb6e2dd7ad0"

inherit cmake

EXTRA_OECMAKE = "-Dglaze_BUILD_EXAMPLES=OFF -DBUILD_TESTING=OFF -Dglaze_ENABLE_FUZZING=OFF -Dglaze_DEVELOPER_MODE=OFF"

FILES:${PN}-dev += "${datadir}/${BPN}/*.cmake"

# Glaze is a header-only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native"
