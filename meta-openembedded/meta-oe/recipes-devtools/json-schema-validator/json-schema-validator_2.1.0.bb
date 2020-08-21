SUMMARY = "JSON schema validator for JSON for Modern C++"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c441d022da1b1663c70181a32225d006"

SRC_URI = "git://github.com/pboettch/json-schema-validator"
SRCREV = "27fc1d094503623dfe39365ba82581507524545c"

S = "${WORKDIR}/git"

DEPENDS += "nlohmann-json"

inherit cmake
EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DBUILD_EXAMPLES=OFF -DBUILD_TESTS=OFF"

