SUMMARY = "JSON schema validator for JSON for Modern C++"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c441d022da1b1663c70181a32225d006"

SRC_URI = "git://github.com/pboettch/json-schema-validator;branch=main;protocol=https;tag=${PV}"
SRCREV = "c780404a84dd9ba978ba26bc58d17cb43fa7bc80"

DEPENDS += "nlohmann-json"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DJSON_VALIDATOR_BUILD_TESTS=OFF -DJSON_VALIDATOR_BUILD_EXAMPLES=OFF"

BBCLASSEXTEND = "native nativesdk"
