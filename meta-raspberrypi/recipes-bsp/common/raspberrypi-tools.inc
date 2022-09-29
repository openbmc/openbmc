RPITOOLS_DATE ?= "20220711"
SRCREV ?= "439b6198a9b340de5998dd14a26a0d9d38a6bcac"
RPITOOLS_SRC_URI ?= "git://github.com/raspberrypi/tools;protocol=https;branch=master"
RPITOOLS_S ?= "${WORKDIR}/git"

SRC_URI = "${RPITOOLS_SRC_URI}"

PV = "${RPITOOLS_DATE}"

BB_GIT_SHALLOW = "1"

