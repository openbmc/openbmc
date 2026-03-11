SUMMARY = "JSON schema validator for JSON for Modern C++"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c441d022da1b1663c70181a32225d006"

SRC_URI = "git://github.com/pboettch/json-schema-validator;branch=main;protocol=https"
SRCREV = "349cba9f7e3cb423bbc1811bdd9f6770f520b468"

DEPENDS += "nlohmann-json"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DJSON_VALIDATOR_BUILD_TESTS=OFF -DJSON_VALIDATOR_BUILD_EXAMPLES=OFF"

BBCLASSEXTEND = "native nativesdk"
