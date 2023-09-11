SUMMARY = "JSON schema validator for JSON for Modern C++"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c441d022da1b1663c70181a32225d006"

SRC_URI = "git://github.com/pboettch/json-schema-validator;branch=main;protocol=https \
           file://0001-Set-Json_validator-Install-off-if-it-finds-it-via-li.patch \
           file://0002-Fix-assumed-signed-char.patch \
           file://0003-For-root-value-use-empty-pointer.patch \
           file://0004-cmake-Use-GNUInstallDirs.patch \
           "

SRCREV = "6b17782d6a5d1dee5d2c4fc5d25ffb1123913431"

S = "${WORKDIR}/git"

DEPENDS += "nlohmann-json"

inherit cmake
EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DJSON_VALIDATOR_BUILD_TESTS=OFF -DJSON_VALIDATOR_BUILD_EXAMPLES=OFF"
