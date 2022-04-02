RPITOOLS_DATE ?= "20211101"
SRCREV ?= "13474ee775d0c5ec8a7da4fb0a9fa84187abfc87"
RPITOOLS_SRC_URI ?= "git://github.com/raspberrypi/tools;protocol=https;branch=master"
RPITOOLS_S ?= "${WORKDIR}/git"

SRC_URI = "${RPITOOLS_SRC_URI}"

PV = "${RPITOOLS_DATE}"

BB_GIT_SHALLOW = "1"

