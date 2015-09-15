require bash.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd5d9bcabd8ed5a54a01ce8d183d592a"

PR = "r11"

SRC_URI = "${GNU_MIRROR}/bash/bash-${PV}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-049;apply=yes;striplevel=0;name=patch049 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-050;apply=yes;striplevel=0;name=patch050 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-051;apply=yes;striplevel=0;name=patch051 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-052;apply=yes;striplevel=0;name=patch052 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-053;apply=yes;striplevel=0;name=patch053 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-054;apply=yes;striplevel=0;name=patch054 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-055;apply=yes;striplevel=0;name=patch055 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-056;apply=yes;striplevel=0;name=patch056 \
           ${GNU_MIRROR}/bash/bash-3.2-patches/bash32-057;apply=yes;striplevel=0;name=patch057 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://dont-include-target-CFLAGS-in-host-LDFLAGS.patch \
          "

SRC_URI[tarball.md5sum] = "338dcf975a93640bb3eaa843ca42e3f8"
SRC_URI[tarball.sha256sum] = "128d281bd5682ba5f6953122915da71976357d7a76490d266c9173b1d0426348"
SRC_URI[patch049.md5sum] = "af571a2d164d5abdcae4499e94e8892c"
SRC_URI[patch049.sha256sum] = "b1217ed94bdb95dc878fa5cabbf8a164435eb0d9da23a392198f48566ee34a2f"
SRC_URI[patch050.md5sum] = "8443d4385d73ec835abe401d90591377"
SRC_URI[patch050.sha256sum] = "081bb03c580ecee63ba03b40beb3caf509eca29515b2e8dd3c078503609a1642"
SRC_URI[patch051.md5sum] = "15c6653042e9814aa87120098fc7a849"
SRC_URI[patch051.sha256sum] = "354886097cd95b4def77028f32ee01e2e088d58a98184fede9d3ce9320e218ef"
SRC_URI[patch052.md5sum] = "691023a944bbb9003cc92ad462d91fa1"
SRC_URI[patch052.sha256sum] = "a0eccf9ceda50871db10d21efdd74b99e35efbd55c970c400eeade012816bb61"
SRC_URI[patch053.md5sum] = "eb97d1c9230a55283d9dac69d3de2e46"
SRC_URI[patch053.sha256sum] = "fe6f0e96e0b966eaed9fb5e930ca12891f4380f30f9e0a773d200ff2063a864e"
SRC_URI[patch054.md5sum] = "1107744058c43b247f597584b88ba0a6"
SRC_URI[patch054.sha256sum] = "c6dab911e85688c542ce75afc175dbb4e5011de5102758e19a4a80dac1e79359"
SRC_URI[patch055.md5sum] = "05d201176d3499e2dfa4a73d09d42f05"
SRC_URI[patch055.sha256sum] = "c0e816700837942ed548da74e5917f74b70cbbbb10c9f2caf73e8e06a0713d0a"
SRC_URI[patch056.md5sum] = "222eaa3a2c26f54a15aa5e08817a534a"
SRC_URI[patch056.sha256sum] = "063a8d8d74e4407bf07a32b965b8ef6d213a66abdb6af26cc3584a437a56bbb4"
SRC_URI[patch057.md5sum] = "47d98e3e042892495c5efe54ec6e5913"
SRC_URI[patch057.sha256sum] = "5fc689394d515990f5ea74e2df765fc6e5e42ca44b4591b2c6f9be4b0cadf0f0"

PARALLEL_MAKE = ""
